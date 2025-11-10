package com.cosmosboard.fmh.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import java.util.Arrays;
import java.util.List;

public final class AppUtil {

    private static final int MIN_LENGTH = 6;

    private static final int MAX_LENGTH = 32;

    private AppUtil() {}

    public static List<String> isPasswordValid(String password) {
        if (password == null)
            return List.of();

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // Length rule. Min 6 max 32 characters
                new LengthRule(MIN_LENGTH, MAX_LENGTH),
                // At least one upper case letter
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // At least one lower case letter
                // new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // At least one number
                // new CharacterRule(EnglishCharacterData.Digit, 1),
                // At least one special characters
                // new CharacterRule(EnglishCharacterData.Special, 1),
                // No whitespace
                new WhitespaceRule()
        ));

        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid())
            return List.of();

        return validator.getMessages(result);
    }
}
