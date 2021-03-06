package com.helpie.helpie;

/**
 * Created by DCM on 08/12/2016.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SaveSharedPreference {
    static final String PREF_USER_ID = "id";
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_EMAIL = "email";
    static final String PREF_USER_CONTACT = "contact";
    static final String PREF_USER_TYPE = "type";
    static final String PREF_DISTANCE = "distance";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUser(Context ctx, int id, String name, String mail, String contact, String type)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_ID, id);
        editor.putString(PREF_USER_NAME, name);
        editor.putString(PREF_USER_EMAIL, mail);
        editor.putString(PREF_USER_CONTACT, contact);
        editor.putString(PREF_USER_TYPE, type);
        editor.putInt(PREF_DISTANCE, 30);
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

    public static String getContact(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_CONTACT, "");
    }

    public static String getType(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_TYPE, "normal");
    }

    public static int getDistance(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(PREF_DISTANCE, 30);
    }

    public static void setDistance(Context ctx, int dist)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_DISTANCE, dist);
        editor.commit();
    }

    public static void clearAll(Context ctx)
    {
        setUser(ctx,0,"","","","");
    }



}