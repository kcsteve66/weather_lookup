package com.sample.sample.model;

import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.sample.sample.AppController;
import com.sample.sample.utilities.Constants;
import com.sample.sample.utilities.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class Weather {

    public long lastUpdatedDate;
    public long sunrise;
    public long sunset;
    public double latitude;
    public double longitude;
    public double temperature;
    public double windSpeed = -1;
    public double windAngle;
    public double windGusts;
    public double pressure;
    public String city;
    public String description;
    public String iconId;
    public String humidity;
    public String visibility;

    public String getLastUpdatedDate() {
        String value;

        try {
            // Convert the pressure into inches of mercury.
            value = "As of " + DateUtils.getDateTimeFormat(lastUpdatedDate, DateUtils.DATE_TIME_FORMAT);
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getCityAndState() {
        String value = "";

        try {
            // Lookup the state using lat/lon and append to the city name.
            Geocoder myLocation = new Geocoder(AppController.getAppContext(), Locale.getDefault());
            String state = "";
            try {
                List<Address> addressInfo = myLocation.getFromLocation(latitude, longitude, 1);

                if (addressInfo != null && addressInfo.size() > 0) {
                    String[] spState = addressInfo.get(0).getAddressLine(1).split(", ");
                    state = spState[1].substring(0, 2);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                state = "";
            }

            value = city;
            // If DC is returned as state, then ignore it because it is already returned with city (i.e. Washington, D.C.)
            if (!TextUtils.isEmpty(state) && !state.equalsIgnoreCase("DC"))
                value = value.concat(", " + state);
        }
        catch (Exception e) {
            value = "";
        }

        return value;
    }

    public String getTemperature() {
        String value = "";

        try {
            // Round the temperature to the nearest integer and add a degree symbol.
            int roundedTempVal = (int) Math.round(temperature);
            value = String.valueOf(roundedTempVal) + Constants.degreeSymbol;
        }
        catch (Exception e) {
            value = "";
        }

        return value;
    }

    public String getDescription() {
        String value = "";

        try {
            // Capitalize the first letter of the description.
            if (!TextUtils.isEmpty(description) && description.length() > 1)
                value = description.substring(0, 1).toUpperCase() + description.substring(1);
        }
        catch (Exception e) {
            value = "";
        }

        return value;
    }

    public String getIconId() {
        return iconId;
    }

    public String getWindAndDirection() {
        String value = "N/A";

        try {
            // Round the wind speed to the nearest integer and add the mph unit.
            if (windSpeed >= 0) {
                value = String.valueOf(Math.round(windSpeed)) + " mph";

                try {
                    // Take the wind angle and convert to a compass value - i.e. NW, ESE, SE, etc...
                    String windDirection = Constants.windDirection[(int) Math.floor((windAngle + 11.25) / 22.5)];
                    value = value.concat(" " + windDirection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getWindGusts() {
        String value = "N/A";

        try {
            // Correctly calculate the gusts and add mph unit.
            if (windGusts > 0) {
                double dblGusts = windGusts + windSpeed;
                value = String.valueOf(Math.round(dblGusts)) + " mph";
            }
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getHumidity() {
        String value = "N/A";

        try {
            // Show the humidity as a percentage.
            if (!TextUtils.isEmpty(humidity)) {
                value = humidity + "%";
            }
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getPressure() {
        String value = "N/A";

        try {
            // Convert the pressure into inches of mercury.
            if (pressure > 0) {
                value = String.format("%.2f", pressure * Constants.pressureConversion) + "\"";
            }
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getVisibility() {
        String value;

        try {
            // Convert the visibility into miles.
            if (!TextUtils.isEmpty(visibility)) {
                int visibilityValue = (int) Math.round(Integer.valueOf(visibility).intValue() / 1609);
                value = String.valueOf(visibilityValue) + " mi";
            }
            else {
                value = "N/A";
            }
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getSunrise() {
        String value;

        try {
            // Convert sunrise value into formatted time.
            value = DateUtils.getDateTimeFormat(sunrise, DateUtils.TIME_FORMAT);
            if (TextUtils.isEmpty(value))
                value = "N/A";
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public String getSunset() {
        String value;

        try {
            // Convert sunset value into formatted time.
            value = DateUtils.getDateTimeFormat(sunrise, DateUtils.TIME_FORMAT);
            if (TextUtils.isEmpty(value))
                value = "N/A";
        }
        catch (Exception e) {
            value = "N/A";
        }

        return value;
    }

    public static Weather parse(JSONObject jsonObj) {
        Weather weather = new Weather();

        try {
            JSONObject coordObj = jsonObj.getJSONObject("coord");
            JSONObject sysObj = jsonObj.getJSONObject("sys");
            JSONObject mainObj = jsonObj.getJSONObject("main");
            JSONObject windObj = jsonObj.getJSONObject("wind");
            JSONArray weatherArray = jsonObj.getJSONArray("weather");
            JSONObject weatherObj = weatherArray.getJSONObject(0);

            weather.city = jsonObj.optString("name");
            weather.description = weatherObj.optString("description");
            weather.iconId = weatherObj.optString("icon");
            weather.humidity = mainObj.optString("humidity");
            weather.visibility = jsonObj.optString("visibility");

            if (jsonObj.has("dt"))
                weather.lastUpdatedDate = jsonObj.getLong("dt");

            if (coordObj.has("lat"))
                weather.latitude = coordObj.getDouble("lat");

            if (coordObj.has("lon"))
                weather.longitude = coordObj.getDouble("lon");

            if (mainObj.has("temp"))
                weather.temperature = mainObj.getDouble("temp");

            if (windObj.has("speed"))
                weather.windSpeed = windObj.getDouble("speed");

            if (windObj.has("deg"))
                weather.windAngle = windObj.getDouble("deg");

            if (windObj.has("gust"))
                weather.windGusts = windObj.getDouble("gust");

            if (mainObj.has("pressure"))
                weather.pressure = mainObj.getDouble("pressure");

            if (sysObj.has("sunrise"))
                weather.sunrise = sysObj.getLong("sunrise");

            if (sysObj.has("sunset"))
                weather.sunset = sysObj.getLong("sunset");
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return weather;
    }
}
