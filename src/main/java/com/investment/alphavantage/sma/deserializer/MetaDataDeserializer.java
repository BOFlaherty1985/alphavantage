package com.investment.alphavantage.sma.deserializer;

import com.google.gson.JsonObject;
import com.investment.alphavantage.sma.model.MetaData;

import java.util.Optional;

public class MetaDataDeserializer {

    public Optional<MetaData> getMetaData(JsonObject jsonObject) {
        JsonObject jsonMetaData = jsonObject.getAsJsonObject("Meta Data");
        if (jsonMetaData != null) {
            return Optional.of(MetaData.builder()
                    .symbol(jsonMetaData.get("1: Symbol").getAsString())
                    .indicator(jsonMetaData.get("2: Indicator").getAsString())
                    .build());
        }
        return Optional.empty();
    }

}
