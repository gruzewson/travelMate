package org.travelmate.config;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;
import java.security.Principal;
import java.util.UUID;
import java.util.logging.Logger;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {

    private static final Logger LOGGER = Logger.getLogger(LoggingInterceptor.class.getName());

    @Inject
    private SecurityContext securityContext;

    @AroundInvoke
    public Object logOperation(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSuperclass().getSimpleName();
        
        // Get username
        String username = "anonymous";
        try {
            Principal principal = securityContext.getCallerPrincipal();
            if (principal != null) {
                username = principal.getName();
            }
        } catch (Exception e) {
            // Security context not available
        }

        // Determine operation type
        String operation = determineOperation(methodName);
        
        // Get resource ID from parameters
        String resourceId = extractResourceId(context.getParameters());

        // Log before operation
        LOGGER.info(String.format("[USER: %s] [OPERATION: %s] [SERVICE: %s] [RESOURCE_ID: %s] - Starting",
                username, operation, className, resourceId));

        try {
            // Execute the actual method
            Object result = context.proceed();

            // Log after successful operation
            LOGGER.info(String.format("[USER: %s] [OPERATION: %s] [SERVICE: %s] [RESOURCE_ID: %s] - Completed successfully",
                    username, operation, className, resourceId));

            return result;
        } catch (Exception e) {
            // Log failed operation
            LOGGER.warning(String.format("[USER: %s] [OPERATION: %s] [SERVICE: %s] [RESOURCE_ID: %s] - Failed: %s",
                    username, operation, className, resourceId, e.getMessage()));
            throw e;
        }
    }

    private String determineOperation(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("add") || methodName.startsWith("save")) {
            return "CREATE";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "DELETE";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit") || methodName.startsWith("modify")) {
            return "UPDATE";
        }
        return methodName.toUpperCase();
    }

    private String extractResourceId(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "N/A";
        }

        for (Object param : parameters) {
            if (param instanceof UUID) {
                return param.toString();
            }
            // If it's an entity with getId method, try to extract ID
            if (param != null) {
                try {
                    java.lang.reflect.Method getIdMethod = param.getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(param);
                    if (id != null) {
                        return id.toString();
                    }
                } catch (Exception e) {
                    // Not an entity with getId
                }
            }
        }

        return "N/A";
    }
}
