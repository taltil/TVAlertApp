package com.example.tvalert.http;


import android.util.Log;

import com.example.tvalert.DTO.Authentication;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

public final class HttpUtils {

    public static final String NOT_AUTHORIZED = "Error: Not Authorized";
    public static final String NOT_FOUND = "Error: Not Found";
    public static final String SERVER_UNAVAILABLE = "Error: Server unavailable";
    private static final String LOG_TAG = HttpUtils.class.getSimpleName();

    private HttpUtils() {
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url, HashMap<String, String> headers) throws IOException {
        String jsonResponse = null;
        // If the URL is null, then return early.
        if (url == null) {
            jsonResponse = null;
        } else {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection = setHeaders(urlConnection, headers);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else if (urlConnection.getResponseCode() == 401) {
                    jsonResponse = NOT_AUTHORIZED;
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                } else if (urlConnection.getResponseCode() == 404) {
                    jsonResponse = NOT_FOUND;
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                } else if (urlConnection.getResponseCode() == 503) {
                    jsonResponse = SERVER_UNAVAILABLE;
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return jsonResponse;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequestPost(URL url, Authentication tokenDTO) throws IOException {
        String jsonResponse = null;

        if (url == null) {
            return jsonResponse;
        } else {
            Gson gson = new Gson();
            String jsonData = gson.toJson(tokenDTO);

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                wr.write(jsonData);
                wr.flush();

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                    jsonResponse = null;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem in retrieving the JSON results.", e);
                jsonResponse = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // Closing the input stream could throw an IOException, which is why
                    // the makeHttpRequest(URL url) method signature specifies than an IOException
                    // could be thrown.
                    inputStream.close();
                }
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static HttpURLConnection setHeaders(HttpURLConnection urlConnection, HashMap<String, String> headers) {
        for (String headerKey : headers.keySet()) {
            urlConnection.setRequestProperty(headerKey, headers.get(headerKey));
        }
        return urlConnection;
    }


}


