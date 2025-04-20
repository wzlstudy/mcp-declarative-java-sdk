package com.github.codeboyzhou.mcp.declarative.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class YamlConfigurationLoader {

    private static final Logger logger = LoggerFactory.getLogger(YamlConfigurationLoader.class);

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public McpServerConfiguration loadConfiguration() {
        try {
            McpServerConfiguration configuration = load("mcp-server.yml");
            if (configuration == null) {
                configuration = load("mcp-server.yaml");
            }
            return Objects.requireNonNullElseGet(configuration, McpServerConfiguration::defaultConfiguration);
        } catch (IOException e) {
            logger.error("Error loading configuration file, will use default configuration", e);
            return McpServerConfiguration.defaultConfiguration();
        }
    }

    public McpServerConfiguration load(String configFileName) throws IOException {
        ClassLoader classLoader = YamlConfigurationLoader.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                throw new NoSuchFileException(configFileName);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String content = bufferedReader.lines().collect(joining(System.lineSeparator()));
            return mapper.readValue(content, McpServerConfiguration.class);
        }
    }

}
