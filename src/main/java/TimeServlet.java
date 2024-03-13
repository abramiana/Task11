import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Отримання значення параметра zoneId, який встановлено в результаті валідації фільтром
        ZoneId zoneId = (ZoneId) request.getAttribute("zoneId");

        // Перевірка, чи zoneId було встановлено коректно фільтром
        if (zoneId == null) {
            // Якщо zoneId не було встановлено, використовуємо UTC за замовчуванням
            zoneId = ZoneId.of("UTC");
        }

        // Отримання поточного часу у вказаному часовому поясі
        LocalDateTime currentTime = LocalDateTime.now(zoneId);

        // Форматування поточного часу у вказаному форматі
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + zoneId;

        // Вивід відформатованого часу в відповідь
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Current Time</title></head>");
        out.println("<body><h1>Current Time</h1>");
        out.println("<p>" + formattedTime + "</p>");
        out.println("</body></html>");
    }
}