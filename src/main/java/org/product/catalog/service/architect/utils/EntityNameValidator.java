package org.product.catalog.service.architect.utils;

import java.util.regex.Pattern;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsSafe;

public class EntityNameValidator implements InputValidator {
    private static final Pattern JAVA_CLASS_NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");

    @Override
    public boolean checkInput(@NlsSafe final String inputString) {
        if (inputString == null || inputString.trim()
                                              .isEmpty()) {
            return false;
        }
        return JAVA_CLASS_NAME_PATTERN.matcher(inputString.trim())
                                      .matches();
    }

    @Override
    public boolean canClose(@NlsSafe final String inputString) {
        return checkInput(inputString);
    }
}
