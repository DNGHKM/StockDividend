package zerobase.stockDividends.scrapper;

import zerobase.stockDividends.model.Company;
import zerobase.stockDividends.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
