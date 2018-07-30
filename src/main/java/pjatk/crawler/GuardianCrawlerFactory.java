package pjatk.crawler;

import org.springframework.stereotype.Component;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pjatk.persist.DataBlockRepository;
import pjatk.persist.TagService;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class GuardianCrawlerFactory implements CrawlController.WebCrawlerFactory<GuardianCrawler> {

    private final DataBlockRepository dataBlockRepository;
    private final TagService tagService;

    @Override
    public GuardianCrawler newInstance() throws Exception {
        return new GuardianCrawler(dataBlockRepository, tagService);
    }
}
