package com.cosmosboard.fmh.config.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
class MailConfig {
    private final MailConfigurationProperties mailConfigurationProperties;

    @Value("${app.mail.smtp.socketFactory.port}") private Integer socketPort;

    @Value("${app.mail.smtp.auth}") private boolean auth;

    @Value("${app.mail.smtp.starttls.enable}") private boolean starttls;

    @Value("${app.mail.smtp.starttls.required}") private boolean startllsRequired;

    @Value("${app.mail.smtp.socketFactory.fallback}") private boolean fallback;

    /**
     * Defining JavaMailSender as a bean
     * JavaMailSender is an interface for JavaMail, supporting MIME messages both as direct arguments
     * and through preparation callbacks
     * @return -- an implementation of the JavaMailSender interface
     */
    @Bean
    public JavaMailSender javaMailSender() {
        final JavaMailSenderImpl ms = new JavaMailSenderImpl();
        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", starttls);
        properties.put("mail.smtp.starttls.required", startllsRequired);
        properties.put("mail.smtp.socketFactory.port", socketPort);
        properties.put("mail.smtp.debug", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", fallback);
        ms.setJavaMailProperties(properties);
        ms.setHost(mailConfigurationProperties.getHost());
        ms.setPort(Integer.parseInt(mailConfigurationProperties.getPort()));
        ms.setProtocol(mailConfigurationProperties.getProtocol());
        ms.setUsername(mailConfigurationProperties.getUsername());
        ms.setPassword(mailConfigurationProperties.getPassword());
        return ms;
    }

    /**
     * THYMELEAF TemplateResolver(3) <- TemplateEngine
     */
    @Bean(name = "htmlTemplateEngine")
    public ITemplateEngine htmlTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.setTemplateEngineMessageSource(templateMessageSource());
        return templateEngine;
    }

    /**
     * THYMELEAF TemplateResolver(3) <- TemplateEngine
     * @return -- an implementation of the ITemplateResolver interface
     */
    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(2);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    /**
     * THYMELEAF TemplateMessageSource
     * @return -- an implementation of the TemplateMessageSource interface
     */
    private ResourceBundleMessageSource templateMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("templates/i18n/Template");
        return messageSource;
    }
}
