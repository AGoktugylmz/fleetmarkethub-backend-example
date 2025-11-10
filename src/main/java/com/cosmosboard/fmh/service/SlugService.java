package com.cosmosboard.fmh.service;

import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SlugService {
    @Value("${app.default-locale:en}")
    private String defaultLocale;

    /**
     * Generate slug from string.
     *
     * @param text String
     * @return String
     */
    public String generate(String text) {
        return getInstance().slugify(text);
    }

    /**
     * Get Slugify instance.
     * <a href="https://github.com/slugify/slugify">Slugify</a>
     *
     * @return Slugify instance
     */
    private Slugify getInstance() {
        return Slugify.builder()
            .locale(new Locale.Builder().setLanguage(defaultLocale).build())
            // provided as a map
            // .customReplacements(Map.of("Foo", "Hello", "bar", "world"))
            // provided as single key-value
            // .customReplacement("Foo", "Hello")
            // .customReplacement("bar", "world")
            .build();
    }
}
