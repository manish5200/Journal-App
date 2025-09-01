package net.manifest.journalapp.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

   @Setter
   @Getter
    public class WeatherResponse{

        private Location location;
        private Current current;

        @Setter
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Current{

            @JsonProperty("observation_time")
            private String observationTime;

            private int temperature;

            @JsonProperty("weather_code")
            private int weatherCode;

            @JsonProperty("weather_icons")
            private ArrayList<String> weatherIcons;

            @JsonProperty("weather_descriptions")
            private ArrayList<String> weatherDescriptions;

            @JsonProperty("wind_speed")
            private int windSpeed;

            @JsonProperty("wind_degree")
            private int windDegree;

            @JsonProperty("wind_dir")
            private String windDir;

            private int pressure;
            private int humidity;
            private int feelslike;
        }

        @Setter
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Location{
            private String name;
            private String country;
            private String localtime;

            @JsonProperty("localtime_epoch")
            private int localtimeEpoch;

        }

    }






