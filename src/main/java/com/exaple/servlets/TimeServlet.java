package com.exaple.servlets;

import com.exaple.cookie.CookieManager;
import com.exaple.template.TemplateEngineSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Клас-сервлет TimeServlet
 * Цей сервлет відповідає за обробку запитів, пов'язаних із часом,
 * включаючи встановлення та отримання інформації про часовий пояс з кукі.
 */
@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(TimeServlet.class);
    private final TemplateEngine templateEngine;

    /**
     * Ініціалізує сервлет, отримуючи екземпляр шаблонного движка Thymeleaf.
     */
    public TimeServlet() {
        this.templateEngine = TemplateEngineSingleton.getInstance();
    }

    /**
     * Обробляє HTTP GET-запити для відображення поточного часу на основі наданого часового поясу.
     *
     * @param request  об'єкт HttpServletRequest, що містить запит клієнта
     * @param response об'єкт HttpServletResponse для відправлення відповіді клієнту
     * @throws IOException якщо відбувається помилка вводу-виводу при обробці запиту
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Отримання значення параметра timezone з запиту
        String timezoneParam = request.getParameter("timezone");
        Optional<String> lastTimezone = CookieManager.getLastTimezone(request);

        // Встановлення значення параметра zoneId
        ZoneId zoneId;
        if (timezoneParam != null && !timezoneParam.trim().isEmpty()) {
            // Розділити рядок параметра на числову і текстову частини
            String[] parts = timezoneParam.split("\\s+");
            String numericPart = parts[1]; // Отримати числову частину, наприклад, "4"

            // Перевірити, чи містить числова частина знак "+" або "-"
            boolean isNegativeOffset = numericPart.startsWith("-");
            boolean isPositiveOffset = numericPart.startsWith("+");

            // Перетворити числову частину на зміщення часового поясу
            int offsetHours = Integer.parseInt(numericPart);
            ZoneOffset offset = ZoneOffset.ofHours(offsetHours);

            // Створити об'єкт ZoneId з використанням зміщення
            zoneId = ZoneId.ofOffset("UTC", offset);
            CookieManager.setLastTimezone(response, numericPart);

            logger.info("Set timezone to {}", timezoneParam);
        } else if (lastTimezone.isPresent()) {
            // Використання збереженого значення часового поясу з кукі
            ZoneOffset offset = ZoneOffset.ofHours(Integer.parseInt(lastTimezone.get()));
            zoneId = ZoneId.ofOffset("UTC", offset);

            logger.info("Using last timezone from cookie: {}", lastTimezone.get());
        } else {
            // За замовчуванням використовуємо UTC
            zoneId = ZoneId.of("UTC");

            logger.info("Using default timezone UTC");
        }

        // Отримання поточного часу у вказаному часовому поясі
        LocalDateTime currentTime = LocalDateTime.now(zoneId);

        // Форматування поточного часу у вказаному форматі
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + zoneId;

        // Підготовка контексту для шаблону Thymeleaf
        Context ctx = new Context();
        ctx.setVariable("formattedTime", formattedTime);

        // Відображення шаблону Thymeleaf
        templateEngine.process("current_time", ctx, response.getWriter());
    }
}