import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }

   public void run() {
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String request = in.readLine();
         System.out.println("Request: " + request); // log to check request received

         Map<String, String> headers = new HashMap<>();
         String headerLine;

         // FOR CHECKING HEADERS -- can delete
         while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
               headers.put(headerParts[0], headerParts[1].trim());
            }
         }
         System.out.println("Headers: " + headers); // log

         int contentLength = 0;
         if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
         }

         // FOR CHECKING CONTENT/BODY -- can delete
         StringBuilder body = new StringBuilder();
         if (request.startsWith("POST") && contentLength > 0) {
            char[] bodyBuffer = new char[contentLength];
            in.read(bodyBuffer, 0, contentLength);
            body.append(bodyBuffer);
            System.out.println("Body: " + body); // log
         }

         HttpServletRequest req = new HttpServletRequest(socket.getInputStream(), headers, body.toString());
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);
         HttpServlet httpServlet = new UploadServlet();

         if (request.startsWith("POST")) {
            httpServlet.doPost(req, res);
         } else if (request.startsWith("GET")) {
            httpServlet.doGet(req, res);
         } else {
            PrintWriter out = new PrintWriter(res.getOutputStream(), true);
            out.println("HTTP/1.1 405 Method Not Allowed");
            out.println("Allow: GET, POST\n");
         }

         OutputStream out = socket.getOutputStream();
         out.write(baos.toByteArray());
         socket.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
