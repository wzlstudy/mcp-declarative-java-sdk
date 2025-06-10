package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractMcpServerComponentFactory<T> implements McpServerComponentFactory<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMcpServerComponentFactory.class);

    private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/mcp_server_component_descriptions";

    protected final Injector injector;

    private final ResourceBundle bundle;

    protected AbstractMcpServerComponentFactory(Injector injector) {
        this.injector = injector;
        this.bundle = loadResourceBundle();
    }

    protected String getDescription(String descriptionI18nKey, String description) {
        if (!descriptionI18nKey.isBlank() && bundle != null && bundle.containsKey(descriptionI18nKey)) {
            return bundle.getString(descriptionI18nKey);
        }
        if (!description.isBlank()) {
            return description;
        }
        return "No description provided.";
    }

    private ResourceBundle loadResourceBundle() {
        Locale locale = Locale.getDefault();
        try {
            return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
        } catch (Exception e) {
            logger.warn("Can't find resource bundle for base name: {}, locale {}, i18n will be unsupported", RESOURCE_BUNDLE_BASE_NAME, locale);
            return null;
        }
    }

}
