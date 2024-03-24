package com.exaple.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

/**
 * Утилітарний клас для керування кукі з часовим поясом.
 */
public class CookieManager {
    private static final String COOKIE_NAME = "lastTimezone";
    private static final Logger logger = LogManager.getLogger(CookieManager.class);

    /**
     * Отримує останній часовий пояс, збережений у куці, з HttpServletRequest.
     *
     * @param request HttpServletRequest об'єкт
     * @return Optional, який містить останній часовий пояс, якщо він присутній, в іншому випадку - порожній
     */
    public static Optional<String> getLastTimezone(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookie = Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(COOKIE_NAME))
                    .findFirst();
            if (cookie.isPresent()) {
                String timezone = cookie.get().getValue();
                logger.info("Retrieved timezone from cookie: {}", timezone);
                return Optional.of(timezone);
            }
        }
        logger.info("No timezone found in cookies");
        return Optional.empty();
    }

    /**
     * Встановлює останній часовий пояс у куці для HttpServletResponse.
     *
     * @param response HttpServletResponse об'єкт
     * @param timezone Рядок, який представляє часовий пояс
     */
    public static void setLastTimezone(HttpServletResponse response, String timezone) {
        Cookie cookie = new Cookie(COOKIE_NAME, timezone);
        cookie.setMaxAge(24 * 60 * 60); // 1 day expiration time (in seconds)
        response.addCookie(cookie);
        logger.info("Set timezone in cookie: {}", timezone);
    }
}