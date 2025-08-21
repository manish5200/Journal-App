package net.manifest.journalApp.services;

import net.manifest.journalApp.api.response.WeatherResponse;
import net.manifest.journalApp.cache.AppCache;
import net.manifest.journalApp.constants.Placeholders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    public WeatherResponse getWeather(String city){
      String finalAPI =  appCache.appCache.get(AppCache.keys.WEATHER_API.toString()).replace(Placeholders.CITY,city).replace(Placeholders.API_KEY,apiKey);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        return response.getBody();
    }




}
