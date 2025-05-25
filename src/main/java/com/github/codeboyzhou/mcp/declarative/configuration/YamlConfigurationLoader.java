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

import static java.util.stream.Collectors.joining;

public class YamlConfigurationLoader {

    private static final Logger logger = LoggerFactory.getLogger(YamlConfigurationLoader.class);

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public McpServerConfiguration loadConfiguration() {
        try {
            return load("mcp-server.yml");
        } catch (IOException e) {
            try {
                return load("mcp-server.yaml");
            } catch (IOException ex) {
                logger.warn("The mcp-server.yml and mcp-server.yaml were not found, will use default configuration");
                try {
                    return load("mcp-server-default.yml");
                } catch (IOException ignored) {
                    // should never happen
                    return null;
                }
            }
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
            logger.debug("Loaded configuration from {}:\n{}", configFileName, content);
            return mapper.readValue(content, McpServerConfiguration.class);
        }
    }

}
