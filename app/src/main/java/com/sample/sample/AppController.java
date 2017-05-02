package com.sample.sample;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sample.sample.utilities.Constants;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;     // Global request queue for volley.
    private static AppController sInstance;
    private static Context sContext;

    public static synchronized AppController getInstance() {
        return sInstance;
    }

    // Application context that can be referenced anywhere in the application.
    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sContext = getApplicationContext();
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null.
     */
    public RequestQueue getRequestQueue() {
        // Lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        // Set the default tag if tag is empty.
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.volleyRequestTimeout * 1000, 1, 1.0f));
        getRequestQueue().add(request);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     */
    public <T> void addToRequestQueue(Request<T> request) {
        // Set the default tag if tag is empty.
        request.setTag(TAG);
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.volleyRequestTimeout * 1000, 1, 1.0f));
        getRequestQueue().add(request);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
