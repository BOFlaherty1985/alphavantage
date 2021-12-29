package com.investment.alphavantage.sma;

import com.investment.alphavantageapi.model.sma.SimpleMovingDayAverageData;
import com.investment.alphavantageapi.model.sma.SmaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class SimpleMovingDayAverageControllerTest {

    private static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @InjectMocks
    private SimpleMovingDayAverageController smaController;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Environment environment;

    @Mock
    private Clock clock;

    private Clock fixedClock;

    private String API_KEY;

    @Before
    public void setup() {
        fixedClock = Clock.fixed(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(environment.getProperty("alphavantage.api.key")).thenReturn("ALPHAVANTAGE_API_KEY");
        API_KEY = environment.getProperty("alphavantage.api.key");
    }

//    @Test
//    public void shouldNotReturnNullFromSMAController() {
//        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
//                String.class)).thenReturn(ResponseEntity.ok(new Gson().toJson(SimpleMovingDayAverageData.builder().build())));
//
//        SimpleMovingDayAverageData result = smaController.retrieveSimpleMovingDayAverage("IBM");
//        assertNotNull(result);
//    }

    @Test
    public void shouldReturnSimpleMovingDayObjectWithDefaultValues() {
        // given
        String symbol = "IBM";
        String indicator = "Simple Moving Average (SMA)";
        String smaDate = LOCAL_DATE.minusDays(3).format(JSON_DATE_FORMATTER);
        String smaValue = "123";

        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"IBM\",\"2: Indicator\":\"Simple Moving Average (SMA)\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}," +
                "\"Technical Analysis: SMA\":{\"2021-04-09\":{\"SMA\":\"123\"}}}";

        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly" +
                        "&time_period=200&series_type=open&apikey=" + API_KEY,
                String.class)).thenReturn(ResponseEntity.ok(jsonResponse));

        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        // when
        SimpleMovingDayAverageData result = smaController.retrieveSimpleMovingDayAverage("IBM");

        // then
        assertNotNull(result);
        assertEquals(result.getMetaData().getSymbol(), symbol);
        assertEquals(result.getMetaData().getIndicator(), indicator);
        List<SmaData> simpleMovingDayAverages = result.getTechnicalAnalysis().getSimpleMovingDayAverages();
        SmaData resultSmaData = simpleMovingDayAverages.stream().findFirst().get();
        assertEquals(resultSmaData.getDate(), smaDate);
        assertEquals(resultSmaData.getSimpleMovingDayAverage(), smaValue);
    }

//    @Test
//    public void shouldRetrieveDataDirectlyFromAlphaVantageViaRestTemplate() {
//        SimpleMovingDayAverageData smaData = SimpleMovingDayAverageData.builder().build();
//
//        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
//                String.class)).thenReturn(ResponseEntity.ok(new Gson().toJson(smaData)));
//
//        smaController.retrieveSimpleMovingDayAverage("IBM");
//        verify(restTemplate).getForEntity(
//                "https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly&time_period=200&series_type=open&apikey=ALPHAVANTAGE_API_KEY",
//                String.class);
//    }

    // Monday 12th April 2021
    private final static LocalDate LOCAL_DATE = LocalDate.of(2021, 04, 12);

    @Test
    public void shouldRetrieveFridayDataAsCurrentDateWhenRequestIsMadeOnMonday() {
        // given
        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"IBM\",\"2: Indicator\":\"Simple Moving Average (SMA)\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}," +
                "\"Technical Analysis: SMA\":{\"2021-04-09\":{\"SMA\":\"999\"}}}";

        when(restTemplate.getForEntity("https://www.alphavantage.co/query?function=SMA&symbol=IBM&interval=weekly" +
                        "&time_period=200&series_type=open&apikey=" + API_KEY,
                String.class)).thenReturn(ResponseEntity.ok(jsonResponse));

        fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();

        // when
        SimpleMovingDayAverageData result = smaController.retrieveSimpleMovingDayAverage("IBM");
        SmaData smaData = result.getTechnicalAnalysis().getSimpleMovingDayAverages().stream().findFirst().get();

        // then
        assertNotNull(smaData);
        assertEquals("999", smaData.getSimpleMovingDayAverage());
    }
}
