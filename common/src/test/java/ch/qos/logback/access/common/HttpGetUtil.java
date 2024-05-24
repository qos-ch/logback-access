package ch.qos.logback.access.tomcat_10_1;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpGetUtil {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    URL url;

    HttpURLConnection conn;

    public HttpGetUtil(String urlStr) throws MalformedURLException {
        this.url = new URL(urlStr);
    }

    public HttpURLConnection connect() {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("Failed to connect");
            return null;
        }
    }

    String readResponse(HttpURLConnection conn) {
        if(conn == null)
            return null;

        try {
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                return innerReadResponse(conn);
            } else {
                logger.atError().addKeyValue("status", responseCode).log("Failed response");
                return null;
            }
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("failed to read status");
            return  null;
        }
    }



    private String innerReadResponse(HttpURLConnection conn) {
        try (InputStream is = conn.getInputStream()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String inputLine;
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            return buffer.toString();
        } catch (IOException e) {
            logger.atError().addKeyValue("url", url.toString()).setCause(e).log("failed");
            return null;
        }
    }


}
