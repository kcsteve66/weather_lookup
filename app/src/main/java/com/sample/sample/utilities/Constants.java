package com.sample.sample.utilities;

public class Constants {

    // Number of seconds for request timeout.
    public static final int volleyRequestTimeout = 20;

    public static final char degreeSymbol = '\u00b0';

    // Conversion rate to convert pressure into inches of mercury.
    public static final double pressureConversion = 0.02953;

    // Wind direction lookup table.
    public static final String[] windDirection = {"N","NNE","NE","ENE","E","ESE", "SE","SSE","S","SSW","SW","WSW", "W","WNW","NW","NNW","N"};

    public static final String sharedPrefFile = "SampleWeatherApp";
    public static final String lastLocationKey = "LastCitySearched";

    public static final String BASE_CITY_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String BASE_ZIP_URL = "http://api.openweathermap.org/data/2.5/weather?zip=";
    public static final String IMG_URL = "http://openweathermap.org/img/w/";

    public static final String API_KEY = "&appid=da31df5176247b1cec08f2908e2ad1ac";
    public static final String UNITS = "&units=imperial";
}
