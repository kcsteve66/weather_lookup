package com.sample.sample.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.sample.sample.AppController;
import com.sample.sample.R;
import com.sample.sample.interfaces.VolleyRequestListener;
import com.sample.sample.model.Weather;
import com.sample.sample.utilities.Constants;
import com.sample.sample.volley.VolleyRequestHandler;
import com.sample.sample.volley.VolleyRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, VolleyRequestListener {

    private static final String TAG = "MainActivity";

    private boolean mIsLastLocation;
    private TextView mErrorMsg;
    private EditText mSearchValue;
    private ScrollView mMainLayout;
    private ProgressBar mProgressBar;
    private VolleyRequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestQueue = VolleyRequestQueue.getInstance(this);

        mMainLayout = (ScrollView) findViewById(R.id.main_layout);
        mErrorMsg = (TextView) findViewById(R.id.search_error);
        mSearchValue = (EditText) findViewById(R.id.location);
        findViewById(R.id.search_button).setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        initLocation();
    }

    /*
     * When app is launched, do a lookup to pre-populate the screen using the last location searched for.
     */
    private void initLocation() {
        String lastLocation = getLastLocation();
        if (!TextUtils.isEmpty(lastLocation)) {
            mIsLastLocation = true;
            String url = getLocationUrl(lastLocation);
            VolleyRequestHandler.getJsonObject(this, mProgressBar, url, TAG);
        }
        else {
            mMainLayout.setVisibility(View.GONE);
        }
    }

    private void saveLastLocation(String lastLocation) {
        SharedPreferences.Editor edit = getSharedPreferences(Constants.sharedPrefFile, Context.MODE_PRIVATE).edit();
        edit.putString(Constants.lastLocationKey, lastLocation);
        edit.apply();
    }

    private String getLastLocation() {
        return getSharedPreferences(Constants.sharedPrefFile, 0).getString(Constants.lastLocationKey, "");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search_button) {
            String searchValue = mSearchValue.getText().toString();
            if (TextUtils.isEmpty(searchValue.trim()))
                return;

            // Hide any previous error message when doing a new search.
            if (mErrorMsg.getVisibility() == View.VISIBLE) {
                mErrorMsg.setText("");
                mErrorMsg.setVisibility(View.GONE);

                findViewById(R.id.last_updated).setVisibility(View.VISIBLE);
            }

            hideKeyboard();

            // Create the url and request the weather data.
            String url = getLocationUrl(searchValue);
            VolleyRequestHandler.getJsonObject(this, mProgressBar, url, TAG);
        }
    }

    /*
     * Determine if the search value is a city or zipcode, and provide the appropriate url.
     */
    private String getLocationUrl(String locValue) {
        boolean isZip = false;
        String url;

        if (!TextUtils.isEmpty(locValue) && !locValue.contains(","))
            locValue = locValue.concat(",US");

        if (locValue.length() >= 5) {
            String tmpValue = locValue.substring(0, 5);
            if (TextUtils.isDigitsOnly(tmpValue))
                isZip = true;
        }

        if (isZip)
            url = Constants.BASE_ZIP_URL + locValue + Constants.UNITS + Constants.API_KEY ;
        else
            url = Constants.BASE_CITY_URL + locValue + Constants.UNITS + Constants.API_KEY ;

        return url;
    }

    private void hideKeyboard() {
        View focusView = this.getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppController.getInstance().cancelPendingRequests(TAG);
    }

    public void onVolleyJsonObjectResponse(JSONObject response) {
        try {
            // Make sure only a US location has been searched for.
            if (!response.getJSONObject("sys").getString("country").equalsIgnoreCase("US")) {
                setErrorMessage("Please enter a valid US location.");
                return;
            }

            if (mMainLayout.getVisibility() == View.GONE)
                mMainLayout.setVisibility(View.VISIBLE);

            // Save the last search location, to display next time the app is launched.
            saveLastLocation(mSearchValue.getText().toString());

            // Parse the json data into a weather onject.
            Weather weather = Weather.parse(response);
            if (weather == null) {
                setErrorMessage("Error reading weather information.");
                return;
            }

            // Download and display the Weather icon for the current condition.
            NetworkImageView weatherIcon = (NetworkImageView) findViewById(R.id.weather_icon);
            weatherIcon.setDefaultImageResId(R.drawable.placeholder);
            String iconId = weather.getIconId();
            if (!TextUtils.isEmpty(iconId)) {
                // Use Volley request queue and image loader to download and display the image.
                weatherIcon.setImageUrl(Constants.IMG_URL + iconId + ".png", mRequestQueue.getImageLoader());
            }

            ((TextView) findViewById(R.id.last_updated)).setText(weather.getLastUpdatedDate());
            ((TextView) findViewById(R.id.city)).setText(weather.getCityAndState());
            ((TextView) findViewById(R.id.temperature)).setText(weather.getTemperature());
            ((TextView) findViewById(R.id.description)).setText(weather.getDescription());
            ((TextView) findViewById(R.id.wind)).setText(weather.getWindAndDirection());
            ((TextView) findViewById(R.id.gusts)).setText(weather.getWindGusts());
            ((TextView) findViewById(R.id.humidity)).setText(weather.getHumidity());
            ((TextView) findViewById(R.id.pressure)).setText(weather.getPressure());
            ((TextView) findViewById(R.id.visibility)).setText(weather.getVisibility());
            ((TextView) findViewById(R.id.sunrise)).setText(weather.getSunrise());
            ((TextView) findViewById(R.id.sunset)).setText(weather.getSunset());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        mSearchValue.setText("");
        findViewById(R.id.last_updated).setVisibility(View.VISIBLE);
        mIsLastLocation = false;
    }

    private void setErrorMessage(String msg) {
        if (mIsLastLocation)
            return;

        findViewById(R.id.last_updated).setVisibility(View.GONE);

        TextView errorMsg = (TextView) findViewById(R.id.search_error);
        errorMsg.setVisibility(View.VISIBLE);
        errorMsg.setText(msg);
    }

    public void onVolleyErrorResponse(String response) {
        if (!mIsLastLocation) {
            setErrorMessage(response);
        }
        else {
            mIsLastLocation = false;
        }
    }
}
