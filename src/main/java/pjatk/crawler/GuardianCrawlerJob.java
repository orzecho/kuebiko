package pjatk.crawler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class GuardianCrawlerJob {
    private final GuardianCrawlerFactory guardianCrawlerFactory;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void crawl() throws Exception {
        log.info("Starting crawl job.");
        String crawlStorageFolder = "./data/crawl/root";
        int numberOfCrawlers = 6;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://www.theguardian.com/");

        controller.start(guardianCrawlerFactory, numberOfCrawlers);
        log.info("Ended crawl job.");
    }
}
