package com.example.tvalert;


import android.app.Activity;
import android.util.Log;

import com.example.tvalert.DTO.Authentication;
import com.example.tvalert.DTO.EpisodeData;
import com.example.tvalert.DTO.EpisodeDatum;
import com.example.tvalert.DTO.SeriesData;
import com.example.tvalert.DTO.SeriesDatum;
import com.example.tvalert.DTO.Token;
import com.example.tvalert.http.HttpUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * APIManger acquire data from TVDB API
 */
public class APIManger {

    private static final String LOG_TAG = APIManger.class.getSimpleName();

    private static final String BASE_URL = "https://api.thetvdb.com";
    private static final String SEARCH_SERIES = "/search/series";
    private static final String NAME_PARAMETER = "?name=";
    private static final String IMDB_ID_PARAMETER = "?imdbId=";
    private static final String SERIES = "/series/";
    private static final String EPISODE_DETAILS = "/episodes";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private static final String CONTENT_LABEL = "Content-Type";
    private static final String APPLICATION = "application/json";
    private static final String LANGUAGE_LABEL = "Accept-Language";
    private static final String language_choice = "en";
    private static String token = "";

    private static HashMap<String, String> headers = new HashMap<>();

    private APIManger() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * prepared headers for http request
     **/
    private static void setHeadersMap(Activity activity) {
        headers.put(CONTENT_LABEL, APPLICATION);
        headers.put(LANGUAGE_LABEL, language_choice);
        getAuthentication(activity);
    }

    /**
     * takes url and return json response as a string
     **/
    private static String getJson(URL url, final Activity activity) {
        String jsonResponse = null;
        try {
            jsonResponse = HttpUtils.makeHttpRequest(url, headers);
            if (Objects.equals(jsonResponse, HttpUtils.NOT_AUTHORIZED)) {
                getAuthentication(activity);
                jsonResponse = HttpUtils.makeHttpRequest(url, headers);
            }
            if (Objects.equals(jsonResponse, HttpUtils.NOT_FOUND)) {
                Log.v(LOG_TAG, "json response has not found");
                jsonResponse = null;
            }
            if (Objects.equals(jsonResponse, HttpUtils.SERVER_UNAVAILABLE)) {
                Log.v(LOG_TAG, "server is unavailable");
                jsonResponse = null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
            jsonResponse = null;
        }
        return jsonResponse;
    }

    /**
     * take series name and IMDB id and return series object
     **/
    public static List<SeriesDatum> fetchSeriesDatum(String name, String imdbId, final Activity activity) {
        URL url;
        String jsonResponse = null;
        List<SeriesDatum> seriesDatum = null;
        try {
            setHeadersMap(activity);
            if (!imdbId.isEmpty()) {
                url = createUrl(BASE_URL + SEARCH_SERIES + IMDB_ID_PARAMETER + imdbId);
                Log.v(LOG_TAG, "fetch series with IMDB: " + url.toString());
            } else if (!name.isEmpty()) {
                url = createUrl(BASE_URL + SEARCH_SERIES + NAME_PARAMETER + name);
                Log.v(LOG_TAG, "fetch series with name: " + url.toString());
            } else {
                Log.v(LOG_TAG, "Both name and IMDB id are missing.");
                return seriesDatum;
            }
            jsonResponse = getJson(url, activity);
            Gson gson = new Gson();
            SeriesData seriesData = gson.fromJson(jsonResponse, SeriesData.class);
            seriesDatum = seriesData.getData();
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error in retrieving series datum", e);
            seriesDatum = null;
        }
        return seriesDatum;
    }


    /**
     * get last episode details (if exist)
     **/
    public static List<EpisodeDatum> fetchEpisodesDatum(long id, Activity activity) {
        List<EpisodeDatum> episodeDatum = null;
        URL url;
        String jsonResponse = null;
        try {
            setHeadersMap(activity);
            url = createUrl(BASE_URL + SERIES + id + EPISODE_DETAILS);
            // Perform HTTP request to the URL and receive a JSON response back
            jsonResponse = getJson(url, activity);
            Gson gson = new Gson();
            EpisodeData episodeData = gson.fromJson(jsonResponse, EpisodeData.class);
            episodeDatum = episodeData.getData();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in fetching episode datum", e);
            episodeDatum = null;
        }
        return episodeDatum;
    }

    /**
     * add authentication header to send with url
     */
    private static void getAuthentication(final Activity activity) {
        String jsonResponse = "";

        if (token.isEmpty()) {
            URL loginUrl = createUrl(BASE_URL + "/login");
            Authentication tokenDTO = getAuthenticationDetails(activity);
            try {
                jsonResponse = HttpUtils.makeHttpRequestPost(loginUrl, tokenDTO);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the token JSON.", e);
            }
            Log.v("response login:  ", jsonResponse);
            if (jsonResponse.isEmpty()) {
                Log.v(LOG_TAG, "Problem retrieving the token JSON.");
            } else {
                Gson gson = new Gson();
                Token jsonToken = gson.fromJson(jsonResponse, Token.class);
                token = jsonToken.getToken();
                headers.put(AUTHORIZATION, AUTHORIZATION_TYPE + token);
            }
        } else {
            headers.put(AUTHORIZATION, AUTHORIZATION_TYPE + token);
            Log.v(LOG_TAG, "refresh token");
            URL refreshTokenUrl = createUrl(BASE_URL + "/refresh_token");
            try {
                jsonResponse = HttpUtils.makeHttpRequest(refreshTokenUrl, headers);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
            }
            if (!jsonResponse.isEmpty()) {
                Gson gson = new Gson();
                Token jsonToken = gson.fromJson(jsonResponse, Token.class);
                token = jsonToken.getToken();
                headers.put(AUTHORIZATION, AUTHORIZATION_TYPE + token);
            }
        }
    }

    /**
     * get authentication details from a file in this format:
     * {
     * "apikey": "string",
     * "userkey": "string",
     * "username": "string"
     * }
     */
    private static Authentication getAuthenticationDetails(Activity activity) {
        String filename = "token.txt";
        BufferedReader reader = null;
        StringBuilder authenticationJson = new StringBuilder();
        Gson gson = new Gson();
        Authentication tokenDTO = null;
        try {
            final InputStream file = activity.getAssets().open(filename);
            reader = new BufferedReader(new InputStreamReader(file));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                authenticationJson.append(currentLine);
            }
            tokenDTO = gson.fromJson(authenticationJson.toString(), Authentication.class);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in reading from file", e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Error in reading from file", ex);
            }
        }
        return tokenDTO;
    }
}
