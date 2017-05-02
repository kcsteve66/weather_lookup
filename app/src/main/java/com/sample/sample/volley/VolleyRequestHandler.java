package com.sample.sample.volley;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sample.sample.AppController;
import com.sample.sample.interfaces.VolleyRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class VolleyRequestHandler {

    public static void getJsonObject(final VolleyRequestListener listener, final ProgressBar progressBar, String url, String tag) {
        showProgressBar(progressBar);

        // Pass third argument as "null" for GET requests.
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Response:%n %s", response.toString(4));
                            hideProgressBar(progressBar);
                            listener.onVolleyJsonObjectResponse(response);
                        } catch (JSONException e) {
                            Log.e("Error: ", e.getMessage());
                            hideProgressBar(progressBar);
                            listener.onVolleyErrorResponse(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = "";

                if (error.getMessage() != null) {
                    Log.e("Error: ", error.getMessage());
                    errorMsg = error.getMessage();
                }
                else {
                    errorMsg = "Error processing request.";
                }

                hideProgressBar(progressBar);
                listener.onVolleyErrorResponse(errorMsg);
            }
        });

        // Add the request object to the queue to be executed.
        AppController.getInstance().addToRequestQueue(req, tag);
    }

    public static void postJsonObject() {

    }

    private static void showProgressBar(ProgressBar progressBar) {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    private static void hideProgressBar(ProgressBar progressBar) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }
}
