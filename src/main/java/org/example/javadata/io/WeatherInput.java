package org.example.javadata.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class WeatherInput {

    @Value("${safety.api.base-url}")
    private String baseUrl;

    @Value("${safety.api.service-key}")
    private String serviceKey;

    public String callApi(int pageNo, int numOfRows) throws Exception {

        String cleanedBaseUrl = baseUrl == null ? "" : baseUrl.trim();
        String cleanedServiceKey = serviceKey == null ? "" : serviceKey.trim();

        URI uri = UriComponentsBuilder
                .fromUriString(cleanedBaseUrl)
                .queryParam("serviceKey", cleanedServiceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .build(true)
                .toUri();

        System.out.println("FINAL URL=[" + uri + "]");

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int code = conn.getResponseCode();
        InputStream stream = (code >= 200 && code < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            if (code < 200 || code >= 300) {
                throw new RuntimeException(
                        "API 호출 실패 status=" + code + " body=" + sb
                );
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }
}