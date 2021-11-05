package com.investment.alphavantage.integration;

import com.investment.alphavantage.sma.SimpleMovingDayAverageController;
import com.investment.alphavantage.sma.model.MetaData;
import com.investment.alphavantage.sma.model.SimpleMovingDayAverageData;
import com.investment.alphavantage.sma.model.SmaData;
import com.investment.alphavantage.sma.model.TechnicalAnalysis;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class SimpleMovingDayAverageIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Autowired
    private SimpleMovingDayAverageController controller;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void shouldReturnSimpleMovingDayAverageDataFromEndpoint() throws URISyntaxException {
        String symbol = "IBM";
        String indicator = "Simple Moving Average (SMA)";

        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"IBM\",\"2: Indicator\":\"Simple Moving Average (SMA)\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}," +
                "\"Technical Analysis: SMA\":{\"2021-04-09\":{\"SMA\":\"123\"}}}";

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200" +
                        "&series_type=open&apikey=ALPHAVANTAGE_API_KEY")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        SimpleMovingDayAverageData result = controller.retrieveSimpleMovingDayAverage(symbol);
        MetaData metaData = result.getMetaData();
        assertEquals(metaData.getSymbol(), symbol);
        assertEquals(metaData.getIndicator(), indicator);

        TechnicalAnalysis technicalAnalysis = result.getTechnicalAnalysis();
        Optional<SmaData> smaData = technicalAnalysis.simpleMovingDayAverages.stream().findFirst();
        String date = smaData.get().getDate();
        String value = smaData.get().getSimpleMovingDayAverage();
        assertEquals("2021-04-09", date);
        assertEquals("123", value);
    }

    @Test
    public void shouldCacheResponse() throws URISyntaxException {
        String symbol = "IBM";

        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"IBM\",\"2: Indicator\":\"Simple Moving Average (SMA)\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}," +
                "\"Technical Analysis: SMA\":{\"2021-04-09\":{\"SMA\":\"123\"}}}";

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200" +
                        "&series_type=open&apikey=ALPHAVANTAGE_API_KEY")))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        SimpleMovingDayAverageData result1 = controller.retrieveSimpleMovingDayAverage(symbol);
        SimpleMovingDayAverageData result2 = controller.retrieveSimpleMovingDayAverage(symbol);
        assertEquals(result1, result2);
    }
}
