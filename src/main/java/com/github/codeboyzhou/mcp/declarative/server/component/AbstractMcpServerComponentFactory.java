package com.github.codeboyzhou.mcp.declarative.server.component;

import static com.github.codeboyzhou.mcp.declarative.common.GuiceInjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMcpServerComponentFactory<T> implements McpServerComponentFactory<T> {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractMcpServerComponentFactory.class);

  private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/mcp_server_component_descriptions";

  protected static final String NOT_SPECIFIED = "Not Specified";

  protected final Injector injector;

  protected final Boolean i18nEnabled;

  private final ResourceBundle bundle;

  protected AbstractMcpServerComponentFactory(
      Injector injector, @Named(INJECTED_VARIABLE_NAME_I18N_ENABLED) Boolean i18nEnabled) {
    this.injector = injector;
    this.i18nEnabled = i18nEnabled;
    this.bundle = loadResourceBundle();
  }

  protected String resolveComponentAttributeValue(String attributeLiteralValue) {
    if (i18nEnabled && bundle != null && bundle.containsKey(attributeLiteralValue)) {
      return bundle.getString(attributeLiteralValue);
    }
    return StringHelper.defaultIfBlank(attributeLiteralValue, NOT_SPECIFIED);
  }

  private ResourceBundle loadResourceBundle() {
    Locale locale = Locale.getDefault();
    try {
      return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
    } catch (Exception e) {
      logger.warn(
          "Can't find resource bundle for base name: {}, locale {}, i18n will be unsupported",
          RESOURCE_BUNDLE_BASE_NAME,
          locale);
      return null;
    }
  }
}
