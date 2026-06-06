package com.reservaespacos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.dir:./uploads}")
    private String uploadsDir;

    /** Redireciona / para /index.html */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/index.html");
    }

    /** Serve ficheiros estáticos e a pasta de uploads do disco */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ficheiros estáticos do classpath
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        // CORRIGIDO: expõe a pasta de uploads em disco via /uploads/**
        // Sem isto, as fotos guardadas em ./uploads/ devolviam 404.
        String location = uploadsDir.startsWith("file:")
                ? uploadsDir
                : "file:" + System.getProperty("user.dir") + "/" + uploadsDir + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
