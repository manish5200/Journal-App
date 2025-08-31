package net.manifest.journalapp.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI myCustomConfig(){
        Contact contact = new Contact();
        contact.setEmail("manishneelambar@gmail.com");
        contact.setName("JournalApp Support");
        contact.setUrl("https://manifesttechnologies.com");

        final String securitySchemeName = "bearerAuth";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token **_only_**.");

        return new OpenAPI().info(
                new Info().title("Journal App APIs")
                        .description("By Manish Chauhan")
                        .contact(contact)
                 )
                .servers(Arrays.asList(new Server().url("http://localhost:8080").description("local")
                        ,new Server().url("http://localhost:8081").description("live")))
                .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));

    }
}
