package com.cosmosboard.fmh.service;

import com.cosmosboard.fmh.dto.request.contactus.ContactUsRequest;
import com.cosmosboard.fmh.entity.Car;
import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Employee;
import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.Request;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.UserInvite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class EmailService {
    private static final String NAME = "name";

    private static final String LAST_NAME = "lastName";

    private static final String URL = "url";

    private static final String UTF8 = "UTF-8";

    private static final String STATUS = "status";

    private static final String FULL_NAME = "fullName";

    private static final String LINK = "link";

    private static final String OFFER_PRICE = "offerPrice";

    private static final String VIN = "vin";

    private static final String FLEET_MARKET= "https://fleetmarkethub.com";

    private final String appName;

    private final String appUrl;

    private final String frontendUrl;

    private final String senderAddress;

    private final MessageSourceService messageSourceService;

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    /**
     * Email service constructor.
     *
     * @param appName              String name of the application name
     * @param appUrl               String url of the application url
     * @param frontendUrl          String url of the frontend url
     * @param senderAddress        String email address of the sender
     * @param messageSourceService MessageSourceService
     * @param mailSender           JavaMailSender
     * @param templateEngine       SpringTemplateEngine
     */
    public EmailService(
        @Value("${spring.application.name}") String appName,
        @Value("${app.url}") String appUrl,
        @Value("${app.frontend-url}") String frontendUrl,
        @Value("${app.mail.username}") String senderAddress,
        MessageSourceService messageSourceService,
        JavaMailSender mailSender,
        SpringTemplateEngine templateEngine
    ) {
        this.appName = appName;
        this.appUrl = appUrl;
        this.frontendUrl = frontendUrl;
        this.senderAddress = senderAddress;
        this.messageSourceService = messageSourceService;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send the user an email activation link.
     *
     * @param user User
     */
    public void sendUserEmailActivation(User user) {
        try {
            log.info(String.format("[EmailService] Sending activation e-mail: %s - %s - %s",
                user.getId(), user.getEmail(), user.getEmailActivationToken()));

            String url = String.format("%s/activate-email/%s", "https://fmh.cosmosboard.com", user.getEmailActivationToken());

            final Context ctx = createContext();
            ctx.setVariable(NAME, user.getName());
            ctx.setVariable(LAST_NAME, user.getLastName());
            ctx.setVariable(FULL_NAME, user.getFullName());
            ctx.setVariable(URL, url);
            ctx.setVariable(LINK, FLEET_MARKET);

            String subject = messageSourceService.get("email_activation");
            send(new InternetAddress(senderAddress, appName), new InternetAddress(user.getEmail(), user.getName()),
                subject, templateEngine.process("mail/user-email-activation", ctx));

            log.info(String.format("[EmailService] Sent activation e-mail: %s - %s", user.getId(), user.getEmail()));
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error(String.format("[EmailService] Failed to send activation e-mail: %s", e.getMessage()));
        }
    }

    public void sendWelcomeEmail(User user) {
        try {
            log.info(String.format("[EmailService] Sending welcome e-mail: %s - %s", user.getId(), user.getEmail()));
            String meetingLink = "https://calendly.com/fcaus/maxrev";

            final Context ctx = createContext();
            ctx.setVariable("fullName", user.getFullName());
            ctx.setVariable("meetingLink", meetingLink);

            String subject = messageSourceService.get("welcome_email_subject");
            sendWithBbc(new InternetAddress(senderAddress, appName, UTF8),
                    new InternetAddress(user.getEmail(), user.getFullName()),
                    subject,
                    templateEngine.process("mail/welcome", ctx));

            log.info(String.format("[EmailService] Sent welcome e-mail: %s - %s", user.getId(), user.getEmail()));
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error(String.format("[EmailService] Failed to send welcome e-mail: %s", e.getMessage()), e);
        }
    }

    /**
     * Send user email invitation link.
     *
     * @param user User
     */
    public void sendUserInvitation(User user, UserInvite userInvite) {
        try {
            log.info("[EmailService] Sending invitation e-mail from: {}, to:{}", user.getId(), userInvite.getUserTo());

            String url = String.format("%s/auth/accept-invitation/%s", frontendUrl, userInvite.getId());

            final Context ctx = createContext();
            ctx.setVariable(NAME, user.getName());
            ctx.setVariable(LAST_NAME, user.getLastName());
            ctx.setVariable(FULL_NAME, user.getFullName());
            ctx.setVariable(URL, url);
            ctx.setVariable(LINK, FLEET_MARKET);

            String subject = messageSourceService.get("you_are_invited_a_company");
            send(new InternetAddress(senderAddress, appName), new InternetAddress(user.getEmail(), user.getName()),
                    subject, templateEngine.process("mail/user-company-invitation.html", ctx));

            log.info("[EmailService] Sent invitation e-mail: from {} to {}", user.getId(), userInvite.getUserTo());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send invitation e-mail: {}", e.getMessage());
        }
    }

    /**
     * Send offer accepted notification email.
     *
     * @param offer Offer
     */
    public void sendOfferAcceptedEmail(Offer offer) {
        try {
            log.info("[EmailService] Sending offer accepted e-mail for offer ID: {}", offer.getId());

            Company company = offer.getCompany();
            List<User> owners = company.getEmployees().stream()
                    .filter(Employee::isOwner)
                    .map(Employee::getUser)
                    .toList();

            for (User owner : owners) {
                final Context ctx = createContext();
                ctx.setVariable(NAME, owner.getName());
                ctx.setVariable(LAST_NAME, owner.getLastName());
                ctx.setVariable("offerId", offer.getId());
                ctx.setVariable(STATUS, offer.getStatus());
                ctx.setVariable(OFFER_PRICE, "$" + NumberFormat.getNumberInstance(Locale.US).format(offer.getPrice()));
                ctx.setVariable(VIN, offer.getCar().getVin());
                ctx.setVariable(LINK, FLEET_MARKET);

                String subject = messageSourceService.get("offer_accepted_email_subject");
                String content = templateEngine.process("mail/offer-accepted.html", ctx);

                send(new InternetAddress(senderAddress, appName), new InternetAddress(owner.getEmail(), owner.getName()),
                        subject, content);
            }

            log.info("[EmailService] Sent offer accepted e-mails for offer ID: {}", offer.getId());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send offer accepted e-mails: {}", e.getMessage());
        }
    }

    /**
     * Send the user offer change status information.
     *
     * @param offer offer
     */
    public void sendOfferStatusEmail(Offer offer) {
        try {
            log.info("[EmailService] Sending {} offer e-mail for offer ID: {}", offer.getStatus(), offer.getId());

            Company company = offer.getCompany();
            List<User> owners = company.getEmployees().stream()
                    .filter(Employee::isOwner)
                    .map(Employee::getUser)
                    .toList();

            for (User owner : owners) {
                final Context ctx = createContext();
                ctx.setVariable(NAME, owner.getName());
                ctx.setVariable(LAST_NAME, owner.getLastName());
                ctx.setVariable("offerId", offer.getId());
                ctx.setVariable(STATUS, offer.getStatus());
                ctx.setVariable(OFFER_PRICE, "$" + NumberFormat.getNumberInstance(Locale.US).format(offer.getPrice()));
                ctx.setVariable(VIN, offer.getCar().getVin());
                ctx.setVariable(LINK, FLEET_MARKET);

                String subjectKey = switch (offer.getStatus()) {
                    case ACCEPTED -> "offer_accepted_email_subject";
                    case REJECTED -> "offer_rejected_email_subject";
                    default -> null;
                };

                String template = switch (offer.getStatus()) {
                    case ACCEPTED -> "mail/offer-accepted.html";
                    case REJECTED -> "mail/offer-rejected.html";
                    default -> null;
                };

                if (subjectKey == null) {
                    log.warn("[EmailService] No email template or subject defined for status: {}", offer.getStatus());
                    continue;
                }

                String subject = messageSourceService.get(subjectKey);
                String content = templateEngine.process(template, ctx);

                send(new InternetAddress(senderAddress, appName), new InternetAddress(owner.getEmail(), owner.getName()),
                        subject, content);
            }

            log.info("[EmailService] Sent {} offer e-mails for offer ID: {}", offer.getStatus(), offer.getId());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send offer e-mails: {}", e.getMessage());
        }
    }

    /**
     * Send reset password email.
     *
     * @param user  User
     * @param token String
     */
    public void sendResetPasswordEmail(User user, String token) {
        try {
            log.info("[EmailService] Sending password reset email to: {}", user.getEmail());
            String url = String.format("%s/change-password/%s", "https://fmh.cosmosboard.com", token);

            final Context ctx = createContext();
            ctx.setVariable(NAME, user.getName());
            ctx.setVariable(LAST_NAME, user.getLastName());
            ctx.setVariable(URL, url);
            ctx.setVariable(LINK, FLEET_MARKET);

            String subject = messageSourceService.get("password_change");
            send(new InternetAddress(senderAddress, appName), new InternetAddress(user.getEmail()), subject,
                    templateEngine.process("mail/reset-password", ctx));

            log.info("[EmailService] Sent password reset email to: {}", user.getEmail());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send password reset email: {}", e.getMessage());
        }
    }

    /**
     * Send change password success email.
     *
     * @param user User
     */
    public void sendChangePasswordSuccess(User user) {
        try {
            log.info("[EmailService] Sending change password successfully e-mail for user ID: {}", user.getId());

            final Context ctx = createContext();
            ctx.setVariable(NAME, user.getName());
            ctx.setVariable(LAST_NAME, user.getLastName());
            ctx.setVariable(LINK, FLEET_MARKET);

            String subject = messageSourceService.get("password_changed_success");
            String content = templateEngine.process("mail/password-changed-success.html", ctx);

            send(new InternetAddress(senderAddress, appName), new InternetAddress(user.getEmail(), user.getName()),
                    subject, content);
            log.info("[EmailService] Sent change password successfully  e-mail for user ID: {}", user.getId());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send car accepted e-mail: {}", e.getMessage());
        }
    }

    /**
     * Send a "Contact Us" email to the support team with the details provided in the contact request.
     * This method processes the user's contact information and sends it via email.
     *
     * @param request The contact us request containing the user's full name, email address, phone number, and message.
     */
    public void sendContactUsEmail(ContactUsRequest request) {
        try {
            log.info("[EmailService] Sending contact us email: {} - {}", request.getFullName(), request.getEmailAddress());

            final Context ctx = new Context();
            ctx.setVariable(FULL_NAME, request.getFullName());
            ctx.setVariable("emailAddress", request.getEmailAddress());
            ctx.setVariable("phone", request.getPhone());
            ctx.setVariable("message", request.getMessage());
            ctx.setVariable(LINK, FLEET_MARKET);

            String subject = "New Contact Request";
            String body = templateEngine.process("mail/contact-us", ctx);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF8);

            helper.setFrom(new InternetAddress(senderAddress, appName, UTF8));
            helper.setTo(new InternetAddress("info@fleetmarkethub.com", "Support Team", UTF8));
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);

            log.info("[EmailService] Contact us email successfully sent.");
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send contact us email: {}", e.getMessage());
        }
    }

    /**
     * Sends a message email to the recipient with the specified title and content.
     *
     * @param recipient The recipient of the message.
     * @param title The title of the message.
     * @param content The content of the message.
     * @param fromCompany The company sending the message.
     */
    public void sendMessageEmail(User recipient, String title, String content, Company fromCompany) {
        try {
            final Context ctx = createContext();
            ctx.setVariable("recipientName", recipient.getName());
            ctx.setVariable("companyName", fromCompany.getName());
            ctx.setVariable("messageTitle", title);
            ctx.setVariable("messageContent", content);
            ctx.setVariable(LINK, FLEET_MARKET);

            String subject = messageSourceService.get("New Message");
            String emailContent = templateEngine.process("mail/new-message.html", ctx);

            send(new InternetAddress(senderAddress, appName),
                    new InternetAddress(recipient.getEmail(), recipient.getName()),
                    subject, emailContent);

            log.info("[EmailService] Sent message notification email to {}", recipient.getEmail());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send message email to {}: {}", recipient.getEmail(), e.getMessage());
        }
    }

    /**
     * Sends an email notification about a new offer creation to all employees of the car owner company.
     *
     * @param carOwnerCompany The company that owns the car.
     * @param offerCompany The company that created the offer.
     * @param car The car being offered.
     * @param price The price of the offer.
     */
    public void sendOfferCreatedEmail(Company carOwnerCompany, Company offerCompany, Car car, BigDecimal price) {
        List<User> employees = carOwnerCompany.getEmployees().stream()
                .map(Employee::getUser)
                .toList();

        for (User employee : employees) {
            try {
                final Context ctx = createContext();
                ctx.setVariable("Name", employee.getName());
                ctx.setVariable("offerCompanyName", offerCompany.getName());
                ctx.setVariable("carDetails", car.getModelYear() + " " + car.getModel().getBrand().getName() + " " + car.getModel().getName());

                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
                String formattedPrice = currencyFormatter.format(price);
                ctx.setVariable(OFFER_PRICE, formattedPrice);

                ctx.setVariable(VIN, car.getVin());
                ctx.setVariable(LINK, FLEET_MARKET);

                String subject = messageSourceService.get("Offer Created");
                String emailContent = templateEngine.process("mail/offer-created.html", ctx);

                send(new InternetAddress(senderAddress, appName),
                        new InternetAddress(employee.getEmail(), employee.getName()),
                        subject, emailContent);

                log.info("[EmailService] Sent offer creation email to {}", employee.getEmail());
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("[EmailService] Failed to send offer creation email to {}: {}", employee.getEmail(), e.getMessage());
            }
        }
    }

    /**
     * Sends an email notification about a new request creation to all employees of the company.
     *
     * @param company The company that created the request.
     * @param request The request that was created.
     */
    public void sendRequestCreatedEmail(Company company, Request request) {
        List<User> employees = company.getEmployees().stream()
                .map(Employee::getUser)
                .toList();

        for (User employee : employees) {
            try {
                final Context ctx = createContext();
                ctx.setVariable("recipient", employee.getName());
                ctx.setVariable("companyName", company.getName());
                ctx.setVariable("requestTitle", request.getTitle());
                ctx.setVariable("requestId", request.getId());
                ctx.setVariable("requestDetail", request.getText());
                ctx.setVariable(LINK, FLEET_MARKET);

                String subject = messageSourceService.get("Request Created");
                String emailContent = templateEngine.process("mail/request-created.html", ctx);

                send(new InternetAddress(senderAddress, appName),
                        new InternetAddress(employee.getEmail(), employee.getName()),
                        subject, emailContent);

                log.info("[EmailService] Sent request creation email to {}", employee.getEmail());
            } catch (UnsupportedEncodingException | MessagingException e) {
                log.error("[EmailService] Failed to send request creation email to {}: {}", employee.getEmail(), e.getMessage());
            }
        }
    }

    /**
     * Sends an email notification about the price change of a car to the user.
     *
     * @param user The user who is being notified.
     * @param oldPrice The old price of the car.
     * @param car The car whose price has changed.
     */
    public void sendCarPriceChangeEmail(User user, Float oldPrice, Car car) {
        try {
            final Context ctx = createContext();
            ctx.setVariable("recipientName", user.getName());
            ctx.setVariable("carBrand", car.getModel().getBrand().getName());
            ctx.setVariable("carModel", car.getModel().getName());
            ctx.setVariable("carYear", car.getModelYear());
            ctx.setVariable("oldPrice", String.format("$%,.2f", oldPrice));
            ctx.setVariable("newPrice", String.format("$%,.2f", car.getDefaultMarketValue()));
            ctx.setVariable(LINK, FLEET_MARKET + "/car/" + car.getId());

            String subject = messageSourceService.get("Car Price Changed");
            String emailContent = templateEngine.process("mail/car-price-change.html", ctx);

            send(new InternetAddress(senderAddress, appName),
                    new InternetAddress(user.getEmail(), user.getName()),
                    subject, emailContent);

            log.info("[EmailService] Sent car price change email to {}", user.getEmail());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("[EmailService] Failed to send car price change email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    /**
     * Create context for the template engine.
     *
     * @return Context
     */
    private Context createContext() {
        final Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("SENDER_ADDRESS", senderAddress);
        context.setVariable("APP_NAME", appName);
        context.setVariable("APP_URL", appUrl);
        context.setVariable("FRONTEND_URL", frontendUrl);
        return context;
    }

    /**
     * Send an e-mail to the specified address.
     *
     * @param from    Address who sent
     * @param to      Address who receives
     * @param subject String subject
     * @param text    String message
     * @throws MessagingException when sending fails
     */
    private void send(InternetAddress from, InternetAddress to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, true);
        mailSender.send(mimeMessage);
    }

    private void sendWithBbc(InternetAddress from, InternetAddress to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, true);
        mimeMessageHelper.addBcc("info@fleetmarkethub.com");
        mailSender.send(mimeMessage);
    }
}
