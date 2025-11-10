package com.cosmosboard.fmh.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class AppConstants {
    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "Bearer";

    public static final String PICTURES_PATH = "pictures";

    public static final String FILES_PATH = "files";

    public static final String AVATARS_PATH = String.format("%s/avatars", PICTURES_PATH);

    public static final String IMAGES_PATH = String.format("%s/images", PICTURES_PATH);

    public static final int AVATAR_SIZE = 620;

    public static final int RANDOM_FILENAME_LENGTH = 32;

    public static final int EMAIL_ACTIVATION_TOKEN_LENGTH = 64;

    public static final int GSM_ACTIVATION_TOKEN_LENGTH = 6;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String STRIPE_SIGNATURE_HEADER = "Stripe-Signature";

    public static final String REVENUECAT_SIGNATURE_HEADER = "X-RevenueCat-Signature";

    private AppConstants() {
    }

    public enum EntitySortEnum {
        UP,
        DOWN
    }

    public enum RoleEnum {
        USER,
        CONSULTANT,
        ADMIN;

        public static RoleEnum getByName(String name) {
            for (RoleEnum item : RoleEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid role: %s", name));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum LanguageEnum {
        TR("tr"),
        EN("en");

        private final String lang;

        public static LanguageEnum getByName(String name) {
            for (LanguageEnum item : LanguageEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid language: %s", name));
        }
    }

    public enum CarStatusEnum {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELLED_REQUESTED,
        CANCELLED,
        FINISHED;

        public static CarStatusEnum getByName(String name) {
            for (CarStatusEnum item : CarStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid car status: %s", name));
        }
    }

    public enum ImageStatusEnum {
        ACTIVE,
        PASSIVE;

        public static ImageStatusEnum getByName(String name) {
            for (ImageStatusEnum item : ImageStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid image status: %s", name));
        }
    }

    public enum OfferStatusEnum {
        WAITING,
        ACCEPTED,
        REJECTED,
        CANCELLED,
        COMPLETED,
        FINISHED;

        public static OfferStatusEnum getByName(String name) {
            for (OfferStatusEnum item : OfferStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid operation status : %s", name));
        }

        public static OfferStatusEnum getByNameOptional(String name) {
            try {
                return getByName(name);
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }
    }

    public enum OfferConversationStatusEnum {
        UNREAD,
        READ
    }

    public enum NotificationTypeEnum {
        GENERAL,
        CAR,
        OFFER,
        REQUEST
    }

    public enum NotificationStatusEnum {
        UNREAD,
        READ
    }

    public enum CompanyStatusEnum {
        WAITING,
        APPROVED,
        REJECTED;

        public static CompanyStatusEnum getByName(String name) {
            for (CompanyStatusEnum item : CompanyStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid company status: %s", name));
        }
    }

    public enum OutsourceTokenPermissionEnum {
        CAR_LIST,
        CAR_ADD,
        CAR_UPDATE;

        public static OutsourceTokenPermissionEnum getByName(String name) {
            for (OutsourceTokenPermissionEnum item : values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid permission: %s", name));
        }
    }

    public enum Operant {
        AND,
        OR
    }

    public enum MarketValueProviderEnum {
        MANHEIM,
        BLACKBOOK;

        public static MarketValueProviderEnum getByName(String name) {
            for (MarketValueProviderEnum item : MarketValueProviderEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(String.format("Invalid MarketValueProviderEnum : %s", name));
        }
    }

    public enum MarketValueBatchResultEnum {
        READY,
        PROCESSING,
        SUCCESS,
        FAILED;

        public static MarketValueBatchResultEnum getByName(String name) {
            for (MarketValueBatchResultEnum item : MarketValueBatchResultEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid MarketValueBatchResultEnum type: " + name);
        }
    }

    public enum MarketValueBatchItemStatusEnum {
        WAITING,
        SUCCESS,
        FAILED;

        public static MarketValueBatchItemStatusEnum getByName(String name) {
            for (MarketValueBatchItemStatusEnum item : MarketValueBatchItemStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid MarketValueBatchItemStatusEnum type: " + name);
        }
    }

    public enum TransactionStatusEnum {
        WAITING,
        SUCCESS,
        FAILED,
        REFUNDED,
        CANCELLED;

        public static TransactionStatusEnum getByName(String name) {
            for (TransactionStatusEnum item : TransactionStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid TransactionStatusEnum type: " + name);
        }
    }

    public enum PaymentProviderEnum {
        STRIPE,
        REVENUECAT;

        public static PaymentProviderEnum getByName(String name) {
            for (PaymentProviderEnum item : PaymentProviderEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid PaymentProviderEnum type: " + name);
        }
    }

    public enum PaymentTypeEnum {
        SUBSCRIPTION,
        SUBSCRIPTION_ADDON;

        public static PaymentTypeEnum getByName(String name) {
            for (PaymentTypeEnum item : PaymentTypeEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid PaymentTypeEnum type: " + name);
        }
    }

    @Getter
    public enum RegionType {
        NA("National"),
        SE("Southeast"),
        NE("Northeast"),
        MW("Midwest"),
        SW("Southwest"),
        WC("West Coast");

        private final String displayName;

        RegionType(String displayName) {
            this.displayName = displayName;
        }

        public static RegionType getByName(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Region name is empty or null");
            }

            String normalized = normalize(name);

            for (RegionType type : values()) {
                if (normalize(type.name()).equals(normalized) || normalize(type.displayName).equals(normalized)) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Invalid region type: " + name);
        }

        public static RegionType defaultRegion() {
            return NA;
        }

        private static String normalize(String input) {
            return input.trim().toUpperCase().replaceAll("[^A-Z]", "");
        }
    }

    public enum BlackBookCondition {
        XCLEAN,
        CLEAN,
        AVG,
        ROUGH;

        public static BlackBookCondition getByName(String name) {
            for (BlackBookCondition item : BlackBookCondition.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid BlackBookCondition type: " + name);
        }
    }

    public enum SubscriptionStatusEnum {
        ACTIVE,
        CANCELED,
        EXPIRED,
        FAILED,
        PENDING;

        public static SubscriptionStatusEnum getByName(String name) {
            for (SubscriptionStatusEnum item : SubscriptionStatusEnum.values()) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid subscription status: " + name);
        }
    }

    @RequiredArgsConstructor
    public enum StripeEventTypeEnum {
        PRODUCT_CREATED("product.created"),
        PRICE_CREATED("price.created"),
        CHARGE_SUCCEEDED("charge.succeeded"),
        PAYMENT_INTENT_CREATED("payment_intent.created"),
        PAYMENT_INTENT_SUCCEEDED("payment_intent.succeeded"),
        PAYMENT_INTENT_PAYMENT_CREATED("payment_intent.payment_created"),
        CHECKOUT_SESSION_COMPLETED("checkout.session.completed"),
        CHECKOUT_SESSION_EXPIRED("checkout.session.expired"),
        INVOICE_CREATED("invoice.created"),
        INVOICE_FINALIZED("invoice.finalized"),
        INVOICE_UPCOMING("invoice.upcoming"),
        INVOICE_UPDATED("invoice.updated"),
        INVOICE_PAID("invoice.paid"),
        INVOICE_PAYMENT_SUCCEEDED("invoice.payment_succeeded"),
        INVOICE_PAYMENT_FAILED("invoice.payment_failed"),
        INVOICE_PAYMENT_ACTION_REQUIRED("invoice.payment_action_required"),
        CUSTOMER_CREATED("customer.created"),
        CUSTOMER_UPDATED("customer.updated"),
        CUSTOMER_DELETED("customer.deleted"),
        PLAN_CREATED("plan.created"),
        PLAN_UPDATED("plan.updated"),
        PLAN_DELETED("plan.deleted"),
        PAYMENT_METHOD_ATTACHED("payment_method.attached"),
        CUSTOMER_SUBSCRIPTION_CREATED("customer.subscription.created"),
        CUSTOMER_SUBSCRIPTION_UPDATED("customer.subscription.updated"),
        CUSTOMER_SUBSCRIPTION_DELETED("customer.subscription.deleted"),
        CHARGE_UPDATED("charge.updated"),
        TEST_CLOCK_ADVANCING("test_helpers.test_clock.advancing"),
        TEST_CLOCK_READY("test_helpers.test_clock.ready");

        private final String value;

        public static StripeEventTypeEnum getByValue(String value) {
            for (StripeEventTypeEnum item : StripeEventTypeEnum.values()) {
                if (item.value.equalsIgnoreCase(value)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid event value: " + value);
        }
    }

    @RequiredArgsConstructor
    public enum StripeSubscriptionStatusTypeEnum {
        ACTIVE("active"),
        TRIALING("trialing"),
        INCOMPLETE("incomplete"),
        INCOMPLETE_EXPIRED("incomplete_expired"),
        PAST_DUE("past_due"),
        CANCELED("canceled"),
        UNPAID("unpaid"),
        PAUSED("paused"),
        ALL("all");

        private final String value;

        public static StripeSubscriptionStatusTypeEnum getByValue(String value) {
            for (StripeSubscriptionStatusTypeEnum item : StripeSubscriptionStatusTypeEnum.values()) {
                if (item.value.equalsIgnoreCase(value)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid event value: " + value);
        }
    }

    /**
     * RevenueCat event types
     */
    @RequiredArgsConstructor
    public enum RevenueCatEventTypeEnum {
        INITIAL_PURCHASE("INITIAL_PURCHASE"),
        NON_RENEWING_PURCHASE("NON_RENEWING_PURCHASE"),
        RENEWAL("RENEWAL"),
        CANCELLATION("CANCELLATION"),
        UNCANCELLATION("UNCANCELLATION"),
        BILLING_ISSUE("BILLING_ISSUE"),
        PRODUCT_CHANGE("PRODUCT_CHANGE"),
        EXPIRATION("EXPIRATION"),
        TRANSFER("TRANSFER"),
        TEST("TEST");

        private final String value;

        public static RevenueCatEventTypeEnum getByValue(String value) {
            for (RevenueCatEventTypeEnum item : RevenueCatEventTypeEnum.values()) {
                if (item.value.equalsIgnoreCase(value)) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid RevenueCat event value: " + value);
        }
    }
}
