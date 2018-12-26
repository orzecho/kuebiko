package pjatk.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataSource;
import pjatk.domain.data.Tag;
import pjatk.domain.service.DataBlockService;
import pjatk.persist.TagService;

/**
 * @author Michał Dąbrowski
 */
@Component
public class GuardianCrawler extends WebCrawler {

    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern DATE_SEARCH_PATTERN = Pattern.compile(".*(\\d{4}.\\w{3}.\\d{2}).*");
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}).(\\w{3}).(\\d{2})");
    private static final Pattern LIVE_PATTERN = Pattern.compile(".*\\/live\\/.*");
    private static final Pattern GALLERY_PATERN = Pattern.compile(".*\\/gallery\\/.*");

    private boolean processLive = false;

    private final DataBlockService dataBlockService;
    private final TagService tagService;

    public GuardianCrawler(TagService tagService, DataBlockService dataBlockService) {
        this.tagService = tagService;
        this.dataBlockService = dataBlockService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }
        return href.startsWith("https://www.theguardian.com/");
    }

    @Override
    @Transactional
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String dateString;
        String articleBody = null;
        Matcher dateMatcher = DATE_SEARCH_PATTERN.matcher(url);
        Matcher liveMatcher = LIVE_PATTERN.matcher(url);
        Matcher galleryMatcher = GALLERY_PATERN.matcher(url);
        List<Tag> tags = new ArrayList<>();
        if(dateMatcher.matches() && (processLive || !liveMatcher.matches()) && !galleryMatcher.matches()) {
            dateString = dateMatcher.group(1);

            if (page.getParseData() instanceof HtmlParseData) {
                org.jsoup.nodes.Document document = Jsoup.parse(((HtmlParseData) page.getParseData()).getHtml());

                articleBody = document.body().select("div").select(".content__main").text();

                tags = document.body().select("div").select(".submeta__link-item").eachText()
                        .stream().map(tagService::findOrCreateTag).collect(Collectors.toList());
            }
            if (articleBody != null && !articleBody.isEmpty()) {
                DataBlock dataBlock = DataBlock.builder()
                        .origin(DataSource.GUARDIAN)
                        .content(articleBody)
                        .tags(tags)
                        .date(parseDateString(dateString).orElse(null))
                        .word2VecUnprocessed(true)
                        .paragraphVectorsUnprocessed(true)
                        .build();
                dataBlock.createContentHash();

                dataBlockService.save(dataBlock);
            }
        }
    }

    public Optional<LocalDate> parseDateString(String dateString) {
        Optional<String> year;
        Optional<Integer> month;
        Optional<String> day;
        Matcher dateMatcher = DATE_PATTERN.matcher(dateString);
        if(dateMatcher.find()) {
            year = Optional.of(dateMatcher.group(1));
            month = Optional.of(DateTimeFormatter.ofPattern("MMM")
                        .withLocale(Locale.ENGLISH).parse(firstCharacterToUpper(dateMatcher.group(2))).get(ChronoField.MONTH_OF_YEAR));
            day = Optional.of(dateMatcher.group(3));
        } else {
            year = Optional.empty();
            month = Optional.empty();
            day = Optional.empty();
        }

        return year.flatMap(y -> month
                .flatMap(m -> day
                        .flatMap(d -> Optional.of(LocalDate.of(Integer.valueOf(y), Month.of(m), Integer.valueOf(d))))));
    }

    private String firstCharacterToUpper(String string) {
        String firstLetter = string.substring(0,1).toUpperCase();
        String lowerPart = string.substring(1).toLowerCase();
        return firstLetter + lowerPart;
    }

    public String stripHtml(String nodeValue) {
        Pattern htmlTagPattern = Pattern.compile("<[^>]*>");
        Matcher matcher = htmlTagPattern.matcher(nodeValue);
        return matcher.replaceAll("");
    }

    private Document parseHtml(String html) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            return dBuilder.parse(html);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}