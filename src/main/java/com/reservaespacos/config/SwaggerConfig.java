package com.reservaespacos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger / OpenAPI 3.
 *
 * Acesso: http://localhost:8080/swagger-ui.html
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │  COMO AUTENTICAR NO SWAGGER                             │
 * │                                                         │
 * │  1. Execute  POST /auth/login  com as credenciais.      │
 * │  2. Copie o valor do campo "token" da resposta.         │
 * │  3. Clique no botão "Authorize" (cadeado) no topo.      │
 * │  4. Cole o token no campo "bearerAuth" e confirme.      │
 * │  5. Feche o diálogo — todas as chamadas usarão o token. │
 * └─────────────────────────────────────────────────────────┘
 *
 * Credenciais iniciais:
 *   ADMIN       admin@reservas.mz / ALTERE_ESTA_SENHA
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Reserva de Espaços — Maputo")
                .description("""
                    ## API REST para gestão e reserva de espaços para eventos em Maputo

                    Desenvolvida em Spring Boot como projecto académico da Universidade Joaquim Chissano.

                    ---

                    ### Perfis de acesso

                    | Perfil | Permissões |
                    |--------|------------|
                    | **ADMIN** | Acesso total ao sistema |
                    | **PROPRIETARIO** | Gere os seus espaços e consulta reservas |
                    | **CLIENTE** | Efectua reservas e pagamentos |

                    ---

                    ### Como autenticar

                    1. Chame **`POST /auth/login`** com as credenciais abaixo.
                    2. Copie o campo **`token`** da resposta JSON.
                    3. Clique no botão **Authorize 🔒** (topo da página).
                    4. Cole o token e clique **Authorize**.
                    5. Feche o diálogo — todas as chamadas incluirão o token automaticamente.

                    ---

                    ### Credenciais de teste

                    | Perfil | Email | Senha |
                    |--------|-------|-------|
                    | ADMIN | `admin@reservas.mz` | `ALTERE_ESTA_SENHA` |

                    > Registe novos utilizadores em `POST /auth/register` (CLIENTE/PROPRIETARIO)
                    > ou em `POST /auth/admin/register` com token ADMIN (qualquer role).
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Grupo III — Engenharia de Software")
                    .email("admin@reservas.mz")
                )
                .license(new License()
                    .name("Projecto Académico — UJC 2026")
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
                        .description("""
                            Token JWT obtido em **POST /auth/login**.

                            Formato do header enviado automaticamente:
                            ```
                            Authorization: Bearer eyJhbGci...
                            ```
                            """)
                )
            );
    }
}
