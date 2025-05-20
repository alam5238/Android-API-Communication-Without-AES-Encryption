package com.nazmulalam.curltest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpImageHelper {

    public static Bitmap fetchImage(Context context, String urlString, String saveFilePath){
        SharedPreferences sharedPreferences = context.getSharedPreferences("CurlApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "default_value");
        String agent = sharedPreferences.getString("agent", "default_value");

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        Bitmap image = null;
        try {

            // Set up SSL to bypass certificate validation (equivalent to cURL --insecure)
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create a hostname verifier that doesn't verify the hostname
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


            // Create URL object
            URL url = new URL(urlString);

            // Open connection
            connection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Set the required headers (including User-Agent)
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            // connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,bn;q=0.8");
            // connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cookie", "__test="+token); // Add your cookie here
            //  connection.setRequestProperty("Sec-Fetch-Dest", "document");
            //   connection.setRequestProperty("Sec-Fetch-Mode", "navigate");
            //   connection.setRequestProperty("Sec-Fetch-Site", "none");
            // connection.setRequestProperty("Sec-Fetch-User", "?1");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", agent);
            //   connection.setRequestProperty("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"");
            // connection.setRequestProperty("sec-ch-ua-mobile", "?0");
            //  connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");

            // Get response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Success

                // Open input stream to read image data
                inputStream = new BufferedInputStream(connection.getInputStream());
                Log.d("ACCD","SUCCESS" + inputStream.toString());
                image = BitmapFactory.decodeStream(inputStream);
                // Open output stream to save the image
                //  fileOutputStream = new FileOutputStream(saveFilePath);

                // Buffer for image data
//                    byte[] buffer = new byte[1024];
//                    int bytesRead;
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        fileOutputStream.write(buffer, 0, bytesRead);
//                    }

                // Post success message to the main thread (UI update)

//                handler.post(() ->{
//                    imageView.setImageBitmap(image);
//                    Log.d("ACCD","SUCCESS");
//
//                });

            } else {
                // Post failure message to the main thread (UI update)
             //   handler.post(() ->  Log.d("ACCD","Faield" + responseCode));
            }

        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
          //  handler.post(() ->  Log.d("ACCD","error"+ e.getMessage()));

        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
                if (connection != null) connection.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return image;
    }
}
