import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
   private InputStream inputStream = null;
   private Map<String, String> headers = new HashMap<>();

   public HttpServletRequest(InputStream inputStream, Map<String, String> headers) {
      this.inputStream = inputStream;
      this.headers = headers;
   }

   public InputStream getInputStream() {
      return inputStream;
   }

   public String getHeader(String headerName) {
      return headers.get(headerName);
   }
}