package com.treehacks.hopify.hopify;

import android.app.Application;

import io.branch.referral.Branch;

/**
 * Created by neel on 2018-02-18 at 5:25 AM.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }
}
