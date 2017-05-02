package com.sample.sample.interfaces;

import org.json.JSONObject;

public interface VolleyRequestListener {
    void onVolleyJsonObjectResponse(JSONObject response);
    void onVolleyErrorResponse(String response);
}
