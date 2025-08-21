package com.github.codeboyzhou.mcp.declarative.server.component;

import static com.github.codeboyzhou.mcp.declarative.common.InjectorModule.INJECTED_VARIABLE_NAME_I18N_ENABLED;

import com.github.codeboyzhou.mcp.declarative.common.InjectorProvider;
import com.github.codeboyzhou.mcp.declarative.util.Strings;
import com.google.inject.Key;
import com.google.inject.name.Names;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMcpServerComponentFactory<T> implements McpServerComponentFactory<T> {

  private static final Logger log =
      LoggerFactory.getLogger(AbstractMcpServerComponentFactory.class);

  private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n/mcp_server_component_descriptions";

  protected static final String NOT_SPECIFIED = "Not Specified";

  private final ResourceBundle bundle;

  private final boolean i18nEnabled;

  protected AbstractMcpServerComponentFactory() {
    this.bundle = loadResourceBundle();
    Key<Boolean> key = Key.get(Boolean.class, Names.named(INJECTED_VARIABLE_NAME_I18N_ENABLED));
    this.i18nEnabled = InjectorProvider.getInstance().getInjector().getInstance(key);
  }

  protected String resolveComponentAttributeValue(String attributeLiteralValue) {
    if (i18nEnabled && bundle != null && bundle.containsKey(attributeLiteralValue)) {
      return bundle.getString(attributeLiteralValue);
    }
    return Strings.defaultIfBlank(attributeLiteralValue, NOT_SPECIFIED);
  }

  private ResourceBundle loadResourceBundle() {
    Locale locale = Locale.getDefault();
    try {
      return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
    } catch (Exception e) {
      log.warn(
          "Can't find resource bundle for base name: {}, locale {}, i18n will be unsupported",
          RESOURCE_BUNDLE_BASE_NAME,
          locale);
      return null;
    }
  }
}
