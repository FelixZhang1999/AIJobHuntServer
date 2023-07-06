package com.example.assist.scraping;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Basic scraper.
 */
public abstract class BaseScraper {

    /**
     * Fetch html from an url with retries when reach rate limit.
     * @param url The url to fetch.
     * @param maxRetries Max amount of retries.
     * @param retryDelayMillis Wait time to retry.
     * @return The html document of url.
     * @throws IOException
     */
    protected Document fetchUrlWithRetries(String url, int maxRetries, int retryDelayMillis) throws IOException {
        int retries = 0;
        Document document = null;

        while (retries <= maxRetries) {
            try {
                document = fetchUrl(url);
                break; // Successful response, exit the loop
            } catch (IOException e) {
                if (isRateLimitExceeded(e)) {
                    System.out.println("Rate limit exceeded. Retrying in " + retryDelayMillis + "ms...");
                    retries++;
                    sleep(retryDelayMillis);
                } else {
                    throw e; // Rethrow other IOExceptions
                }
            }
        }
        return document;
    }

    /**
     * Fetch HTML from an url.
     * @param url The url to fetch.
     * @return The html document.
     * @throws IOException
     */
    protected Document fetchUrl(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute();

        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return response.parse();
        } else {
            throw new IOException("HTTP Error: " + statusCode);
        }
    }

    /**
     * Determine whether reach rate limit.
     * @param e
     * @return
     */
    protected boolean isRateLimitExceeded(IOException e) {
        // Check if the exception message or status code indicates a rate limit error
        return e.getMessage().contains("429") || e.getMessage().contains("Too Many Requests");
    }

    /**
     * Sleep to wait for retry.
     * @param millis
     */
    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}