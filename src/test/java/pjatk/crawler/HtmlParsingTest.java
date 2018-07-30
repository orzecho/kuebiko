package pjatk.crawler;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michał Dąbrowski
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HtmlParsingTest {
    @Autowired
    GuardianCrawler guardianCrawler;

    @Test
    public void stripHtmlTest() {
        String html = "<article>Ala ma <b>kota</b></article>";
        String striped = guardianCrawler.stripHtml(html);

        assertThat(striped).isEqualTo("Ala ma kota");
    }

    @Test
    public void parseDateTest() {
        String date = "2018/mar/19";
        Optional<LocalDate> parsed = guardianCrawler.parseDateString(date);

        assertThat(parsed.get().getYear()).isEqualTo(2018);
        assertThat(parsed.get().getMonth()).isEqualTo(Month.MARCH);
        assertThat(parsed.get().getDayOfMonth()).isEqualTo(19);
    }

    @Test
    public void parseDateTestInvalidDate() {
        String date = "20/mar/19";
        Optional<LocalDate> parsed = guardianCrawler.parseDateString(date);

        assertThat(parsed.isPresent()).isFalse();
    }
}
