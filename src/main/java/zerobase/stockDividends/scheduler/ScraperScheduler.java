package zerobase.stockDividends.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.stockDividends.model.Company;
import zerobase.stockDividends.model.ScrapedResult;
import zerobase.stockDividends.model.constants.CacheKey;
import zerobase.stockDividends.persist.CompanyRepository;
import zerobase.stockDividends.persist.DividendRepository;
import zerobase.stockDividends.persist.entitiy.CompanyEntity;
import zerobase.stockDividends.persist.entitiy.DividendEntity;
import zerobase.stockDividends.scrapper.Scraper;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {

        //저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();
        //회사마다 배당금 정보 새로 스크래핑
        for (CompanyEntity company : companies) {
            log.info("scraping scheduler is  -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));
            scrapedResult.getDividends().stream()
                    //디비든 모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    //엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend -> "+e.toString());
                        }
                    });
            //연속적 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        //스크래핑 한 배당금 정보 중 데이터에비스에 없는 값은 저장

    }
}
