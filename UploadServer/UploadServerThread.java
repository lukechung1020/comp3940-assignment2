import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }

   public void run() {
      System.out.println("UploadServerThread: New client connection established");
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String request = in.readLine();
         System.out.println("UploadServerThread: Request received: " + request); // log to check request received

         // Extract the URL path from the request (e.g., POST /fileuploadservlet HTTP/1.1)
         String[] requestParts = request.split(" ");
         String method = requestParts[0]; // GET or POST
         String path = requestParts[1]; // URL path
         System.out.println("UploadServerThread: Method: " + method + ", Path: " + path); // Debug statement


         Map<String, String> headers = new HashMap<>();
         String headerLine;

         while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
               headers.put(headerParts[0], headerParts[1].trim());
            }
         }
         System.out.println("UploadServerThread: Headers: " + headers); // log

         int contentLength = 0;
         if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
         }

         StringBuilder body = new StringBuilder();
         if (method.equals("POST") && contentLength > 0) {
            char[] bodyBuffer = new char[contentLength];
            in.read(bodyBuffer, 0, contentLength);
            body.append(bodyBuffer);
            System.out.println("UploadServerThread: Body: " + body); // log
         }

         HttpServletRequest req = new HttpServletRequest(socket.getInputStream(), headers, body.toString());
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);

         // Choose the servlet based on the requested path
         String servletName;
         if (path.equals("/fileuploadservlet")) {
            servletName = "FileUploadServlet";
         } else {
            // Fallback to another servlet or handle 404
            servletName = "UploadServlet"; // Assuming you have a default servlet
         }

         // Load and invoke the chosen servlet using reflection
         Class<?> servletClass = Class.forName(servletName);
         Constructor<?> constructor = servletClass.getConstructor();
         HttpServlet httpServlet = (HttpServlet) constructor.newInstance();

         if (method.equals("POST")) {
            System.out.println("UploadServerThread: Invoking doPost");
            httpServlet.doPost(req, res);
         } else if (method.equals("GET")) {
            System.out.println("UploadServerThread: Invoking doGet");
            httpServlet.doGet(req, res);
         } else {
            PrintWriter out = new PrintWriter(res.getOutputStream(), true);
            out.println("HTTP/1.1 405 Method Not Allowed");
            out.println("Allow: GET, POST\n");
         }

         OutputStream out = socket.getOutputStream();
         out.write(baos.toByteArray());
         socket.close();
         System.out.println("UploadServerThread: Connection closed");
      } catch (Exception e) {
         System.err.println("UploadServerThread: Exception occurred: " + e.getMessage());
         e.printStackTrace();
      }
   }
}

