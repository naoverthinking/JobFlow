package com.example.jobflow.notification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {
    String tokenFCM,title, message;
    Context context;
    String postURL = "https://fcm.googleapis.com/v1/projects/jobflow-36524/messages:send";
    public void SendNotificationHi() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            JSONObject mainObj = new JSONObject();
            JSONObject messageObj = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            JSONObject dataObj = new JSONObject();
            dataObj.put("title", title);
            dataObj.put("body", message);
            notificationObj.put("title", title);
            notificationObj.put("body", message);
            messageObj.put("token", tokenFCM);
            messageObj.put("notification", notificationObj);


            mainObj.put("message", messageObj);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postURL, mainObj, response -> {
            }, volleyError -> {

            }){
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    AccessTokenManager accessTokenManager = new AccessTokenManager();
                    headers.put("Authorization", "Bearer " + accessTokenManager.getAccessToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void SendNotificationHi(String target,String chatKey,String UID) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            JSONObject mainObj = new JSONObject();
            JSONObject messageObj = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            JSONObject dataObj = new JSONObject();
            dataObj.put("target", target);
            dataObj.put("chatKey", chatKey);
            dataObj.put("UID", UID);
            dataObj.put("type", "notification");

            notificationObj.put("title", title);
            notificationObj.put("body", message);
            messageObj.put("token", tokenFCM);
            messageObj.put("notification", notificationObj);
            messageObj.put("data", dataObj);
            mainObj.put("message", messageObj);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postURL, mainObj, response -> {
            }, volleyError -> {

            }){
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    AccessTokenManager accessTokenManager = new AccessTokenManager();
                    headers.put("Authorization", "Bearer " + accessTokenManager.getAccessToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SendNotification(String tokenFCM, String title, String message, Context context) {
        this.tokenFCM = tokenFCM;
        this.title = title;
        this.message = message;
        this.context = context;
    }

    public String getTokenFCM() {
        return tokenFCM;
    }

    public void setTokenFCM(String tokenFCM) {
        this.tokenFCM = tokenFCM;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Context   getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setPostURL(String postURL) {
        this.postURL = postURL;
    }

}
