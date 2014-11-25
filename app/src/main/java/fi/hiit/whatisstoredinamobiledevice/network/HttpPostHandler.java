package fi.hiit.whatisstoredinamobiledevice.network;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class HttpPostHandler {

    private final String mJSONUrl;
    private final HttpStack mHttpStack;
    private Context mContext;
    private static final String TAG = "HttpPostHandler";


    public HttpPostHandler(Context context, HttpStack httpStack) {
        this.mContext = context;
        this.mHttpStack = httpStack;
        // the ip of the server that should receive the json
        this.mJSONUrl = "http://pickingdigitalpockets.herokuapp.com/submit";
    }

    public boolean postJSON(final JSONObject jsonToSend) {
        final RequestQueue queue = Volley.newRequestQueue(mContext, mHttpStack);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, mJSONUrl, jsonToSend,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST successful: " + response.toString());
                        queue.stop();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "POST errored: " + volleyError);
                        queue.stop();
                    }
                });

        Log.d(TAG, "Old timoout: " + jsonRequest.getTimeoutMs());
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.d(TAG, "New timoout: " + jsonRequest.getTimeoutMs());


        queue.add(jsonRequest);
        return true;
    }



}
