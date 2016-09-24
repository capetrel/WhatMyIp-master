package com.capetrel.whatmyip.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by capetrel on 27/07/2016.
 */
public class Favoris {

    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String ISP = "isp";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String QUERY = "query";
    private static final String ZIP_CODE = "zip";

    public String mCity;
    public String mCountry;
    public String mCountryCode;
    public String mISP;
    public double mLatitude;
    public double mLongitude;
    public String mQuery;
    public String mZipCode;

    public String strJson;

    public Favoris(String strJson) throws JSONException {

        this.strJson = strJson;

        JSONObject json = new JSONObject(strJson);

        if (json.getString("status").compareTo("success") != 0) {
            throw new JSONException("status fail");
        }

        this.mCity = json.getString(CITY);
        this.mCountry = json.getString(COUNTRY);
        this.mCountryCode = json.getString(COUNTRY_CODE);
        this.mISP = json.getString(ISP);
        this.mLatitude = json.getDouble(LATITUDE);
        this.mLongitude = json.getDouble(LONGITUDE);
        this.mQuery = json.getString(QUERY);
        this.mZipCode = json.getString(ZIP_CODE);
    }
}

