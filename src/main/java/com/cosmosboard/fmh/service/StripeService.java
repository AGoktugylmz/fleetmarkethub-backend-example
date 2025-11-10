package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Subscription;
import com.cosmosboard.fmh.entity.SubscriptionTransaction;
import com.cosmosboard.fmh.exception.BadRequestException;
import com.cosmosboard.fmh.util.AppConstants;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.StripeObject;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionExpireParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {
    private static final BigDecimal DECIMAL_DIVISION = BigDecimal.valueOf(100);

    private static final String METADATA_TYPE = "type";

    private static final String METADATA_COMPANY_ID = "companyId";

    private static final String METADATA_SUBSCRIPTION_ID = "subscriptionId";

    private static final String METADATA_TRANSACTION_ID = "transactionId";


    private final SubscriptionTransactionService subscriptionTransactionService;

    private final SubscriptionService subscriptionService;

    private final UserService userService;

    private final CompanyService companyService;

    private final BlackbookCreditService blackbookCreditService;

    private final StripeEventLogService stripeEventLogService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Get customer from Stripe.
     *
     * @param subscription Subscription
     * @return Customer
     */
    @Transactional
    public Customer getCustomer(Subscription subscription) {
        String customerId = subscription.getCompany().getStripeCustomerId();
        if (customerId != null) {
            try {
                Customer customer = Customer.retrieve(customerId);
                if (customer != null && (customer.getDeleted() == null || !customer.getDeleted())) {
                    return customer;
                }
            } catch (StripeException e) {
                log.warn("[StripeService] Customer ID '{}' not found: {}", customerId, e.getMessage());
            }
        }

        try {
            Company company = subscription.getCompany();
            Customer customer = Customer.create(CustomerCreateParams.builder()
                .setName(company.getName())
                .setEmail(userService.getUser().getEmail())
                .putMetadata("companyId", company.getId())
                .build());

            log.info("[StripeService] Customer created: {}", customer);

            company.setStripeCustomerId(customer.getId());
            companyService.save(company);

            return customer;
        } catch (StripeException e) {
            log.error("[StripeService] Error creating customer: {}", e.getMessage());
            throw new BadRequestException("Error creating customer");
        }
    }

    /**
     * Create subscription on Stripe.
     *
     * @param subscription Subscription
     * @return Session
     */
    @Transactional
    public Session createSubscription(Subscription subscription) {
        Product proPackageProduct = getProduct(proPackageProductId);
        Price proPackagePrice = getPrice(proPackageProduct.getDefaultPrice());

        Product blackbookAddonProduct = getProduct(blackbookAddonProductId);
        Price blackbookAddonPrice = getPrice(blackbookAddonProduct.getDefaultPrice());

        BigDecimal amount = proPackagePrice.getUnitAmountDecimal();
        if (subscription.isBlackbookAddon()) {
            amount = amount.add(blackbookAddonPrice.getUnitAmountDecimal());
        }

        try {
            SubscriptionTransaction transaction = subscriptionTransactionService.create(
                subscription,
                userService.getUser(),
                AppConstants.PaymentProviderEnum.STRIPE,
                amount.divide(DECIMAL_DIVISION, RoundingMode.HALF_UP)
            );

            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            lineItems.add(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPrice(proPackagePrice.getId())
                    .build()
            );

            if (subscription.isBlackbookAddon()) {
                lineItems.add(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(blackbookAddonPrice.getId())
                        .build()
                );
            }

            log.info("[SubscriptionService] Create session on Stripe");

            Customer customer = getCustomer(subscription);
            SessionCreateParams.SubscriptionData.Builder subscriptionDataBuilder =
                SessionCreateParams.SubscriptionData.builder()
                    .putMetadata(METADATA_TYPE, AppConstants.PaymentTypeEnum.SUBSCRIPTION.name())
                    .putMetadata(METADATA_COMPANY_ID, subscription.getCompany().getId())
                    .putMetadata(METADATA_SUBSCRIPTION_ID, subscription.getId())
                    .putMetadata(METADATA_TRANSACTION_ID, transaction.getId());

            Session session = Session.create(new SessionCreateParams.Builder()
                .setCustomer(customer.getId())
                .setSuccessUrl(String.format("%s/subscription/checkout?status=success&subscription=%s&transaction=%s",
                    frontendUrl, subscription.getId(), transaction.getId()))
                .setCancelUrl(String.format("%s/subscription/checkout?status=cancel&subscription=%s&transaction=%s",
                    frontendUrl, subscription.getId(), transaction.getId()))
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addAllLineItem(lineItems)
                .setSubscriptionData(subscriptionDataBuilder.build())
                .setAllowPromotionCodes(true)
                .build());
            log.info("[SubscriptionService] Session created on Stripe: {}", session);

            transaction.setSessionId(session.getId());
            subscriptionTransactionService.save(transaction);

            return session;
        } catch (StripeException e) {
            log.error("[SubscriptionService] Error creating subscription on Stripe", e);
            throw new BadRequestException("Error creating subscription on Stripe");
        }
    }

    /**
     * Update subscription on Stripe.
     *
     * @param subscription Subscription
     * @return Subscription
     */
    @Transactional
    public Subscription updateSubscription(Subscription subscription) {
        com.stripe.model.Subscription stripeSubscription = getSubscription(subscription.getPaymentProviderId());

        Product blackbookAddonProduct = getProduct(blackbookAddonProductId);
        Price blackbookAddonPrice = getPrice(blackbookAddonProduct.getDefaultPrice());

        boolean hasBlackbookAddon = stripeSubscription.getItems().getData().stream().anyMatch(item ->
            item.getPrice().getId().equals(blackbookAddonPrice.getId()));

        SubscriptionUpdateParams.Item updateItem = null;
        if (subscription.isBlackbookAddon() && !hasBlackbookAddon) {
            updateItem = SubscriptionUpdateParams.Item.builder()
                .setPrice(blackbookAddonPrice.getId())
                .setQuantity(1L)
                .build();

            log.info("[SubscriptionService] Adding blackbook addon to subscription");
        } else if (!subscription.isBlackbookAddon() && hasBlackbookAddon) {
            SubscriptionItem itemToRemove = stripeSubscription.getItems().getData().stream()
                .filter(item -> item.getPrice().getId().equals(blackbookAddonPrice.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Blackbook item not found!"));

            updateItem = SubscriptionUpdateParams.Item.builder()
                .setId(itemToRemove.getId())
                .setDeleted(true)
                .build();
        }

        if (updateItem == null) {
            log.info("[SubscriptionService] No update needed for subscription");
            return subscription;
        }

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
            .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.NONE)
            .addItem(updateItem)
            .build();
        try {
            stripeSubscription.update(params);
        } catch (StripeException e) {
            log.error("[SubscriptionService] Error updating subscription: {}", e.getMessage());
            throw new BadRequestException("Error updating subscription: " + e.getMessage());
        }

        log.info("[SubscriptionService] Update subscription on Stripe: {}", stripeSubscription);

        return subscription;
    }

    /**
     * Create addon invoice.
     *
     * @param subscription Subscription
     * @return Session
     */
    @Transactional
    public Session createAddonPaymentSession(Subscription subscription) {
        try {
            Customer customer = getCustomer(subscription);
            Product blackbookAddonProduct = getProduct(blackbookAddonOneTimeProductId);
            Price blackbookAddonPrice = getPrice(blackbookAddonProduct.getDefaultPrice());

            BigDecimal amount = blackbookAddonPrice.getUnitAmountDecimal().divide(
                DECIMAL_DIVISION,
                RoundingMode.HALF_UP
            );

            SubscriptionTransaction transaction = subscriptionTransactionService.create(
                subscription,
                userService.getUser(),
                AppConstants.PaymentProviderEnum.STRIPE,
                amount
            );

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                .setCustomer(customer.getId())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(String.format("%s/subscription/checkout?status=success&subscription=%s&transaction=%s",
                    frontendUrl, subscription.getId(), transaction.getId()))
                .setCancelUrl(String.format("%s/subscription/checkout?status=cancel&subscription=%s&transaction=%s",
                    frontendUrl, subscription.getId(), transaction.getId()))
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPrice(blackbookAddonPrice.getId())
                        .setQuantity(1L)
                        .build()
                )
                .setAllowPromotionCodes(true)
                .putMetadata(METADATA_TYPE, AppConstants.PaymentTypeEnum.SUBSCRIPTION_ADDON.name())
                .putMetadata(METADATA_COMPANY_ID, subscription.getCompany().getId())
                .putMetadata(METADATA_SUBSCRIPTION_ID, subscription.getId())
                .putMetadata(METADATA_TRANSACTION_ID, transaction.getId())
                .build();

            Session session = Session.create(sessionParams);
            log.info("[StripeService] Session created on Stripe: {}", session);

            transaction.setSessionId(session.getId());
            subscriptionTransactionService.save(transaction);

            return session;
        } catch (StripeException e) {
            log.error("[StripeService] Error creating invoice: {}", e.getMessage());
            throw new BadRequestException("Error creating invoice");
        }
    }

    /**
     * Cancel subscription on Stripe.
     *
     * @param subscription Subscription
     */
    public void cancelSubscription(Subscription subscription) {
        com.stripe.model.Subscription stripeSubscription = getSubscription(subscription.getPaymentProviderId());

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
            .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.NONE)
            .setCancelAtPeriodEnd(true)
            .build();
        try {
            stripeSubscription.update(params);
            log.info("[StripeService] Cancel subscription on Stripe: {}", stripeSubscription);
        } catch (StripeException e) {
            throw new BadRequestException("Error updating subscription: " + e.getMessage());
        }
    }

    /**
     * Check if subscription is resumable.
     *
     * @param subscription Subscription
     * @return boolean
     */
    public boolean isResumableSubscription(Subscription subscription) {
        if (subscription.getPaymentProviderId() == null) {
            log.info("[StripeService] Subscription has no payment provider ID");
            return false;
        }

        com.stripe.model.Subscription stripeSubscription = getSubscription(subscription.getPaymentProviderId());

        return !stripeSubscription.getStatus().equalsIgnoreCase("canceled");
    }

    /**
     * Resume subscription on Stripe.
     *
     * @param subscription Subscription
     */
    public void resumeSubscription(Subscription subscription) {
        com.stripe.model.Subscription stripeSubscription = getSubscription(subscription.getPaymentProviderId());

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
            .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.NONE)
            .setCancelAtPeriodEnd(false)
            .build();
        try {
            stripeSubscription.update(params);
            log.info("[StripeService] Resume subscription on Stripe: {}", stripeSubscription);
        } catch (StripeException e) {
            throw new BadRequestException("Error resuming subscription: " + e.getMessage());
        }
    }

    /**
     * Expire session.
     *
     * @param id String
     */
    public void expireSession(String id) {
        try {
            Session resource = Session.retrieve(id);
            resource.expire(SessionExpireParams.builder().build());
        } catch (StripeException e) {
            log.error("[StripeService] Error canceling session: {}", e.getMessage());
        }
    }

    /**
     * Handle Stripe webhook events.
     *
     * @param payload   String
     * @param sigHeader String
     */
    @Transactional
    public void handleWebhookEvents(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, signingSecret);
            log.info("[Stripe Webhook] event: {}", event);
        } catch (SignatureVerificationException e) {
            log.error("[Stripe Webhook] signature error", e);
            throw new BadRequestException("Invalid Stripe signature");
        }

        log.info("[Stripe Webhook] Raw event data: {}", event.getData());
        log.info("[Stripe Webhook] DataObjectDeserializer: {}", event.getDataObjectDeserializer());

        stripeEventLogService.save(event.getType(), event.toJson());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            log.error("[Stripe Webhook] deserialization failed");
            throw new BadRequestException("Invalid Stripe event data");
        }

        log.info("[Stripe Webhook] stripeObject: {}", stripeObject);

        try {
            AppConstants.StripeEventTypeEnum eventType = AppConstants.StripeEventTypeEnum.getByValue(event.getType());

            switch (eventType) {
                case CHECKOUT_SESSION_COMPLETED:
                    log.info("[Stripe Webhook] checkout session completed");
                    Session session = (Session) stripeObject;

                    String type = session.getMetadata().get(METADATA_TYPE);
                    log.info("[Stripe Webhook] checkout session completed - Type: {}", type);

                    if (type.equalsIgnoreCase(AppConstants.PaymentTypeEnum.SUBSCRIPTION_ADDON.name())) {
                        String subscriptionId = session.getMetadata().get(METADATA_SUBSCRIPTION_ID);
                        log.info("[Stripe Webhook] checkout session completed - Subscription ID: {}", subscriptionId);

                        String transactionId = session.getMetadata().get(METADATA_TRANSACTION_ID);
                        log.info("[Stripe Webhook] checkout session completed - Transaction ID: {}", transactionId);

                        // Update subscription transaction
                        updateSubscriptionTransaction(transactionId, event);

                        // Add blackbook credit
                        Subscription subscription = subscriptionService.findById(subscriptionId);
                        blackbookCreditService.add(subscription.getCompany(),
                            Long.parseLong(blackbookAddonMonthlyCredit));
                    }

                    break;
                case INVOICE_PAID:
                    log.info("[Stripe Webhook] Invoice paid");
                    break;
                case INVOICE_PAYMENT_SUCCEEDED:
                    log.info("[Stripe Webhook] Invoice payment succeeded");
                    invoicePaymentSucceeded((Invoice) stripeObject, event);
                    break;
                case INVOICE_PAYMENT_FAILED:
                    log.info("[Stripe Webhook] Invoice payment failed");
                    invoicePaymentSucceeded((Invoice) stripeObject, event);
                    break;
                case CUSTOMER_SUBSCRIPTION_CREATED:
                    log.info("[Stripe Webhook] Customer subscription created");
                    subscriptionUpdate((com.stripe.model.Subscription) stripeObject);
                    break;
                case CUSTOMER_SUBSCRIPTION_UPDATED:
                    log.info("[Stripe Webhook] Customer subscription updated");
                    subscriptionUpdate((com.stripe.model.Subscription) stripeObject);
                    break;
                case CUSTOMER_SUBSCRIPTION_DELETED:
                    log.info("[Stripe Webhook] Customer subscription deleted");
                    subscriptionUpdate((com.stripe.model.Subscription) stripeObject);
                    break;
                case CHARGE_UPDATED:
                    log.info("[Stripe Webhook] Charge updated");
                    break;
                case TEST_CLOCK_ADVANCING:
                    log.info("[Stripe Webhook] Test clock advancing");
                    break;
                case TEST_CLOCK_READY:
                    log.info("[Stripe Webhook] Test clock ready");
                    break;
                default:
                    log.info("[Stripe Webhook] unhandled event type: {}", event.getType());
                    break;
            }
        } catch (Exception e) {
            log.error("[Stripe Webhook] error: {}", e.getMessage());
            log.info("[Stripe Webhook] unhandled event type: {}", event.getType());
        }
    }

    /**
     * Get product from Stripe.
     *
     * @param productId String
     * @return Product
     */
    private Product getProduct(String productId) {
        try {
            return Product.retrieve(productId);
        } catch (StripeException e) {
            log.error("[StripeService] Error retrieving product from Stripe: {}", e.getMessage());
            throw new BadRequestException("Error retrieving product from Stripe");
        }
    }

    /**
     * Get price from Stripe.
     *
     * @param priceId String
     * @return Price
     */
    private Price getPrice(String priceId) {
        try {
            return Price.retrieve(priceId);
        } catch (StripeException e) {
            log.error("[StripeService] Error retrieving price from Stripe: {}", e.getMessage());
            throw new BadRequestException("Error retrieving price from Stripe");
        }
    }

    /**
     * Get subscription from Stripe.
     *
     * @param subscriptionId String
     * @return com.stripe.model.Subscription
     */
    private com.stripe.model.Subscription getSubscription(String subscriptionId) {
        try {
            return com.stripe.model.Subscription.retrieve(subscriptionId);
        } catch (StripeException e) {
            log.error("[StripeService] Error retrieving subscription from Stripe: {}", e.getMessage());
            throw new BadRequestException("Error retrieving subscription from Stripe");
        }
    }

    /**
     * Invoice payment succeeded.
     *
     * @param stripeObject com.stripe.model.Invoice
     * @param event        com.stripe.model.Event
     */
    private void invoicePaymentSucceeded(Invoice stripeObject, Event event) {
        log.info("[Stripe Webhook] Invoice: {}", stripeObject);

        if (stripeObject.getBillingReason().equalsIgnoreCase("subscription_create") ||
            stripeObject.getBillingReason().equalsIgnoreCase("subscription_cycle")) {
            log.info("[Stripe Webhook] Invoice billing reason: subscription_create");

            stripeObject.getLines().getData().forEach(line -> {
                String stripeSubscriptionId = line.getSubscription();
                log.info("[Stripe Webhook] Stripe subscription ID: {}", stripeSubscriptionId);

                String type = line.getMetadata().get(METADATA_TYPE);
                log.info("[Stripe Webhook] Type: {}", type);

                String subscriptionId = line.getMetadata().get(METADATA_SUBSCRIPTION_ID);
                log.info("[Stripe Webhook] Subscription ID: {}", subscriptionId);

                String transactionId = line.getMetadata().get(METADATA_TRANSACTION_ID);
                log.info("[Stripe Webhook] Transaction ID: {}", transactionId);

                // Update subscription transaction
                updateSubscriptionTransaction(transactionId, event);

                // Update subscription
                Subscription subscription = subscriptionService.findById(subscriptionId);
                subscription.setLastPaymentDate(LocalDateTime.now());
                subscription.setStatus(AppConstants.SubscriptionStatusEnum.ACTIVE);

                Product product = getProduct(line.getPricing().getPriceDetails().getProduct());
                log.info("[Stripe Webhook] Product: {}", product);

                if (product.getId().equals(blackbookAddonProductId)) {
                    log.info("[Stripe Webhook] Blackbook addon product");
                    subscription.setBlackbookAddon(true);
                    // Add blackbook credit
                    blackbookCreditService.add(subscription.getCompany(), Long.parseLong(blackbookAddonMonthlyCredit));
                } else {
                    log.info("[Stripe Webhook] Non-blackbook product");
                }

                subscriptionService.save(subscription);
            });
        } else {
            log.info("[Stripe Webhook] Invoice billing reason: {}", stripeObject.getBillingReason());
        }
    }

    /**
     * Update subscription transaction.
     *
     * @param transactionId String
     * @param event         com.stripe.model.Event
     */
    private void updateSubscriptionTransaction(String transactionId, Event event) {
        SubscriptionTransaction transaction = subscriptionTransactionService.findById(transactionId);
        transaction.setStatus(AppConstants.TransactionStatusEnum.SUCCESS);
        transaction.setResult(event.toJson());
        subscriptionTransactionService.save(transaction);
    }

    /**
     * Update subscription.
     *
     * @param stripeSubscription com.stripe.model.Subscription
     */
    private void subscriptionUpdate(com.stripe.model.Subscription stripeSubscription) {
        SubscriptionItem subscriptionItem = stripeSubscription.getItems().getData().get(0);
        String subscriptionId = stripeSubscription.getMetadata().get(METADATA_SUBSCRIPTION_ID);
        Subscription subscription = subscriptionService.findById(subscriptionId);
        subscription.setStartDate(LocalDateTime.ofInstant(
            Instant.ofEpochSecond(subscriptionItem.getCurrentPeriodEnd()), TimeZone.getDefault().toZoneId()));
        subscription.setEndDate(LocalDateTime.ofInstant(
            Instant.ofEpochSecond(subscriptionItem.getCurrentPeriodEnd()),
            TimeZone.getDefault().toZoneId()));
        subscription.setPaymentProviderId(stripeSubscription.getId());

        if (stripeSubscription.getStatus() != null) {
            try {
                AppConstants.StripeSubscriptionStatusTypeEnum status =
                    AppConstants.StripeSubscriptionStatusTypeEnum.getByValue(stripeSubscription.getStatus());
                switch (status) {
                    case ACTIVE:
                        // subscription.setCanceledAt(null);
                        // subscription.setStatus(AppConstants.SubscriptionStatusEnum.ACTIVE);
                        break;
                    case CANCELED:
                    case PAUSED:
                    case PAST_DUE:
                    case UNPAID:
                        subscription.setCanceledAt(LocalDateTime.now());
                        subscription.setStatus(AppConstants.SubscriptionStatusEnum.CANCELED);
                        break;
                    default:
                        log.warn("[StripeService] Unknown subscription status: {}", status);
                        break;
                }
            } catch (IllegalArgumentException e) {
                log.error("[StripeService] Error getting subscription status: {}", e.getMessage());
            }
        }

        subscriptionService.save(subscription);
    }
}
