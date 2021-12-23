package com.investment.alphavantage.company;

import com.investment.alphavantageapi.model.company.CompanyOverviewData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
public class CompanyOverviewControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CompanyOverviewController companyOverviewController =
            new CompanyOverviewController(restTemplate, "ALPHAVANTAGE_API_KEY");


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenTickerIsNull() {
        // given
        String ticker = null;

        // when
        CompanyOverviewData result = companyOverviewController.retrieveCompanyOverviewDataFor(ticker);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenTickerIsEmpty() {
        // given
        String ticker = "";

        // when
        CompanyOverviewData result = companyOverviewController.retrieveCompanyOverviewDataFor(ticker);
    }

    @Test
    public void shouldReturnCompanyOverviewDetails() {
        // given
        String ticker = "IBM";
        String name = "International Business Machines Corporation";
        String description = "Blah Blah Blah";
        String exchange = "NYSE";
        String peRatio = "22.08";
        String sector = "Technology";
        String apiResponse = "{\n" +
                "    \"Symbol\": \"" + ticker + "\",\n" +
                "    \"Name\": \"" + name + "\",\n" +
                "    \"Description\": \"" + description + "\",\n" +
                "    \"Exchange\": \"" + exchange + "\",\n" +
                "    \"Sector\": \"" + sector + "\",\n" +
                "    \"PERatio\": \"" + peRatio + "\"\n" +
                "}";

        given(restTemplate.getForEntity("https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=ALPHAVANTAGE_API_KEY",
                String.class)).willReturn(ResponseEntity.of(Optional.of(apiResponse)));

        // when
        CompanyOverviewData result = companyOverviewController.retrieveCompanyOverviewDataFor(ticker);

        // then
        verify(restTemplate).getForEntity("https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=ALPHAVANTAGE_API_KEY",
                String.class);
        assertEquals(ticker, result.getSymbol());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(sector, result.getSector());
        assertEquals(exchange, result.getExchange());
        assertEquals(peRatio, result.getPeRatio());
    }

}
