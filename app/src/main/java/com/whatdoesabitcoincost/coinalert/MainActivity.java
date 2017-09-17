package com.whatdoesabitcoincost.coinalert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.whatdoesabitcoincost.coinalert.models.Price.Price;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String UPDATE_URL = "https://whatdoesabitcoincost.com/api/current";
    public static final int TEN_SECONDS_IN_MILLI = 10000;
    public static final int PROGRESS_INCREMENT_MILLI = 20;

    private RequestQueue queue;
    private String currPrice = "$...";
    private ProgressBar mProgressBar;
    private TextView mPriceView;
    private AdView mAdView;
    private Timer timer;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mPriceView = (TextView) findViewById(R.id.price);
        queue = Volley.newRequestQueue(this);
        mPriceView.setText(currPrice);
        gson = new Gson();
        initProgressBar();
        initCoinbaseLink();
        initAdView();
        updatePrice();
    }

    private void initCoinbaseLink() {
        TextView textView = (TextView) findViewById(R.id.coinbaseLink);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initAdView() {
        MobileAds.initialize(this, "ca-app-pub-9324700519628898~5170710791");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A0364CF65CB79993BF4245F915A196EA")
                .build();
        mAdView.loadAd(request);
    }

    private void initProgressBar() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int progress = mProgressBar.getProgress() + PROGRESS_INCREMENT_MILLI;
                if (progress >= TEN_SECONDS_IN_MILLI) {
                    updatePrice();
                    mProgressBar.setProgress(0);
                } else {
                    mProgressBar.setProgress(progress);
                }
            }
        }, 0, PROGRESS_INCREMENT_MILLI);
    }

    private void updatePrice() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, UPDATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Price price = gson.fromJson(response, Price.class);
                        currPrice = "$" + price.currentPrice;
                        mPriceView.setText(currPrice);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                currPrice = "$...";
                mPriceView.setText(currPrice);
            }
        });
        queue.add(stringRequest);
    }
}
