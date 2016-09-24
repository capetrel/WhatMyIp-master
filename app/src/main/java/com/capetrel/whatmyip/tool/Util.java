package com.capetrel.whatmyip.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.capetrel.whatmyip.models.Favoris;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by capetrel on 27/07/2016.
 */
public class Util {

    public static final String PREF_FILE_NAME = "pref_file_name";
    public static final String KEY_PREFS_FAVORITE = "key_prefs_favourite";

    private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

    /**
     * Determine if the given string is a valid IPv4 or IPv6 address.  This method
     * uses pattern matching to see if the given string could be a valid IP address.
     *
     * @param ipAddress A string that is to be examined to verify whether or not
     *  it could be a valid IP address.
     * @return <code>true</code> if the string is a value that is a valid IP address,
     *  <code>false</code> otherwise.
     */
    public static boolean isIpAddress(String ipAddress) {
        Pattern pattern = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
        Matcher m1 = pattern.matcher(ipAddress);
        return m1.matches();
    }

    public static void writeFavouritePrefs(Context context, ArrayList<Favoris> dataForFavs) {

        // Cette méthode permet de sauvegarder la liste des favoris dans les préférences.

        SharedPreferences preferencesList = context.getSharedPreferences(Util.PREF_FILE_NAME, 0);
        SharedPreferences.Editor editor = preferencesList.edit();

        // Initialisation d'un tableau JSON
        JSONArray jsonArray = new JSONArray();

        // Ajout dans le tableau de chaque favoris au format JSON
        for (int i = 0; i < dataForFavs.size(); i++) {
            jsonArray.put(dataForFavs.get(i).strJson);
        }

        // Ecriture dans les préférences en écrasant l'existant.
        editor.putString(Util.KEY_PREFS_FAVORITE, jsonArray.toString());
        editor.commit();

        // map pour recupérer en log le contenu du shared preferences
        //Map<String, ?> keys =preferencesList.getAll();
        //for (Map.Entry<String, ?> entry : keys.entrySet()) {
        //    Log.d("lol map values", entry.getKey() + ": " + entry.getValue().toString());
        //}
    }

    public static ArrayList<Favoris> getFavoriteFromPrefs(Context context) {

        // 1° - Initialisation de la liste vide des favoris
        ArrayList<Favoris> arrayListIpFavoris = new ArrayList<>();

        // 2° - Récupération des favoris (s'ils existent) via les preferences.
        SharedPreferences preferences = context.getSharedPreferences(Util.PREF_FILE_NAME, 0);
        String strFavourite = preferences.getString(Util.KEY_PREFS_FAVORITE, null);

        // 3° - Ajout de chaque favoris si la liste est non vide
        if (strFavourite != null) {
            try {
                JSONArray jsonArray = new JSONArray(strFavourite);
                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayListIpFavoris.add(new Favoris(jsonArray.getString(i)));
                }
            } catch (Exception e) {
            }
        }

        return arrayListIpFavoris;
    }

}
