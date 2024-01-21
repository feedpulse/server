package dev.feder.util;

import dev.feder.exceptions.FetchException;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for fetching content from a given URL.
 */
public final class FetchUtil {

    //Note: This client is shared across all instances of the class.
    private static final HttpClient client = HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(java.time.Duration.ofSeconds(20))
            .build();

    public FetchUtil() {
    }

    /**
     * Fetches the content from the given URI as a Reader.
     *
     * @param uri The URI of the content to fetch.
     * @return A Reader object representing the fetched content.
     * @throws FetchException If an error occurs while fetching the content.
     */
    public static Reader fetchAsReader(URI uri) throws FetchException {
        HttpResponse<InputStream> response = fetch(uri);
        return new InputStreamReader(response.body());
    }

    /**
     * Fetches the content from the given URI as text.
     *
     * @param uri The URI of the content to fetch.
     * @return A String representing the fetched content.
     * @throws FetchException If an error occurs while fetching the content.
     * @throws IOException    If an I/O error occurs while reading the response body.
     */
    public static String fetchAsText(URI uri) throws FetchException, IOException {
        HttpResponse<InputStream> response = fetch(uri);
        return new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Fetches the content from the given URI as an InputStream.
     *
     * @param uri The URI of the content to fetch.
     * @return An InputStream object representing the fetched content.
     * @throws FetchException If an error occurs while fetching the content.
     */
    public static InputStream fetchAsInputStream(URI uri) throws FetchException {
        HttpResponse<InputStream> response = fetch(uri);
        return response.body();
    }

    /**
     * Checks if the given URL string is valid. Returns null if the URL is not valid and a {@link URI} object if it is.
     *
     * @param url The URL string to validate.
     * @return A {@link URI} object representing the valid URL, or null if the URL is not valid or null.
     */
    @Nullable
    public static URI isValidUrlOrNull(String url) {
        if (url == null) return null;
        try {
            return new URL(url).toURI();
        } catch (MalformedURLException e) {
            if (url.startsWith("http://") || url.startsWith("https://")) return null;
            return isValidUrlOrNull("https://" + url);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Fetches the content from the given URI.
     *
     * @param uri The URI of the content to fetch.
     * @return An HttpResponse containing the fetched content as an InputStream.
     * @throws FetchException If an error occurs while fetching the content.
     */
    public static HttpResponse<InputStream> fetch(URI uri) throws FetchException {
        try {
            var request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            checkStatus(response, uri);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FetchException(uri.toString());
        }
    }

    /**
     * Checks the status code of the given HTTP response. If the status code is an error code (>= 400 or < 200),
     * throws a FetchException.
     *
     * @param response The HTTP response to check.
     * @param uri      The URI associated with the response.
     * @throws FetchException If the status code is an error code.
     */
    private static void checkStatus(HttpResponse<InputStream> response, URI uri) throws FetchException {
        int statusCode = response.statusCode();
        if (statusCode >= 400 || statusCode < 200) {
            System.out.println("Status code: " + statusCode);
            throw new FetchException(uri.toString());
        }
    }



}
