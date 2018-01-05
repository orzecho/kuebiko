package pjatk.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pjatk.domain.data.DataBlock;
import pjatk.domain.data.DataSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michał Dąbrowski
 */
public class GuardianCrawler extends WebCrawler {

    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }
        return href.startsWith("https://www.theguardian.com/");
    }

    @Override
    public void visit(Page page) {
        Pattern datePattern = Pattern.compile(".*(\\d{4}.\\w{3}.\\d{2}).*");
        String url = page.getWebURL().getURL();
        String dateString;
        String article = null;
        Matcher dateMatcher = datePattern.matcher(url);
        if(dateMatcher.matches()) {
            dateString = dateMatcher.group(1);

            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String html = htmlParseData.getHtml();

                Document document = parseHtml(html);

                NodeList nodeList = document.getElementsByTagName("div");
                Node topicNode;
                for(int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if(node.getAttributes().getNamedItem("itemProp").getNodeValue().equals("articleBody")) {
                        article = stripHtml(node.getNodeValue());
                    }
                    //TODO tags
                }


            }

            DataBlock dataBlock = DataBlock.builder()
                    .origin(DataSource.GUARDIAN)
                    .content(article)
                    .tags()
                    .date(parseDateString(dateString))
                    .build();
            //TODO persist dataBlock
        }
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
