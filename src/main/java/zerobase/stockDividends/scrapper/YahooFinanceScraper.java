package zerobase.stockDividends.scrapper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zerobase.stockDividends.model.Company;
import zerobase.stockDividends.model.Dividend;
import zerobase.stockDividends.model.ScrapedResult;
import zerobase.stockDividends.model.constants.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Component
public class YahooFinanceScraper implements Scraper{
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s";
    private static final long START_TIME = 86400; //60*60*24

    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);
        try {
            long now = System.currentTimeMillis()/1000;

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            Elements parsingDivs =  document.getElementsByAttributeValue("class", "table svelte-ewueuo"); //이 부분이 매번 바뀌어서 모니터링 해야 됨
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }
                String[] split = text.split(" ");
                int month = Month.strToNumber(split[0]);
                int day = Integer.parseInt(split[1].replace(",", ""));
                int year = Integer.parseInt(split[2]);
                String dividend = split[3];
                if(month<0){

                    throw new RuntimeException("Unexpected Month enum value ->" + split[0]);
                }
                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));
            }
            scrapedResult.setDividends(dividends);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scrapedResult;
    }
    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL, ticker, ticker);
        try {
            Document document = Jsoup.connect(url).get();
            Elements titleEle = document.getElementsByClass("svelte-ufs8hf"); //이 부분이 매번 바뀌어서 모니터링 해야 됨
            String title = titleEle.text().trim();

            return new Company(ticker, title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
