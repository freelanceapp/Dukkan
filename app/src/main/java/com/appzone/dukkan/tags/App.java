package com.appzone.dukkan.tags;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.appzone.dukkan.language_helper.LanguageHelper;

import java.util.Locale;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        Log.e("ss",Locale.getDefault().getLanguage());
        super.attachBaseContext(LanguageHelper.onAttach(base, Locale.getDefault().getLanguage()));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fonts.setDefaultFont(this, "DEFAULT", "ar_font.ttf");
        Fonts.setDefaultFont(this, "MONOSPACE", "ar_font.ttf");
        Fonts.setDefaultFont(this, "SERIF", "ar_font.ttf");
        Fonts.setDefaultFont(this, "SANS_SERIF", "ar_font.ttf");


    }
}