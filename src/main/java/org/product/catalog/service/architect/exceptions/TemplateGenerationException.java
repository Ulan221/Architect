package org.product.catalog.service.architect.exceptions;

public class TemplateGenerationException extends ArchitectException {

    public TemplateGenerationException(final String message, final Throwable cause) {
        super("Failed to generate template " + message, cause);
    }
}
