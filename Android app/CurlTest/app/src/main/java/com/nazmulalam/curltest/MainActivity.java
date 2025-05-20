package com.nazmulalam.curltest;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    // Create a background thread executor
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    // Create a handler for the main thread
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private TextView responseTextView; // TextView to display the response

    Bitmap imageBitmap;
    static ImageView imageView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GlobalResourch global = (GlobalResourch)getApplicationContext();

        SharedPreferences sharedPreferences = getSharedPreferences("CurlApp", MODE_PRIVATE);
        responseTextView = findViewById(R.id.response_text_view); // Assuming you have a TextView in your layout

        WebView wed = findViewById(R.id.wed);
        imageView = findViewById(R.id.imageView);

        wed.getSettings().setJavaScriptEnabled(true);
        wed.setWebViewClient(new WebViewClient(){

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // Capture request headers
                Map<String, String> requestHeaders = request.getRequestHeaders();

                // Get the User-Agent header
                String userAgent = requestHeaders.get("User-Agent");
                if (userAgent != null) {
                    Log.d("User-Agent", "User-Agent: " + userAgent);
                   global.setAgent(userAgent);
                   // SharedPreferences.Editor editor = sharedPreferences.edit();
                 //   editor.putString("agent", userAgent);
                 //   editor.apply();
                } else {
                    Log.d("User-Agent", "User-Agent header not found");
                }

                // Continue loading the request
                // If you need to modify headers or return a custom response, you can do so here.
                return super.shouldInterceptRequest(view, request);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Access cookies
                Log.d("PageFinished", "Page loaded: " + url);

                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("Cookies", "All the cookies for this URL: " + cookies);

                // Check if __test cookie exists
                if (cookies != null && cookies.contains("__test")) {
                    // You can now work with the __test cookie value
                    String testCookie = extractTestCookie(cookies);
                    Log.d("TestCookie", "__test cookie value: " + testCookie);

                    global.setToken(testCookie);
                  //  Log.d("HTTPHELPER12", "FromMain"+global.getToken());
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("token", testCookie);
//                    editor.apply();

                    fetchData();
                }
            }

            // Extract the __test cookie value from the cookie string
            private String extractTestCookie(String cookies) {
                String[] cookieArray = cookies.split(";");
                for (String cookie : cookieArray) {
                    if (cookie.trim().startsWith("__test=")) {
                        return cookie.split("=")[1];
                    }
                }
                return null;
            }
        });

        wed.loadUrl("http://nazmulalamshuvo.42web.io/eijagarbackup/curl/");
        // Send the HTTP GET request
        // Example URL (replace with the URL you provided)

    }

    private void fetchData() {

//        // Example usage of the helper method
        String url = "https://nazmulalamshuvo.42web.io/eijagarbackup/curl/api.php";
        String postData = "name=John&age=25";  // Post data can be null for GET requests

        executor.execute(() -> {
            // Use the helper class method to get a response from the server

            String jsonResponse = HttpHelper.getRequest(this, url, postData); // Can pass null for GET request

            // Post the result back to the main thread to update the UI
            handler.post(() -> {
                responseTextView.setText(jsonResponse);
                Log.d("HTTPHELPER", jsonResponse);
            });
        });

        String imageUrl = "https://nazmulalamshuvo.42web.io/eijagarbackup/sla/logo.png";
        executor.execute(() -> {

            Bitmap image = HttpImageHelper.fetchImage(this, imageUrl, "null");
            handler.post(() -> {
                imageView.setImageBitmap(image);
                Log.d("RETURNIMG", "Image set");
            });
        });

    }


}