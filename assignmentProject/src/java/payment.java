import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/payment"})
public class payment extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String price = request.getParameter("price");

           out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Payment Page</title>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<style>");
            out.println("body { background-color: #f3f4f6; color: #333; font-family: 'Roboto', sans-serif; margin: 0; padding: 0; display: flex; justify-content: center; align-items: center; height: 100vh; }");
            out.println(".container { max-width: 600px; padding: 30px; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); text-align: center; }");
            out.println(".container h1 { margin-top: 0; color: #111; font-size: 28px; }");
            out.println(".container p { margin: 10px 0; line-height: 1.6; }");
            out.println(".container .user-details, .container .order-summary, .container .payment-options { margin-bottom: 20px; text-align: left; }");
            out.println(".container .user-details h2, .container .order-summary h2, .container .payment-options h2 { margin: 0; font-size: 22px; color: #444; text-align: center; }");
            out.println(".container .order-summary p { font-weight: 600; }");
            out.println(".container .payment-options form { margin: 0; }");
            out.println(".container .payment-options p { margin: 5px 0; }");
            out.println(".container input[type='radio'] { margin-right: 10px; }");
            out.println(".container button { background-color: #4CAF50; color: white; border: none; padding: 12px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; border-radius: 8px; cursor: pointer; transition: background-color 0.3s ease; }");
            out.println(".container button:hover { background-color: #45a049; }");
            out.println(".header { background-color: #28282B; color: whitesmoke; padding: 10px 0; text-align: center; font-size: 24px; margin-bottom: 20px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
//            out.println("<div class='header'>Payment Page</div>");
            out.println("<div class='container'>");
            out.println("<h1>Order Payment</h1>");
            out.println("<div class='user-details'>");
            out.println("<h2>User Details</h2>");
            out.println("<form action='confirmPayment' method='POST'>");
            out.println("<input type='hidden' name='id' value='" + id + "' />");
            out.println("<input type='hidden' name='name' value='" + name + "' />");
            out.println("<input type='hidden' name='price' value='" + price + "' />");
            out.println("<p><label>Name</label><br> <input type='text' name='userName' required /></p>");
            out.println("<p><label>Email or Telephone</label> <br><input type='text' name='userEmailOrTelephone' required /></p>");
            out.println("</div>");
            out.println("<div class='order-summary'>");
            out.println("<h2>Order Summary</h2>");
            out.println("<p><strong>Food Item:</strong> " + name + "</p>");
            out.println("<p><strong>Price:</strong> Rs." + price + "</p>");
            out.println("</div>");
            out.println("<div class='payment-options'>");
            out.println("<h2>Payment Options</h2>");
            out.println("<p><label><input type='radio' name='paymentMethod' value='Credit Card' required> Credit Card</label></p>");
            out.println("<p><label><input type='radio' name='paymentMethod' value='PayPal' required> PayPal</label></p>");
            out.println("<button type='submit'>Proceed to Payment</button>");
            out.println("</form>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
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
