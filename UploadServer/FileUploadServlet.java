import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUploadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");
        String htmlResponse = "<!DOCTYPE html>"
                + "<html>"
                + "<body>"
                + "<h2>File Upload from FileUploadServlet</h2>"
                + "<form method='post' enctype='multipart/form-data'>"
                + "Caption: <input type='text' name='caption'/><br/><br/>"
                + "Date: <input type='date' name='date' /><br/>"
                + "<input type='file' name='fileName'/><br/><br/>"
                + "<input type='submit' value='Upload' />"
                + "</form>"
                + "</body>"
                + "</html>";
        response.writeResponse(htmlResponse);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("INSIDE FileUploadServlet DOPOST METHOD"); // logging
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

                System.out.println("FileUploadServlet Headers: " + headersPart); // logging

                String[] headers = headersPart.split("\r\n");
                for (String header : headers) {
                    if (header.startsWith("Content-Disposition")) {
                        System.out.println("FileUploadServlet Header: " + header); // logging

                        // filter out fields
                        String[] dispositionParts = header.split("; ");
                        for (String dispositionPart : dispositionParts) {
                            if (dispositionPart.startsWith("name=")) {
                                String fieldName = dispositionPart.split("=")[1].replace("\"", "");
                                System.out.println("FileUploadServlet Field Name: " + fieldName); // logging
                                if (fieldName.equals("caption")) {
                                    caption = contentPart;
                                } else if (fieldName.equals("date")) {
                                    date = contentPart;
                                }
                            } else if (dispositionPart.startsWith("filename=")) {
                                fileName = dispositionPart.split("=")[1].replace("\"", "");
                                System.out.println("FileUploadServlet File Name: " + fileName); // logging
                            }
                        }
                    }
                }
                if (fileName != null) {
                    fileContent = contentPart.getBytes();
                }
            }

            // Create directory "files" if it doesn't exist
            File directory = new File("files");
            if (!directory.exists()) {
                directory.mkdir();
                System.out.println("FileUploadServlet: Created 'files' directory"); // logging
            }

            // Make the new filename with caption and date
            String newFileName = caption + "_" + date + "_" + fileName;
            File fileToSave = new File(directory, newFileName);

            // Save the file
            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                fos.write(fileContent);
                System.out.println("FileUploadServlet: File saved as " + newFileName);
            }

            // Now generate a sorted listing of the files folder
            List<String> fileNames = new ArrayList<>();
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }

            // Sort the file names alphabetically
            Collections.sort(fileNames);

            // Generate HTML response
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html>")
                    .append("<html>")
                    .append("<body>")
                    .append("<h2>Uploaded Files</h2>")
                    .append("<ul>");
            for (String name : fileNames) {
                htmlResponse.append("<li>").append(name).append("</li>");
            }
            htmlResponse.append("</ul>")
                    .append("</body>")
                    .append("</html>");

            response.setContentType("text/html");
            response.writeResponse(htmlResponse.toString());

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
