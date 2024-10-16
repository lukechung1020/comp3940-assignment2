import java.io.*;

public class HttpServletResponse {
   private OutputStream outputStream = null;
   private String contentType;
   private PrintWriter writer;

   public HttpServletResponse(OutputStream outputStream) {
      this.outputStream = outputStream;
      this.writer = new PrintWriter(outputStream);
   }

   public OutputStream getOutputStream() {
      return this.outputStream;
   }

   public void setContentType(String type) {
      this.contentType = type;
   }

   public void sendResponseHeaders() {
      writer.println("HTTP/1.1 200 OK");
      if (contentType != null) {
         writer.println("Content-Type: " + contentType);
      }
      writer.println();
   }

   public void writeResponse(String responseBody) {
      sendResponseHeaders();
      writer.println(responseBody);
      writer.flush();
   }
}
