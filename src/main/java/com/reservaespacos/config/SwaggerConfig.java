package com.reservaespacos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Reserva de Espacos - Maputo")
                .description("""
                    ## API REST para gestao e reserva de espacos para eventos em Maputo

                    Desenvolvida em Spring Boot como projecto academico da Universidade Joaquim Chissano.

                    ---

                    ### Perfis de acesso

                    | Perfil | Permissoes |
                    |--------|------------|
                    | **ADMIN** | Acesso total ao sistema |
                    | **PROPRIETARIO** | Gere os seus espacos e consulta reservas |
                    | **CLIENTE** | Efectua reservas e pagamentos |

                    ---

                    ### Como autenticar

                    1. Chame **`POST /auth/login`** com as credenciais abaixo.
                    2. Copie o campo **`token`** da resposta JSON.
                    3. Clique no botao **Authorize** (topo da pagina).
                    4. Cole o token e clique **Authorize**.
                    5. Feche o dialogo - todas as chamadas incluirao o token automaticamente.

                    ---

                    ### Credenciais iniciais

                    | Perfil | Email | Senha |
                    |--------|-------|-------|
                    | ADMIN | `admin@reservas.mz` | `engsoft2026!` |

                    > Registe novos utilizadores em `POST /auth/register` (CLIENTE/PROPRIETARIO)
                    > ou em `POST /auth/admin/register` com token ADMIN (qualquer role).
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Azarias Mahumane - Engenharia de Software")
                    .email("admin@reservas.mz")
                )
                .license(new License()
                    .name("Projecto Academico - UJC 2026")
                )
            )
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor local de desenvolvimento")
            ))
            .addSecurityItem(new SecurityRequirement().addList(schemeName))
            .components(new Components()
                .addSecuritySchemes(schemeName,
                    new SecurityScheme()
                        .name(schemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token JWT obtido em POST /auth/login. Formato: Authorization: Bearer <token>")
                )
            );
    }
}
