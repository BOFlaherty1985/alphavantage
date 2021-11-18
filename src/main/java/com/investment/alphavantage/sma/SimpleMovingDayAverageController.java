package com.investment.alphavantage.sma;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.investment.alphavantage.sma.deserializer.SimpleMovingDayAverageDeserializer;
import com.investment.alphavantageapi.model.sma.SimpleMovingDayAverageData;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.util.concurrent.ConcurrentMap;

@RestController
@Log
public class SimpleMovingDayAverageController {

    private RestTemplate restTemplate;
    private Clock clock;
    private CacheManager cacheManager;

    @Autowired
    public SimpleMovingDayAverageController(RestTemplate restTemplate, Clock clock, CacheManager cacheManager) {
        this.restTemplate = restTemplate;
        this.clock = clock;
        this.cacheManager = cacheManager;
    }

    /*
     How do we handle errors? @ControllerAdvice or Netflix Hystrix?
     */
    @GetMapping("/simpleMovingDayAverage")
    @Cacheable(value = "simpleMovingDayAverages", key = "#ticker")
    public SimpleMovingDayAverageData retrieveSimpleMovingDayAverage(@RequestParam String ticker) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "https://www.alphavantage.co/query?function=SMA&symbol=" + ticker + "&interval=weekly&time_period=200" +
                        "&series_type=open&apikey=ALPHAVANTAGE_API_KEY", String.class);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SimpleMovingDayAverageData.class, new SimpleMovingDayAverageDeserializer(clock));
        Gson customGson = gsonBuilder.create();
        return customGson.fromJson(responseEntity.getBody(), SimpleMovingDayAverageData.class);
    }

    @GetMapping("/cache")
    public void cacheContentFor(String name) {
        ConcurrentMap<String, SimpleMovingDayAverageData> cache =
                (ConcurrentMap<String, SimpleMovingDayAverageData>) cacheManager.getCache(name).getNativeCache();
        log.info("SimpleMovingDayAverages Cache Content: " + cache.keySet());
    }
}
