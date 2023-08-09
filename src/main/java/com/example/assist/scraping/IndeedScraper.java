package com.example.assist.scraping;

import com.example.assist.model.JobContent;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class IndeedScraper extends BaseScraper {
    
    private final int JOBS_PER_PAGE = 15;
    private final Logger logger = LoggerFactory.getLogger(IndeedScraper.class);
    private final String BASE_URL = "https://www.indeed.com/jobs?";
    private final String JAVASCRIPT_SCRIPT = "return window.mosaic.providerData['mosaic-provider-jobcards'];";

    protected String getFullUrl(final String title, @Nullable final String location, final int nextStart) {
        String fullUrl = BASE_URL + "q=" + title.replace(" ", "+");
        if (location != null){
            fullUrl += "&l=" + location.replace(" ", "+")
                                        .replace(",", "%2C");
        }
        if (nextStart != 0) {
            fullUrl += "&start=" + (nextStart / JOBS_PER_PAGE * 10);
        }
        return fullUrl;
    }
    protected List<String> getLinksFromURL(final String url, final int size, final int nextStart) {
        List<String> links = new ArrayList<String>();
        return links;
    }
    protected JobContent scrapeJobContent(final String url) {
        return JobContent.builder().build();
    }
}
