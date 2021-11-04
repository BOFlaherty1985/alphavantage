package com.investment.alphavantage.sma.deserializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.investment.alphavantage.sma.model.MetaData;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class MetaDataDeserializerTest {

    private MetaDataDeserializer metaDataDeserializer = new MetaDataDeserializer();

    @Test
    public void shouldDeserializeAlphaVantageMetaData() {
        String symbol = "IBM";
        String indicator = "Simple Moving Average (SMA)";
        String jsonResponse = "{\"Meta Data\":{\"1: Symbol\":\"" + symbol + "\",\"2: Indicator\":\"" + indicator + "\"," +
                "\"3: Last Refreshed\":\"2021-04-09\",\"4: Interval\":\"weekly\"," +
                "\"5: Time Period\":200,\"6: Series Type\":\"open\",\"" +
                "7: Time Zone\":\"US/Eastern\"}}";

        JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();

        Optional<MetaData> result = metaDataDeserializer.getMetaData(jsonObject);
        MetaData metaData = result.get();
        assertEquals(metaData.getSymbol(), symbol);
        assertEquals(metaData.getIndicator(), indicator);
    }
}
