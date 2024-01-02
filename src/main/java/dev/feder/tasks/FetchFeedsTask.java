package dev.feder.tasks;

import com.rometools.rome.io.FeedException;
import dev.feder.exceptions.HtmlNotParsableException;
import dev.feder.service.EntryService;
import dev.feder.service.FeedService;
import dev.feder.util.FeedUtil;
import io.github.cdimascio.essence.Essence;
import io.github.cdimascio.essence.EssenceResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Component
@Configuration
@EnableScheduling
public class FetchFeedsTask {

    private final FeedService feedService;
    private final EntryService entryService;

    public FetchFeedsTask(FeedService feedService, EntryService entryService) {
        this.feedService = feedService;
        this.entryService = entryService;
        System.out.println("FetchFeedsTask initialized");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60) // every hour
    public void scheduleFetchFeeds() {
        System.out.println("Fetching feeds...");
        feedService.getFeeds().forEach(feed -> {
            System.out.println("Fetching feed: " + feed.getTitle());
            try {
                FeedUtil.fetchFeed(feed.getFeedUrl()).getEntries().forEach(entry -> {
                    System.out.println("Entry Link " + entry.getLink());
                    String html = null;
                    try {
                        html = FeedUtil.fetchHtml(entry.getLink());
                    } catch (IOException | URISyntaxException e) {
                        throw new HtmlNotParsableException(entry.getLink());
                    }
                    EssenceResult data = Essence.extract(html);

                    if (entryService.getEntryByLink(entry.getLink()).isEmpty()) {
                        System.out.println("Adding entry: " + data.getCanonicalLink());
                        System.out.println(data.getDescription());
                        entryService.addEntry(entry, feed, data);
                    }
                });
            } catch (MalformedURLException | FeedException | HtmlNotParsableException e) {
                e.printStackTrace();
                //TODO: handle exception
                // add field in Feed.java to indicate that it could not be fetched
                // and display it in the frontend with a something like a warning triangle
            }
        });
        System.out.println("Feeds fetched");
    }
}
