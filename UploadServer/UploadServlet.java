import java.io.*;

public class UploadServlet extends HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response) {
      response.setContentType("text/html");
      String htmlResponse = "<!DOCTYPE html>"
              + "<html>"
              + "<body>"
              + "<h2>HTML Forms</h2>"
              + "<form method='post' enctype='multipart/form-data'>"
              + "Caption: <input type='text' name='caption'/><br/><br/>"
              + "Date: <input type='date' name='date' /><br/>"
              + "<input type='file' name='fileName'/><br/><br/>"
              + "<input type='submit' value='Submit' />"
              + "</form>"
              + "</body>"
              + "</html>";
      response.writeResponse(htmlResponse);
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      System.out.println("INSIDE DOPOST METHOD"); // logging
      try {
         String body = request.getBody();
         System.out.println("Body received: " + body); // logging

         String boundary = "--" + request.getHeader("Content-Type").split("=")[1];
         String[] parts = body.split(boundary);

         // form data parts
         String caption = null;
         String date = null;
         String fileName = null;
         byte[] fileContent = null;

         for (String part : parts) {
            part = part.trim();
            if (part.isEmpty() || part.equals("--")) continue;

            String[] headersAndBody = part.split("\r\n\r\n", 2);
            if (headersAndBody.length < 2) continue;

            String headersPart = headersAndBody[0];
            String contentPart = headersAndBody[1].trim(); // body

            System.out.println("Headers: " + headersPart); // logging

            String[] headers = headersPart.split("\r\n");
            for (String header : headers) {
               if (header.startsWith("Content-Disposition")) {
                  System.out.println("Header: " + header); // logging

                  // filter out fields
                  String[] dispositionParts = header.split("; ");
                  for (String dispositionPart : dispositionParts) {
                     if (dispositionPart.startsWith("name=")) {
                        String fieldName = dispositionPart.split("=")[1].replace("\"", "");
                        System.out.println("Field Name: " + fieldName); // logging
                        if (fieldName.equals("caption")) {
                           caption = contentPart;
                        } else if (fieldName.equals("date")) {
                           date = contentPart;
                        }
                     } else if (dispositionPart.startsWith("filename=")) {
                        fileName = dispositionPart.split("=")[1].replace("\"", "");
                        System.out.println("File Name: " + fileName); // logging
                     }
                  }
               }
            }
            if (fileName != null) {
               fileContent = contentPart.getBytes();
            }
         }

         // create directory "files" if it doesn't exist
         File directory = new File("files");
         if (!directory.exists()) {
            directory.mkdir();
         }

         // make the new filename with caption and date -- append to front cause too lazy to separate file extension
         String newFileName = caption + "_" + date + "_" + fileName;
         File fileToSave = new File(directory, newFileName);

         try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
            fos.write(fileContent);
         }


         response.setContentType("text/plain");
         response.writeResponse("Upload successful! File saved as: " + newFileName);

      } catch (Exception ex) {
         System.err.println(ex);
      }
   }
}