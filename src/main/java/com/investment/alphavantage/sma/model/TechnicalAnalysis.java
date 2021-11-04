package com.investment.alphavantage.sma.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class TechnicalAnalysis {

    public List<SmaData> simpleMovingDayAverages;

}