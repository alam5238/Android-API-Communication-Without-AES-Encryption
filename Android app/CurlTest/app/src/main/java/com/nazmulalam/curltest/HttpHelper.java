package com.nazmulalam.curltest;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpHelper {


    public static String getRequest(Context context, String urlString, String postDataString) {

//        SharedPreferences sharedPreferences = context.getSharedPreferences("CurlApp", Context.MODE_PRIVATE);
//        String token = sharedPreferences.getString("token", "default_value");
//        String agent = sharedPreferences.getString("agent", "default_value");
       GlobalResourch glo = (GlobalResourch) context.getApplicationContext();
        String token = glo.getToken();
        String agent = glo.getAgent();

       Log.d("HTTPHELPER12", token + "AGENT:"+ agent);

        HttpURLConnection connection = null;
        try {

            if(isHttps(urlString)){

                // Set up SSL to bypass certificate validation (equivalent to cURL --insecure)
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                };

                // Install the all-trusting trust manager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                // Create a hostname verifier that doesn't verify the hostname
                HostnameVerifier allHostsValid = (hostname, session) -> true;
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            }

            // Create a URL object
            URL url = new URL(urlString);
            // Open the connection
            connection = (HttpURLConnection) url.openConnection();


            // Set headers
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");

            connection.setRequestProperty("Cookie", "__test="+token);
          //  connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", agent);


            // If postDataString is null, it's a GET request; otherwise, it's a POST request
            if (postDataString == null || postDataString.isEmpty()) {
                connection.setRequestMethod("GET");
            } else {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);  // Indicates POST request

                // Send POST data
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(postDataString);
                outputStream.flush();
                outputStream.close();
                Log.d("HTTPHELPER", "Method POST");
            }




            // Get response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Success
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Read the response
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Return the response as a string
                return response.toString();
            } else {
                return "Error: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }




        // Method to check if the URL uses HTTPS
        public static boolean isHttps(String url) {
            return url.toLowerCase().startsWith("https://");
        }

        // Method to check if the URL uses HTTP
        public static boolean isHttp(String url) {
            return url.toLowerCase().startsWith("http://");
        }

}
