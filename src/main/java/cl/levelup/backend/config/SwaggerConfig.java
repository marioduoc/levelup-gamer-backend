package cl.levelup.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI documentacion() {
        String esquema = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Level-Up Gamer API")
                        .version("v1")
                        .description("API REST de la tienda Level-Up Gamer. "
                                + "Para probar endpoints protegidos: POST /api/v1/auth/login, copiar el accessToken "
                                + "y pegarlo en el botón Authorize."))
                .addSecurityItem(new SecurityRequirement().addList(esquema))
                .components(new Components().addSecuritySchemes(esquema, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
