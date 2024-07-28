import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet(urlPatterns = {"/home"})
public class home extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Connection conn = null;
            Statement stmt = null;

            try {
                // Connect to the database
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt = conn.createStatement();
                String sql = "SELECT id, name, description, price FROM menu";
                ResultSet rs = stmt.executeQuery(sql);

 
                out.println("<div class='catalog'>");

                // Generate cards with data
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");

                    out.println("<div class='card'>");
                    out.println("<h3>" + name + "</h3>");
                    out.println("<p>" + description + "</p>");
                    out.println("<p>Price: Rs." + price + "</p>");
                    out.println("<form action='payment' method='POST'>");
                    out.println("<input type='hidden' name='id' value='" + id + "' />");
                    out.println("<input type='hidden' name='name' value='" + name + "' />");
                    out.println("<input type='hidden' name='price' value='" + price + "' />");
                    out.println("<button type='submit'>Place Order</button>");
                    out.println("</form>");
                    out.println("</div>");
                }

                out.println("</div>");
  

                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
