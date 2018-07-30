package pjatk.crawler;

import pjatk.domain.crawler.CrawlQuery;
import pjatk.domain.data.DataBlock;

import java.util.List;

/**
 * @author Michał Dąbrowski
 */
public interface SourceCrawlerController {
    List<DataBlock> crawl(CrawlQuery crawlQuery);
}
