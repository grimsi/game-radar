package de.grimsi.gameradar.backend.configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

/**
 * Parses out Spring Security {@link PreAuthorize} annotations and adds them in the notes section.
 */
@Component
@Profile({"default", "!prod"}) // This disables Swagger in unittests
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerSecurityConfiguration implements OperationBuilderPlugin {
    @Override
    public void apply(final OperationContext context) {
        // Look for @PreAuthorize on method, otherwise look on controller
        context.findAnnotation(PreAuthorize.class)
                .or(() -> context.findControllerAnnotation(PreAuthorize.class))
                .ifPresent(preAuth -> context.operationBuilder()
                        .notes(getNotes(preAuth)));
    }

    private String getNotes(PreAuthorize preAuth) {
        String preauthExpr = preAuth.value();
        return "**Security @PreAuthorize expression:** `" + preauthExpr + "`";
    }

    @Override
    public boolean supports(final DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}
