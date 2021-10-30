package com.jskang.storageclient.common;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestApi {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());
    HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).build();

    /**
     * Common rest api header.
     */
    private List<String> commonHeaders = Arrays.asList(
        "Accept", "application/json",
        "Content-type", "application/json;charset=UTF-8"
    );

    /**
     * Rest api GET method.
     *
     * @param url connect url.
     * @return server response.
     */
    public Object get(String url) {
        return this.get(url, null);
    }

    /**
     * Rest api GET method.
     *
     * @param url     connect url.
     * @param headers custom rest api header.
     * @return server response.
     */
    public Object get(String url, String[] headers) {

        // Common headers setting.
        if (headers != null) {
            for (String header : headers) {
                commonHeaders.add(header);
            }
        }

        String result = "";
        try {
            result = client.sendAsync(
                HttpRequest
                    .newBuilder(new URI("http://" + url))
                    .GET()
                    .headers(commonHeaders.toArray(String[]::new))
                    .build(),
                HttpResponse
                    .BodyHandlers
                    .ofString()
            ).thenApply(HttpResponse::body).get();
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        } catch (ExecutionException e) {
            LOG.error(e.getMessage());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }

        return Converter.jsonToMap(result);
    }

    /**
     * Rest api POST method.
     *
     * @param url     connect url.
     * @param headers custom rest api header.
     * @param data    json data.
     * @return server response.
     * @throws Exception
     */
    public Object post(String url, String[] headers, Map<?, ?> data) throws Exception {
        // Common headers setting.
        if (headers != null) {
            for (String header : headers) {
                commonHeaders.add(header);
            }
        }

        String requestBody = Converter.objToJson(data);
        if (requestBody.equals("null")) {
            requestBody = "";
        }
        BodyPublisher body = BodyPublishers.ofString(requestBody);

        String result = "";
        try {
            result = client.sendAsync(
                HttpRequest
                    .newBuilder(new URI("http://" + url))
                    .POST(body)
                    .headers(commonHeaders.toArray(String[]::new))
                    .build(),
                HttpResponse
                    .BodyHandlers
                    .ofString()
            ).thenApply(HttpResponse::body).get();
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        } catch (ExecutionException e) {
            LOG.error(e.getMessage());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }

        return Converter.jsonToMap(result);
    }

    /**
     * Rest api POST method.
     *
     * @param url  connect url.
     * @param file file path.
     * @return server response.
     * @throws IOException
     */
    public Object fileUpload(String url, @NotNull Path file) throws IOException {
        // Generate file boundary.
        String boundary = new BigInteger(256, new Random()).toString();
        String mimeType = Files.probeContentType(file);

        // Generate file form.
        Map<Object, Object> data = new LinkedHashMap<>();
        data.put("file", file);

        List<String> commonHeaders = Arrays.asList(
            "Accept", "multipart/form-data",
            "Content-type", "multipart/form-data;boundary=" + boundary
        );

        String result = "";
        try {
            BodyPublisher body = this.ofMimeMultipartData(data, boundary);

            result = client.sendAsync(
                HttpRequest
                    .newBuilder(new URI("http://" + url))
                    .POST(body)
                    .headers(commonHeaders.toArray(String[]::new))
                    .build(),
                HttpResponse
                    .BodyHandlers
                    .ofString()
            ).thenApply(HttpResponse::body).get();
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        } catch (ExecutionException e) {
            LOG.error(e.getMessage());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return Converter.jsonToMap(result);
    }

    /**
     * Generate file upload format.
     *
     * @param data     File form
     * @param boundary Random int value.
     * @return BodyPublisher.
     * @throws IOException
     */
    private BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
        String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\n"
            + "Content-Disposition: multipart/form-data;"
            + "name=").getBytes(StandardCharsets.UTF_8);

        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                if (mimeType == null) {
                    // If mimetype is null, it is judged as a binary file.
                    mimeType = "application/octet-stream";
                }

                byteArrays.add(("\"" + entry.getKey() + "\"; "
                    + "filename=\"" + path.getFileName() + "\"\r\n"
                    + "Content-Type: " + mimeType + "\r\n\r\n")
                    .getBytes(StandardCharsets.UTF_8));

                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n"
                    + entry.getValue() + "\r\n")
                    .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return BodyPublishers.ofByteArrays(byteArrays);
    }
}
