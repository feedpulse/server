package dev.feder.util;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FeedUtil {

    public FeedUtil() {
    }

    public static SyndFeed fetchFeed(String url) throws MalformedURLException, FeedException {
        URL urlObj;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            urlObj = new URL("https://" + url);
        }
        InputStream is = urlToInputStream(urlObj, new HashMap<>());
        Reader reader = new InputStreamReader(is);
        SyndFeed feed = new SyndFeedInput().build(reader);
        System.out.println("Feed fetched: " + feed.getPublishedDate());
        return feed;
    }

    public static String fetchHtml(String url) throws IOException, URISyntaxException {
        URI uri = new URI(url);
        URL urlObj = new URL(uri.toASCIIString());
        InputStream is = urlToInputStream(urlObj, new HashMap<>());
        String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return text;
    }

    private static InputStream urlToInputStream(URL url, Map<String, String> args) {
        HttpURLConnection con = null;
        InputStream inputStream = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            if (args != null) {
                for (Map.Entry<String, String> e : args.entrySet()) {
                    con.setRequestProperty(e.getKey(), e.getValue());
                }
            }
            con.connect();
            int responseCode = con.getResponseCode();
            /* By default, the connection will follow redirects. The following
             * block is only entered if the implementation of HttpURLConnection
             * does not perform the redirect. The exact behavior depends on
             * the actual implementation (e.g. sun.net).
             * !!! Attention: This block allows the connection to
             * switch protocols (e.g. HTTP to HTTPS), which is <b>not</b>
             * default behavior. See: https://stackoverflow.com/questions/1884230
             * for more info!!!
             */
            if (responseCode < 400 && responseCode > 299) {
                String redirectUrl = con.getHeaderField("Location");
                try {
                    URL newUrl = new URL(redirectUrl);
                    return urlToInputStream(newUrl, args);
                } catch (MalformedURLException e) {
                    URL newUrl = new URL(url.getProtocol() + "://" + url.getHost() + redirectUrl);
                    return urlToInputStream(newUrl, args);
                }
            }
            /*!!!!!*/

            inputStream = con.getInputStream();
            return inputStream;
        } catch (Exception e) {
            System.err.println("Error while fetching URL: " + url);
            throw new RuntimeException(e); // TODO: handle exception
            // a 429 status code would cause this exception
        }
    }
}
