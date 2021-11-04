package com.investment.alphavantage.sma.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SimpleMovingDayAverageData {

    @SerializedName("Meta Data")
    private MetaData metaData;

    @SerializedName("Technical Analysis: SMA")
    private TechnicalAnalysis technicalAnalysis;

}
