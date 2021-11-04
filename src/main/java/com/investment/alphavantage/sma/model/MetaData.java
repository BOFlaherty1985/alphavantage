package com.investment.alphavantage.sma.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MetaData {

    @SerializedName("1: Symbol")
    private String symbol;

    @SerializedName("2: Indicator")
    private String indicator;
}
