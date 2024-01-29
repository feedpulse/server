package io.feedpulse.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import io.feedpulse.exceptions.FetchException;
import io.feedpulse.exceptions.HtmlNotParsableException;
import io.feedpulse.exceptions.RomeFeedParseException;
import io.feedpulse.model.Feed;
import io.feedpulse.model.Keyword;
import io.feedpulse.repository.FeedRepository;
import io.feedpulse.util.FetchUtil;
import io.github.cdimascio.essence.Essence;
import io.github.cdimascio.essence.EssenceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The FeedFetchService class is responsible for fetching feeds, processing the entries, and storing them in the database.
 */
@Slf4j
@Service
public class FeedFetchService {

    private final FeedRepository feedRepository; // can't use the service because it would cause a circular dependency
    private final KeywordService keywordService;
    private final EntryService entryService;


    public FeedFetchService(FeedRepository feedRepository, KeywordService keywordService, @Lazy EntryService entryService) {
        this.feedRepository = feedRepository;
        this.keywordService = keywordService;
        this.entryService = entryService;
    }

    /**
     * Fetches feeds from the feed repository and processes each feed by calling the processFeed method.
     */
    public void fetchFeeds() {
        List<Feed> feeds = feedRepository.findAll();
        for (Feed feed : feeds) {
            log.info("Fetching feed: {}", feed.getFeedUrl());
            processFeed(feed);
        }
    }

    /**
     * Fetches a feed from the feed repository and processes it.
     *
     * @param feed The feed to fetch and process
     */
    public void fetchFeed(Feed feed) {
        processFeed(feed);
    }

    /**
     * Fetches a SyndFeed from the given feed URL.
     *
     * @param feedUrl The URL of the feed to fetch.
     * @return The fetched SyndFeed object.
     * @throws FetchException       If an error occurs while fetching the feed content.
     * @throws FeedException       If an error occurs while parsing the feed content.
     * @throws IllegalArgumentException If the feed URL is invalid.
     * @throws FetchException       If an error occurs while fetching the feed content.
     */
    public SyndFeed fetchFeed(String feedUrl) throws RomeFeedParseException, IllegalArgumentException, FetchException {
        URI uri = FetchUtil.isValidUrlOrNull(feedUrl);
        if (uri == null) {
            throw new FetchException(feedUrl);
        }
        Reader reader = FetchUtil.fetchAsReader(uri);
        try {
            return new SyndFeedInput().build(reader);
        } catch (FeedException e) {
            throw new RomeFeedParseException(e);
        }
    }

    /**
     * Processes a given feed by obtaining a reader for the feed, parsing the feed content using
     * SyndFeedInput, and then calling processEntries to process the feed's entries.
     *
     * @param feed The feed to be processed.
     */
    private void processFeed(Feed feed) {
        try (Reader reader = obtainReaderForFeed(feed)) {
            SyndFeed syndFeed = new SyndFeedInput().build(reader);
            processEntries(syndFeed.getEntries(), feed);

        } catch (FetchException | FeedException | HtmlNotParsableException | IOException e) {
            // TODO: handle exception
            log.error("Error while fetching feed: {}", feed.getFeedUrl());
        }
    }

    /**
     * Obtains a reader for the given feed by fetching the feed content as a reader object.
     *
     * @param feed The feed for which to obtain the reader.
     * @return A reader object for the feed content.
     * @throws FetchException If an error occurs while fetching the feed content.
     */
    private Reader obtainReaderForFeed(Feed feed) throws FetchException {
        URI uri = FetchUtil.isValidUrlOrNull(feed.getFeedUrl());
        if (uri == null) {
            throw new FetchException(feed.getFeedUrl());
        }
        return FetchUtil.fetchAsReader(uri);
    }

    /**
     * Processes the list of SyndEntry objects by calling the parseSyndEntry method for each entry.
     *
     * @param entries The list of SyndEntry objects to be processed.
     * @param feed The Feed object associated with the entries.
     * @throws HtmlNotParsableException If there is an error while parsing the HTML content of an entry.
     */
    private void processEntries(List<SyndEntry> entries, Feed feed) throws HtmlNotParsableException {
        for (SyndEntry syndEntry : entries) {
            parseSyndEntry(syndEntry, feed);
        }
    }


    /**
     * Parses a SyndEntry object by extracting keywords and page content data, and adds the entry to the feed if it does not exist already.
     *
     * @param syndEntry The SyndEntry object to parse.
     * @param feed      The Feed object associated with the entry.
     * @throws HtmlNotParsableException If there is an error while parsing the HTML content of the entry.
     */
    private void parseSyndEntry(SyndEntry syndEntry, Feed feed) throws HtmlNotParsableException {
        Set<Keyword> keywords = parseKeywords(syndEntry);
        EssenceResult data = parsePageContent(syndEntry);
        entryService.addIfNotExists(feed, syndEntry, keywords, data);
    }

    /**
     * Parses the keywords from the syndicated entry by retrieving the categories and mapping them to keyword objects.
     *
     * @param syndEntry The syndicated entry object from which to extract the keywords.
     * @return A set of Keyword objects representing the extracted keywords.
     */
    private Set<Keyword> parseKeywords(SyndEntry syndEntry) {
        Set<String> keywords = syndEntry.getCategories().stream().map(SyndCategory::getName).collect(Collectors.toSet());
        return keywordService.addMissingKeywords(keywords);
    }

    /**
     * Parses the page content of a SyndEntry object and returns an EssenceResult.
     *
     * @param syndEntry The SyndEntry object whose page content needs to be parsed.
     * @return An EssenceResult object containing the parsed data.
     * @throws HtmlNotParsableException If there is an error while parsing the HTML content of the entry.
     */
    public EssenceResult parsePageContent(SyndEntry syndEntry) throws HtmlNotParsableException {
        String html = null;
        try {
            URI uri = FetchUtil.isValidUrlOrNull(syndEntry.getLink());
            if (uri == null) throw new HtmlNotParsableException(syndEntry.getLink());
            html = FetchUtil.fetchAsText(uri);
        } catch (IOException | FetchException e) {
            throw new HtmlNotParsableException(syndEntry.getLink());
        }
        return Essence.extract(html);
    }

}
