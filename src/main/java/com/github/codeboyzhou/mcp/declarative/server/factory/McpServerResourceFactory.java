package com.github.codeboyzhou.mcp.declarative.server.factory;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.common.BufferQueue;
import com.github.codeboyzhou.mcp.declarative.util.JsonHelper;
import com.github.codeboyzhou.mcp.declarative.util.StringHelper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class McpServerResourceFactory extends AbstractMcpServerComponentFactory<McpServerFeatures.AsyncResourceSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpServerResourceFactory.class);

    @Inject
    protected McpServerResourceFactory(Injector injector) {
        super(injector);
    }

    @Override
    public McpServerFeatures.AsyncResourceSpecification create(Class<?> clazz, Method method) {
        McpResource res = method.getAnnotation(McpResource.class);
        final String name = res.name().isBlank() ? method.getName() : res.name();
        final String title = StringHelper.defaultIfBlank(res.title(), NO_TITLE_SPECIFIED);
        final String description = getDescription(res.descriptionI18nKey(), res.description());
        McpSchema.Annotations annotations = new McpSchema.Annotations(List.of(res.roles()), res.priority());
        McpSchema.Resource resource = new McpSchema.Resource(res.uri(), name, title, description, res.mimeType(), null, annotations);
        logger.debug("Registering resource: {}", JsonHelper.toJson(resource));
        return new McpServerFeatures.AsyncResourceSpecification(resource, (exchange, request) ->
            Mono.fromSupplier(() -> {
                Object result;
                try {
                    Object instance = injector.getInstance(clazz);
                    result = method.invoke(instance);
                } catch (Exception e) {
                    logger.error("Error invoking resource method", e);
                    result = e + ": " + e.getMessage();
                }
                McpSchema.ResourceContents contents = new McpSchema.TextResourceContents(
                    resource.uri(), resource.mimeType(), result.toString()
                );
                return new McpSchema.ReadResourceResult(List.of(contents));
            })
        );
    }

    @Override
    public void registerTo(McpAsyncServer server) {
        Reflections reflections = injector.getInstance(Reflections.class);
        Set<Class<?>> resourceClasses = reflections.getTypesAnnotatedWith(McpResources.class);
        BufferQueue<McpServerFeatures.AsyncResourceSpecification> queue = new BufferQueue<>();
        for (Class<?> resourceClass : resourceClasses) {
            Set<Method> resourceMethods = reflections.getMethodsAnnotatedWith(McpResource.class);
            List<Method> methods = resourceMethods.stream().filter(m -> m.getDeclaringClass() == resourceClass).toList();
            for (Method method : methods) {
                McpServerFeatures.AsyncResourceSpecification resource = create(resourceClass, method);
                queue.submit(resource);
            }
        }
        queue.consume(resource -> server.addResource(resource).subscribe());
    }

}
