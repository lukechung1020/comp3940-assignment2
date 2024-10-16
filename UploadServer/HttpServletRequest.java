import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
   private InputStream inputStream = null;
   private Map<String, String> headers = new HashMap<>();
   private String body = null;

   public HttpServletRequest(InputStream inputStream, Map<String, String> headers, String body) {
      this.inputStream = inputStream;
      this.headers = headers;
      this.body = body;
   }

   public String getHeader(String headerName) {
      return headers.get(headerName);
   }

   public String getBody() {
      return body;
   }
}
