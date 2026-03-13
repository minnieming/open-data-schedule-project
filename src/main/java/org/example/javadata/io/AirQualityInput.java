package org.example.javadata.io;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Slf4j
@Component
public class AirQualityInput {

    private final HttpClient httpClient;

    public AirQualityInput() {
        try {
            TrustManager[] trustAll = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
            }};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());
            this.httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("HttpClient 초기화 실패", e);
        }
    }

    private String lastUri;

    public String getLastUri() { return lastUri; }

    @Value("${airquality.api.base-url}")
    private String baseUrl;

    @Value("${airquality.api.service-key}")
    private String serviceKey;

    @Value("${airquality.api.sido-name}")
    private String sidoName;

    public String callApi(int pageNo, int numOfRows) throws Exception {
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl.trim())
                .queryParam("serviceKey", serviceKey.trim())
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("returnType", "json")
                .queryParam("sidoName", sidoName)
                .queryParam("ver", "1.0")
                .build(false)
                .encode()
                .toUri();

        lastUri = uri.toString();
        log.info("[AirQualityInput] 호출 URI: {}", uri);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("대기오염 API 호출 실패 status=" + response.statusCode() + " body=" + response.body());
        }

        return response.body();
    }
}
