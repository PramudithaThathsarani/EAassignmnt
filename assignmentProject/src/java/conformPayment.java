import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

@WebServlet(urlPatterns = {"/confirmPayment"})
public class conformPayment extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Update your database URL, username, and password
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_db?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String price = request.getParameter("price");
            String userName = request.getParameter("userName");
            String userEmail = request.getParameter("userEmail");
            String paymentMethod = request.getParameter("paymentMethod");

            // Generate QR Code content
            String qrCodeData = "Food Item: " + name + "\nPrice: $" + price;

            // Generate QR Code image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int width = 300;
            int height = 300;
            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix matrix = writer.encode(qrCodeData, BarcodeFormat.QR_CODE, width, height,
                        createHintMap());

                // Convert the BitMatrix to a PNG image and write it to the output stream
                writeToStream(matrix, "PNG", stream);
            } catch (WriterException e) {
                throw new IOException("Error generating QR code", e);
            }

            byte[] qrCodeImage = stream.toByteArray();

            // Save payment details to the database
            savePaymentToDatabase(id, name, price, userName, userEmail, paymentMethod);

            // Display HTML page with payment confirmation and QR code
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Payment Confirmation</title>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<style>");
            out.println("body { background-color: #28282B; color: whitesmoke; font-family: Arial, sans-serif; }");
            out.println(".container { max-width: 600px; margin: 50px auto; padding: 20px; background-color: #333; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.2);display:flex;flex-direction:column;align-items:center; }");
            out.println(".container h1 { margin-top: 0; }");
            out.println(".container p { margin: 10px 0; }");
            out.println(".container .user-details, .container .order-summary, .container .payment-options { margin-bottom: 20px; }");
            out.println(".container button { background-color: #4CAF50; color: white; border: none; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; border-radius: 5px; cursor: pointer; }");
            out.println(".qr-code { margin-top: 20px; text-align: center; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>Payment Confirmation</h1>");
            out.println("<p>Thank you, " + userName + "!</p>");
            out.println("<p>Your order for <strong>" + name + "</strong> has been placed successfully.</p>");
            out.println("<p>Order Details:</p>");
            out.println("<ul>");
            out.println("<li>Food Item: " + name + "</li>");
            out.println("<li>Price: Rs." + price + "</li>");
            out.println("<li>Payment Method: " + paymentMethod + "</li>");
            out.println("</ul>");
            out.println("<p>A confirmation email has been sent to " + userEmail + ".</p>");
            out.println("<div class='qr-code'>");
            out.println("<h2>QR Code</h2>");
            out.println("<img src='data:image/png;base64, " + encodeToString(qrCodeImage) + "' alt='QR Code'>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        ImageIO.write(image, format, stream);
    }

    private static Map<EncodeHintType, Object> createHintMap() {
        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        return hintMap;
    }

    private static String encodeToString(byte[] qrCodeImage) {
        return Base64.getEncoder().encodeToString(qrCodeImage);
    }

    private void savePaymentToDatabase(String id, String name, String price, String userName, String userEmail, String paymentMethod) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create SQL statement to insert payment details
            String sql = "INSERT INTO payment_details ( name, price, user_name, user_email, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            // Set parameters
            stmt.setString(1, name);
            stmt.setString(2, price);
            stmt.setString(3, userName);
            stmt.setString(4, userEmail);
            stmt.setString(5, paymentMethod);

            // Execute the update
            stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            // Handle errors
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
