package com.investment.alphavantage.sma;

import com.google.gson.Gson;
import com.investment.alphavantage.sma.model.SimpleMovingDayAverageData;
import com.investment.alphavantage.sma.model.SmaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // SpringJUnit4Runner for Caching to work / test?
public class SimpleMovingDayAverageControllerTest {

    private static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @InjectMocks
    private SimpleMovingDayAverageController smaController;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Clock clock;

    private Clock fixedClock;

    @Before
    public void setup() {
        fixedClock = Clock.fixed(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
    }

    @Test
    public void shouldNotReturnNullFromSMAController() {
        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
                String.class)).thenReturn(ResponseEntity.ok(new Gson().toJson(SimpleMovingDayAverageData.builder().build())));

        SimpleMovingDayAverageData result = smaController.retrieveSimpleMovingDayAverage("IBM");
        assertNotNull(result);
    }

    @Test
    public void shouldReturnSimpleMovingDayObjectWithDefaultValues() {
        String symbol = "IBM";
        String indicator = "Simple Moving Average (SMA)";
        String smaDate = LocalDate.now().minusDays(3).format(JSON_DATE_FORMATTER);
        String smaValue = "123";

        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"IBM\",\"2: Indicator\":\"Simple Moving Average (SMA)\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}," +
                "\"Technical Analysis: SMA\":{\"2021-04-09\":{\"SMA\":\"123\"}}}";

        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
                String.class)).thenReturn(ResponseEntity.ok(jsonResponse));

        SimpleMovingDayAverageData result = smaController.retrieveSimpleMovingDayAverage("IBM");
        assertNotNull(result);
        assertEquals(result.getMetaData().getSymbol(), symbol);
        assertEquals(result.getMetaData().getIndicator(), indicator);
        List<SmaData> simpleMovingDayAverages = result.getTechnicalAnalysis().getSimpleMovingDayAverages();
        SmaData resultSmaData = simpleMovingDayAverages.stream().findFirst().get();
        assertEquals(resultSmaData.getDate(), smaDate);
        assertEquals(resultSmaData.getSimpleMovingDayAverage(), smaValue);
    }

    @Test
    public void shouldRetrieveDataDirectlyFromAlphaVantageViaRestTemplate() {
        SimpleMovingDayAverageData smaData = SimpleMovingDayAverageData.builder().build();

        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
                String.class)).thenReturn(ResponseEntity.ok(new Gson().toJson(smaData)));

        smaController.retrieveSimpleMovingDayAverage("IBM");
        verify(restTemplate).getForEntity(
                "https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
                String.class);
    }

    // Monday 12th April 2021
    private final static LocalDate LOCAL_DATE = LocalDate.of(2021, 04, 12);

    @Test
    public void shouldRetrieveFridayDataAsCurrentDateWhenRequestIsMadeOnMonday() {

        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"IBM\",\"2: Indicator\":\"Simple Moving Average (SMA)\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}," +
                "\"Technical Analysis: SMA\":{\"2021-04-09\":{\"SMA\":\"999\"}}}";

        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
                String.class)).thenReturn(ResponseEntity.ok(jsonResponse));

        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        SimpleMovingDayAverageData result = smaController.retrieveSimpleMovingDayAverage("IBM");
        SmaData smaData = result.getTechnicalAnalysis().getSimpleMovingDayAverages().stream().findFirst().get();
        assertNotNull(smaData);
        assertEquals("999", smaData.getSimpleMovingDayAverage());
    }
}
