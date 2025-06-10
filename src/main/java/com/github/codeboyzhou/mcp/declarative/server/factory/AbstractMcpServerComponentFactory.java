package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.google.inject.Injector;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractMcpServerComponentFactory<T> implements McpServerComponentFactory<T> {

    protected final Injector injector;

    private final ResourceBundle bundle;

    protected AbstractMcpServerComponentFactory(Injector injector) {
        this.injector = injector;
        this.bundle = ResourceBundle.getBundle("i18n/mcp_server_component_descriptions", Locale.getDefault());
    }

    protected String getDescription(String descriptionI18nKey, String description) {
        if (!descriptionI18nKey.isBlank() && bundle.containsKey(descriptionI18nKey)) {
            return bundle.getString(descriptionI18nKey);
        }
        if (!description.isBlank()) {
            return description;
        }
        return "No description provided.";
    }

}
