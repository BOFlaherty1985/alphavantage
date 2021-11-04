package com.investment.alphavantage.sma.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SmaData {

    private String date;
    private String simpleMovingDayAverage;
}
