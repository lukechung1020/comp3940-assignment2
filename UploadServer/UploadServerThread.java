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
         System.out.println("Request: " + request); // remove later

         Map<String, String> headers = new HashMap<>();
         String headerLine;

         while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
               headers.put(headerParts[0], headerParts[1]);
            }
         }
         System.out.println("Headers: " + headers); // remove later

         int contentLength = 0;
         if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
         }

         // remove later
         StringBuilder body = new StringBuilder();
         if (request.startsWith("POST") && contentLength > 0) {
            char[] bodyBuffer = new char[contentLength];
            in.read(bodyBuffer, 0, contentLength);
            body.append(bodyBuffer);
            System.out.println("Body: " + body);
         }

         HttpServletRequest req = new HttpServletRequest(socket.getInputStream(), headers);
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
      } catch (Exception e) { e.printStackTrace(); }
   }
}
