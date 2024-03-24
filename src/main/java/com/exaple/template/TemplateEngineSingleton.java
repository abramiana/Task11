package com.exaple.template;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Клас, який представляє шаблонний двигун Thymeleaf та забезпечує
 * створення та доступ до єдиного екземпляру цього двигуна.
 */
public class TemplateEngineSingleton {
    private static final TemplateEngine templateEngine;
    private static final Logger logger = LogManager.getLogger(TemplateEngineSingleton.class);

    static {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false); // Для розробки, встановіть true на продакшні
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        logger.info("Template engine initialized");
    }

    /**
     * Метод, що повертає єдиний екземпляр шаблонного двигуна.
     *
     * @return екземпляр шаблонного двигуна Thymeleaf
     */
    public static TemplateEngine getInstance() {
        return templateEngine;
    }
}