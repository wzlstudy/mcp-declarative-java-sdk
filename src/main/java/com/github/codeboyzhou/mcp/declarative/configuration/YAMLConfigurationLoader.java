package com.github.codeboyzhou.mcp.declarative.configuration;

import com.github.codeboyzhou.mcp.declarative.exception.McpServerException;
import com.github.codeboyzhou.mcp.declarative.util.ObjectMappers;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YAMLConfigurationLoader {

  private static final Logger logger = LoggerFactory.getLogger(YAMLConfigurationLoader.class);

  private static final String CONFIG_FILE_NAME = "mcp-server.yml";

  private final String configFileName;

  public YAMLConfigurationLoader(String configFileName) {
    this.configFileName = configFileName;
  }

  public YAMLConfigurationLoader() {
    this(CONFIG_FILE_NAME);
  }

  public McpServerConfiguration loadConfig() {
    Path configFilePath = getConfigFilePath(configFileName);
    McpServerConfiguration config =
        ObjectMappers.fromYaml(configFilePath.toFile(), McpServerConfiguration.class);
    logger.info("Configuration loaded successfully from file: {}", configFileName);
    return config;
  }

  private Path getConfigFilePath(String fileName) {
    try {
      ClassLoader classLoader = YAMLConfigurationLoader.class.getClassLoader();
      URL configFileUrl = classLoader.getResource(fileName);
      if (configFileUrl == null) {
        throw new McpServerException("Configuration file not found: " + fileName);
      }
      return Paths.get(configFileUrl.toURI());
    } catch (URISyntaxException e) {
      // should never happen
      throw new McpServerException("Invalid configuration file path: " + fileName, e);
    }
  }
}
