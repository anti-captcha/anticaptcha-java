package com.anti_captcha.Helper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * JSON-over-HTTP transport for the Anti-Captcha API, built on the JDK's own
 * {@link java.net.http.HttpClient}. No third-party HTTP library is needed.
 *
 * <p>TLS certificates are validated, as they should be. Earlier versions of this
 * library installed a trust-all {@code X509TrustManager} and called
 * {@code SSLContext.setDefault()}, which silently disabled certificate checking
 * for the whole JVM. That is gone.
 */
public class HttpHelper {

    /**
     * Shared across calls so connections are pooled. Building one per request
     * would leak file descriptors under load.
     */
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private HttpHelper() {
    }

    /**
     * POSTs a JSON body and returns the response body as a string.
     *
     * @param url            full URL to call
     * @param json           request body
     * @param timeoutMillis  how long to wait for the whole exchange
     * @return the raw response body
     * @throws IOException          when the API cannot be reached, or answers with a non-2xx code
     * @throws InterruptedException when the calling thread is interrupted while waiting
     */
    public static String post(String url, String json, int timeoutMillis)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeoutMillis))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .header("User-Agent", "anticaptcha-java")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("API answered with HTTP " + response.statusCode() + ": "
                    + shorten(response.body()));
        }

        return response.body();
    }

    private static String shorten(String body) {
        if (body == null || body.isEmpty()) {
            return "(empty response)";
        }

        return body.length() <= 500 ? body : body.substring(0, 500) + "...";
    }
}
