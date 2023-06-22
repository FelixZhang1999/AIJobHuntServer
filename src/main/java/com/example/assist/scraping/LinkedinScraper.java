package com.example.assist.scraping;

import com.example.assist.model.JobContent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LinkedinScraper extends BaseScraper {

    private final int maxRetries = 3;
    private final int retryDelayMillis = 1000;
    private final Logger logger = LoggerFactory.getLogger(LinkedinScraper.class);
    private final String baseUrl = "https://www.linkedin.com/jobs/search?";

    public List<JobContent> scrapeJobs(final String title, final int size){
        final String url = getFullUrl(title);
        final List<String> links = getLinksFromURL(url, size);
        return links.stream().map(this::scrapeJobContent).collect(Collectors.toList());
    }

    protected String getFullUrl(final String title) {
        final String formatTitle = title.replace(" ", "%2B");
        return baseUrl + "keywords=" + formatTitle;
    }

    protected List<String> getLinksFromURL(final String url, final int size) {
        List<String> links = new ArrayList<String>();
        try {
            final Document document = Jsoup.connect(url).get();
            final Elements linkElements = document.select("a.base-card__full-link");
            for (final Element linkElement : linkElements) {
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
            final Document document = fetchUrlWithRetries(url, maxRetries, retryDelayMillis);
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
