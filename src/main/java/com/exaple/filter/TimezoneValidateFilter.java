package com.exaple.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Фільтр для перевірки та обробки часового поясу з параметру запиту "timezone".
 */
@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(TimezoneValidateFilter.class);

    /**
     * Виконує фільтрацію запиту та встановлює часовий пояс, якщо він переданий у параметрі.
     *
     * @param request  об'єкт HttpServletRequest
     * @param response об'єкт HttpServletResponse
     * @param chain    об'єкт FilterChain
     * @throws IOException      якщо виникає помилка вводу/виводу при зверненні до потоків
     * @throws ServletException якщо виникає помилка у Servlet-компоненті
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Отримання значення параметра timezone з запиту
        String timezoneParam = httpRequest.getParameter("timezone");
        logger.info("Received timezone parameter: {}", timezoneParam);

        // Перевірка, чи параметр не є порожнім або містить тільки пробіли
        if (timezoneParam != null && !timezoneParam.trim().isEmpty()) {
            // Спроба розбити параметр на ідентифікатор та зсув
            logger.error("Invalid timezone offset: {}", timezoneParam);
            String[] parts = timezoneParam.split("\\s+");
            String timezoneId = parts[0];
            int offset = 0;

            // Перевірка, чи вказаний зсув
            if (parts.length > 1) {
                offset = Integer.parseInt(parts[1]);
            }

            // Перевірка, чи зсув знаходиться в допустимому діапазоні (-18 до 18)
            if (offset < -18 || offset > 18) {
                // Повідомлення про неправильно вказаний часовий пояс
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpResponse.setContentType("text/html");
                try (PrintWriter out = httpResponse.getWriter()) {
                    out.println("<html><head><title>Error</title></head><body>");
                    out.println("<h1>Invalid timezone offset</h1>");
                    out.println("</body></html>");
                }
                return;
            }

            // Встановлення часового поясу відповідно до значення параметру timezone
            ZoneId zoneId;
            if (timezoneId.startsWith("UTC") || timezoneId.startsWith("GMT")) {
                // Визначення часового зсуву для UTC або GMT
                ZoneOffset zoneOffset = ZoneOffset.ofHours(offset);
                zoneId = ZoneId.ofOffset(timezoneId, zoneOffset);
            } else {
                zoneId = ZoneId.of(timezoneId);
            }

            // Установка атрибута з об'єктом ZoneId для подальшого використання
            httpRequest.setAttribute("zoneId", zoneId);
            logger.info("Timezone successfully validated and set: {}", zoneId);
        }

        // Продовження ланцюга фільтрів
        chain.doFilter(request, response);
    }
}