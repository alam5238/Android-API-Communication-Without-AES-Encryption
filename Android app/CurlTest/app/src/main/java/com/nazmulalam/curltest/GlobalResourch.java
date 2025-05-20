package com.nazmulalam.curltest;

import android.app.Application;

public class GlobalResourch extends Application {

    private String agent;
    private String token;


    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize any global variables here
        agent = "default value";
        token = "default value";
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
