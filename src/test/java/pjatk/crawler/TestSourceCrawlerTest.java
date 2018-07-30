package pjatk.crawler;

import java.util.List;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import pjatk.domain.crawler.CrawlQuery;
import pjatk.domain.data.DataBlock;

/**
 * @author Michał Dąbrowski
 */
@SpringBootTest
public class TestSourceCrawlerTest {

    @Test
    public void shouldReturnDataBlockList() {
        //given
        TestSourceCrawler testSourceCrawler = new TestSourceCrawler();
        testSourceCrawler.addDatum("Ala ma kota.");
        testSourceCrawler.addDatum("Kot ma Alę.");

        //when
        List<DataBlock> dataBlockList = testSourceCrawler.crawl(new CrawlQuery());

        //then
        assertThat(dataBlockList.size()).isEqualTo(2);
        assertThat(dataBlockList.get(0).getContent()).isEqualTo("Ala ma kota.");
    }

    @Test
    public void shouldReturnEmptyListWhenNoData() {
        //given
        TestSourceCrawler testSourceCrawler = new TestSourceCrawler();

        //when
        List<DataBlock> dataBlockList = testSourceCrawler.crawl(new CrawlQuery());

        //then
        assertThat(dataBlockList.isEmpty()).isTrue();
    }
}
