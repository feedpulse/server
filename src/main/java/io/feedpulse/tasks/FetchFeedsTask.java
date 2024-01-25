package io.feedpulse.tasks;

import io.feedpulse.service.FeedFetchService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
public class FetchFeedsTask {

    private final FeedFetchService feedFetchService;

    public FetchFeedsTask(FeedFetchService feedFetchService) {
        this.feedFetchService = feedFetchService;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60, initialDelay = 1000 * 60)
    public void scheduleFetchFeeds() {
        System.out.println("Fetching feeds...");
        feedFetchService.fetchFeeds();
        System.out.println("Feeds fetched");
    }
}
