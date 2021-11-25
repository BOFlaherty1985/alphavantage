package com.investment.alphavantage.company;

import com.google.gson.Gson;
import com.investment.alphavantageapi.model.company.CompanyOverviewData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CompanyOverviewController {

    private RestTemplate restTemplate;
    private String apiKey;

    @Autowired
    public CompanyOverviewController(RestTemplate restTemplate, @Value("${alphavantage.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @GetMapping(value = "/companyOverview")
    public CompanyOverviewData retrieveCompanyOverviewDataFor(@RequestParam String ticker) {
        if (ticker == null || ticker.isEmpty()) {
            throw new IllegalArgumentException();
        }

        ResponseEntity<String> apiResponse =
                restTemplate.getForEntity("https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=" + apiKey,
                String.class);

        CompanyOverviewData companyOverview = new Gson().fromJson(apiResponse.getBody(), CompanyOverviewData.class);

        return CompanyOverviewData.builder()
                .symbol(companyOverview.getSymbol())
                .name(companyOverview.getName())
                .description(companyOverview.getDescription())
                .exchange(companyOverview.getExchange())
                .sector(companyOverview.getSector())
                .peRatio(companyOverview.getPeRatio())
                .build();
    }
}
