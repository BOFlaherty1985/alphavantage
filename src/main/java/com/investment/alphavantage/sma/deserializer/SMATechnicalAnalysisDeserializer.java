package com.investment.alphavantage.sma.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.investment.alphavantage.sma.model.SmaData;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SMATechnicalAnalysisDeserializer {

    public static final DateTimeFormatter JSON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Clock clock;

    public SMATechnicalAnalysisDeserializer(Clock clock) {
        this.clock = clock;
    }

    public List<SmaData> getSimpleMovingDayAverageData(JsonObject jsonObject) {
        List<SmaData> smaByDate = new ArrayList<>();
        Optional<JsonElement> json = Optional.ofNullable(jsonObject.get("Technical Analysis: SMA"));

        if (json.isPresent()) {
            JsonObject jsonTechnicalAnalysis = json.get().getAsJsonObject();
            LocalDate today = LocalDate.now(clock);
            LocalDate lastAvailableData = (today.getDayOfWeek() == DayOfWeek.MONDAY) ? today.minusDays(3) : today.minusDays(1);
            String currentDate = lastAvailableData.format(JSON_DATE_FORMATTER);
            JsonObject date = jsonTechnicalAnalysis.getAsJsonObject(currentDate);
            if (date != null) {
                String jsonSma = date.get("SMA").getAsString();
                smaByDate.add(SmaData.builder().date(currentDate).simpleMovingDayAverage(jsonSma).build());
            }

            // retrieve 5 weeks worth of data (TODO: allow for numberOfWeeks to be passed through as a @RequestParam)
            buildLegacyWeeklyData(smaByDate, jsonTechnicalAnalysis, lastAvailableData);

        } else {
            return smaByDate;
        }
        return smaByDate;
    }

    private void buildLegacyWeeklyData(List<SmaData> smaByDate, JsonObject jsonTechnicalAnalysis, LocalDate lastAvailableData) {
        for (int numberOfWeeks = 1; numberOfWeeks <= 5; numberOfWeeks++) {
            // weekly results usually produced on a Friday, the exception being when the Friday is a bank holiday
            String previousWeek = lastAvailableData.minusWeeks(numberOfWeeks).with(DayOfWeek.FRIDAY).format(JSON_DATE_FORMATTER);
            JsonObject jsonPreviousWeek = jsonTechnicalAnalysis.getAsJsonObject(previousWeek);
            if (jsonPreviousWeek != null) {
                String jsonSma = jsonPreviousWeek.get("SMA").getAsString();
                smaByDate.add(SmaData.builder().date(previousWeek).simpleMovingDayAverage(jsonSma).build());
            }
        }
    }

}
