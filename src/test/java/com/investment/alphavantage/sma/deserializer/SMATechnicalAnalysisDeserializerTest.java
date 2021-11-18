package com.investment.alphavantage.sma.deserializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.investment.alphavantageapi.model.sma.SmaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SMATechnicalAnalysisDeserializerTest {

    @Mock
    private Clock clock;

    private Clock fixedClock;

    @InjectMocks
    private SMATechnicalAnalysisDeserializer smaTechnicalAnalysisDeserializer;

    @Before
    public void setup() {
        fixedClock = Clock.fixed(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
    }

    @Test
    public void shouldFailGracefullyWhenExpectedJSONIsMissingOrIncorrectlyFormatted() {
        String jsonString = "{}";
        JsonObject technicalAnalysisJson = new JsonParser().parse(jsonString).getAsJsonObject();
        List<SmaData> result = smaTechnicalAnalysisDeserializer.getSimpleMovingDayAverageData(technicalAnalysisJson);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldDeserializeSimpleMovingDayAverageTechnicalAnalysisJsonObject() {
        String date = "2021-04-09";
        String simpleMovingDayAverage = "124.0520";
        String jsonString = "{\"Technical Analysis: SMA\":{\"" + date + "\":{\"SMA\":\"" + simpleMovingDayAverage + "\"}}}";
        JsonObject technicalAnalysisJson = new JsonParser().parse(jsonString).getAsJsonObject();

        List<SmaData> result = smaTechnicalAnalysisDeserializer.getSimpleMovingDayAverageData(technicalAnalysisJson);
        assertNotNull(result);
        SmaData smaData = result.stream().findFirst().get();
        assertEquals(date, smaData.getDate());
        assertEquals(simpleMovingDayAverage, smaData.getSimpleMovingDayAverage());
    }

    /*
    Add test to check for 5 previous weeks worth of SMA data
     */
}
