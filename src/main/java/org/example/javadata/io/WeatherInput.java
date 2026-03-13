package org.example.javadata.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class WeatherInput {

    private final HttpClient httpClient;

    public WeatherInput() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            this.httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("HttpClient 초기화 실패", e);
        }
    }

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

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException(
                    "API 호출 실패 status=" + response.statusCode() + " body=" + response.body()
            );
        }

        return response.body();
    }
}
