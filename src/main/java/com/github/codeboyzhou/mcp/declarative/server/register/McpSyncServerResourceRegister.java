package com.github.codeboyzhou.mcp.declarative.server.register;

import com.github.codeboyzhou.mcp.declarative.annotation.McpResource;
import com.github.codeboyzhou.mcp.declarative.annotation.McpResources;
import com.github.codeboyzhou.mcp.declarative.util.JsonHelper;
import com.google.inject.Injector;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class McpSyncServerResourceRegister extends McpSyncServerComponentRegister<McpServerFeatures.SyncResourceSpecification> {

    private static final Logger logger = LoggerFactory.getLogger(McpSyncServerResourceRegister.class);

    protected McpSyncServerResourceRegister(Injector injector) {
        super(injector);
    }

    @Override
    public void registerTo(McpSyncServer server) {
        Reflections reflections = injector.getInstance(Reflections.class);
        Set<Class<?>> resourceClasses = reflections.getTypesAnnotatedWith(McpResources.class);
        ComponentBufferQueue<McpSyncServer, McpServerFeatures.SyncResourceSpecification> queue = new ComponentBufferQueue<>();
        for (Class<?> resourceClass : resourceClasses) {
            Set<Method> resourceMethods = reflections.getMethodsAnnotatedWith(McpResource.class);
            List<Method> methods = resourceMethods.stream().filter(m -> m.getDeclaringClass() == resourceClass).toList();
            for (Method method : methods) {
                McpServerFeatures.SyncResourceSpecification resource = createComponentFrom(resourceClass, method);
                queue.submit(resource);
            }
        }
        queue.consume(server, McpSyncServer::addResource);
    }

    @Override
    public McpServerFeatures.SyncResourceSpecification createComponentFrom(Class<?> clazz, Method method) {
        McpResource res = method.getAnnotation(McpResource.class);
        final String name = res.name().isBlank() ? method.getName() : res.name();
        McpSchema.Resource resource = new McpSchema.Resource(
            res.uri(), name, res.description(), res.mimeType(),
            new McpSchema.Annotations(List.of(res.roles()), res.priority())
        );
        logger.debug("Registering resource: {}", JsonHelper.toJson(resource));
        return new McpServerFeatures.SyncResourceSpecification(resource, (exchange, request) -> {
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
        });
    }

}
