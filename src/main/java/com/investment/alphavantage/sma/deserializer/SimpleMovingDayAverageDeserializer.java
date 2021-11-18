package com.investment.alphavantage.sma.deserializer;

import com.google.gson.*;

import com.investment.alphavantageapi.model.sma.MetaData;
import com.investment.alphavantageapi.model.sma.SimpleMovingDayAverageData;
import com.investment.alphavantageapi.model.sma.SmaData;
import com.investment.alphavantageapi.model.sma.TechnicalAnalysis;

import java.lang.reflect.Type;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

public class SimpleMovingDayAverageDeserializer implements JsonDeserializer<SimpleMovingDayAverageData> {

    private Clock clock;

    public SimpleMovingDayAverageDeserializer(Clock clock) {
        this.clock = clock;
    }

    @Override
    public SimpleMovingDayAverageData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        MetaDataDeserializer metaDataDeserializer = new MetaDataDeserializer();
        Optional<MetaData> metaData = metaDataDeserializer.getMetaData(jsonObject);

        SMATechnicalAnalysisDeserializer smaTechnicalAnalysisDeserializer = new SMATechnicalAnalysisDeserializer(clock);
        List<SmaData> smaByDate = smaTechnicalAnalysisDeserializer.getSimpleMovingDayAverageData(jsonObject);
        TechnicalAnalysis technicalAnalysis = TechnicalAnalysis.builder().simpleMovingDayAverages(smaByDate).build();

        return SimpleMovingDayAverageData.builder().metaData(metaData.get()).technicalAnalysis(technicalAnalysis).build();
    }

}
