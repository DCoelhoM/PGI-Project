package com.helpie.helpie;

/**
 * Created by DCM on 08/12/2016.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SaveSharedPreference {
    static final String PREF_USER_ID = "id";
    static final String PREF_USER_NAME= "username";
    static final String PREF_USER_EMAIL= "email";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUser(Context ctx, int id, String name, String mail)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_ID, id);
        editor.putString(PREF_USER_NAME, name);
        editor.putString(PREF_USER_EMAIL, mail);
        editor.commit();
    }

    public static int getID(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(PREF_USER_ID, 0);
    }

    public static String getUsername(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
    }

    public static void clearAll(Context ctx)
    {
        setUser(ctx,0,"","");
    }



}