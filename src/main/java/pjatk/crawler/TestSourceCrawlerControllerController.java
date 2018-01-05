package pjatk.crawler;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pjatk.domain.crawler.CrawlQuery;
import pjatk.domain.data.DataBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@Component
public class TestSourceCrawlerControllerController implements SourceCrawlerController {
    private List<String> rawData = new ArrayList<>();

    @Override
    public List<DataBlock> crawl(CrawlQuery crawlQuery) {
        return rawData.stream()
                .map(datum -> DataBlock.builder()
                        .content(datum)
                        .build())
                .collect(Collectors.toList());
    }

    public TestSourceCrawlerControllerController addDatum(String datum) {
        this.rawData.add(datum);
        return this;
    }


}
