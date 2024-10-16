import java.io.*;
import java.net.*;

public class UploadClient {
    public UploadClient() {
    }

    public String uploadFile() {

        String headers = "";
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String host = "localhost"; // Change this to your target host
        String endpoint = "/upload/upload";
        int port = 8081; // Default HTTP port

        // Prepare the request body
        ByteArrayOutputStream requestBodyStream = new ByteArrayOutputStream();

        try {

            // Connect to the server via a socket
            Socket socket = new Socket(host, port);

            // Add a form field
            addFormField(requestBodyStream, "caption", "logo", boundary);
            addFormField(requestBodyStream, "date", "10_16_2024", boundary);

            // Add the image file part
            File imageFile = new File("AndroidLogo.png");
            addFilePart(requestBodyStream, "File", imageFile, boundary, "image/png");

            // Close the multipart boundary
            String closingBoundary = "--" + boundary + "--" + "\r\n";
            requestBodyStream.write(closingBoundary.getBytes("UTF-8"));

            // Calculate the Content-Length
            int contentLength = requestBodyStream.size();

            // Construct the HTTP request headers
            headers = "POST " + endpoint + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n" +
                    "Content-Length: " + contentLength + "\r\n" +
                    "Connection: close\r\n" + // Close the connection after the request
                    "\r\n";

            // Write headers and body to the socket's output stream
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(headers.getBytes("UTF-8"));
            requestBodyStream.writeTo(outputStream);
            outputStream.flush();

            // Read the server's response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            System.out.println("Server Response: ");
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }

            // Close streams and the socket
            in.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        return headers + requestBodyStream.toString();
    }

    public void addFormField(ByteArrayOutputStream requestBodyStream, String fieldName, String fieldValue,
            String boundary) throws IOException {
        String charset = "UTF-8";
        String part = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n" +
                "\r\n" +
                fieldValue + "\r\n";
        requestBodyStream.write(part.getBytes(charset));
    }

    public void addFilePart(ByteArrayOutputStream requestBodyStream, String fieldName, File file, String boundary,
            String contentType) throws IOException {
        String charset = "UTF-8";
        String partHeader = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "\r\n";
        requestBodyStream.write(partHeader.getBytes(charset));

        // Read the file's content and write it to the requestBodyStream
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            requestBodyStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();

        requestBodyStream.write("\r\n".getBytes(charset));
    }
}