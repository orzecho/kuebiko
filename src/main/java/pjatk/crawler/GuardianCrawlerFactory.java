package pjatk.crawler;

import org.springframework.stereotype.Component;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pjatk.domain.service.DataBlockService;
import pjatk.persist.TagRepository;
import pjatk.persist.TagService;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class GuardianCrawlerFactory implements CrawlController.WebCrawlerFactory<GuardianCrawler> {

    private final DataBlockService dataBlockService;
    private final TagService tagService;
    private final TagRepository tagRepository;

    @Override
    public GuardianCrawler newInstance() throws Exception {
        return new GuardianCrawler(tagService, dataBlockService);
    }
}
