package zerobase.stockDividends;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import zerobase.stockDividends.model.Company;
import zerobase.stockDividends.model.Dividend;
import zerobase.stockDividends.model.ScrapedResult;
import zerobase.stockDividends.model.constants.Month;
import zerobase.stockDividends.scrapper.Scraper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScraperTest {
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/QQQ/history?&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; //60*60*24
    @Test
    public void ScraperTest(){
        try{
            Connection connection = Jsoup.connect(STATISTICS_URL);
            Document document = connection.get();

            Elements eles = document.getElementsByAttributeValue("class", "table svelte-ta1t6m");
            Element ele = eles.get(0);
            System.out.println(ele);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
