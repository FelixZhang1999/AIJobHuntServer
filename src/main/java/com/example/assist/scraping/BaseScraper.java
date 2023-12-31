package com.example.assist.scraping;

import com.example.assist.model.JobContent;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.lang.Nullable;

/**
 * Basic scraper.
 */
public abstract class BaseScraper {

    protected final int MAX_RETRIES = 3;
    protected final int RETRY_DELAY_MILLIS = 1000;

    /**
     * Scrape job postings.
     * @param title The desired title of jobs.
     * @param location The desired location of jobs.
     * @param size The number of jobs to scrape.
     * @return A list of job postings.
     */
    public List<JobContent> scrapeJobs(final String title, @Nullable final String location, final int size,
                                        final int nextStart) {
        if (size <= 0) {
            return ImmutableList.of();
        }
        final String url = getFullUrl(title, location, nextStart);
        final List<String> links = getLinksFromURL(url, size, nextStart);
        return links.stream().map(this::scrapeJobContent).collect(Collectors.toList());
    }

    /**
     * Create full url according to title and location.
     * @param title The desired title of jobs.
     * @param location The desired location of jobs.
     * @return The full url.
     */
    protected abstract String getFullUrl(final String title, @Nullable final String location, final int nextStart);

    /**
     * Get job posting links from Linkedin search url.
     * @param url The linkedin search url.
     * @param size The number of jobs to scrape.
     * @return A list of job postings url.
     */
    protected abstract List<String> getLinksFromURL(final String url, final int size, final int nextStart);

    /**
     * Scrape job posting url to get job posting contents.
     * @param url A job postings url
     * @return The job posting contents.
     */
    protected abstract JobContent scrapeJobContent(final String url);

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