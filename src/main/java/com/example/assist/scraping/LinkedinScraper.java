package com.example.assist.scraping;

import com.example.assist.model.JobContent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Scrape from linkedin
 */
@Component
public class LinkedinScraper extends BaseScraper {

    private final Logger logger = LoggerFactory.getLogger(LinkedinScraper.class);
    private final String BASE_URL = "https://www.linkedin.com/jobs/search?";

    protected String getFullUrl(final String title, @Nullable final String location, final int nextStart) {
        String fullUrl = BASE_URL + "keywords=" + title.replace(" ", "%2B");
        if (location != null){
            fullUrl += "&location=" + location.replace(" ", "%2B");
        }
        return fullUrl;
    }

    protected List<String> getLinksFromURL(final String url, final int size, final int nextStart) {
        List<String> links = new ArrayList<String>();
        try {
            final Document document = Jsoup.connect(url).get();
            final Elements linkElements = document.select("a.base-card__full-link");
            for (int i = nextStart; i < 25; i++) {
                final Element linkElement = linkElements.get(i);
                links.add(linkElement.attr("href"));
                if (links.size() >= size) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return links;
    }

    protected JobContent scrapeJobContent(final String url){
        final JobContent jobContent = JobContent.builder().url(url).build();
        try {
            final Document document = fetchUrlWithRetries(url, MAX_RETRIES, RETRY_DELAY_MILLIS);
            final Element jobTitleElement = document.selectFirst("h1.top-card-layout__title");
            jobContent.setTitle(jobTitleElement.text());

            final Element companyElement = document.selectFirst("a.topcard__org-name-link");
            final String companyName = companyElement.text().trim();
            jobContent.setCompany(companyName);

            final Element locationElement = document.selectFirst("span.topcard__flavor--bullet");
            final String location = locationElement.text().trim();
            jobContent.setLocation(location);

            final Element descriptionElement = document.selectFirst("div.show-more-less-html__markup");
            final String description = descriptionElement.text().trim();
            jobContent.setDescription(description);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return jobContent;
    }
}
