package tik.itera.covid.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

import tik.itera.covid.R;
import tik.itera.covid.activity.presensi.PresensiProsesActivity;

public class WebViewBaruActivity extends AppCompatActivity {

    //Pallate
    private ProgressBar progressBar;
    private WebView webView;

    //String
    private String sTitle, sURL;

    //Timer
    private Timer timer = new Timer();


    private static String xLat, xLong, xAlamatLengkap, xCity, sKet;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_baru_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            sKet = bundle.getString("Ket").toString();
            xLat = bundle.getString("sCovidLatitude").toString();
            xLong = bundle.getString("sCovidLongitude").toString();
            xAlamatLengkap = bundle.getString("sCovidAlamatLengkap").toString();
            xCity = bundle.getString("sCovidKota").toString();

            sTitle = bundle.getString("Title").toString();
            sURL = bundle.getString("URL").toString();
        }

        getSupportActionBar().setTitle(sTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webView = findViewById(R.id.web_view);

        renderWebPage(sURL);

    }

    protected void renderWebPage(String urlToRender) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(100);
                runTimerGps();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(urlToRender);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            finish();
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view, menu);
        return true;
    }


    private void runTimerGps(){

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String urlResult = "http://corona.itera.ac.id/covid/result";
                        String webUrl = webView.getUrl();
                        Log.d("Result", webUrl);
                        if (webUrl.equals(urlResult)) {
                            kirimPresensi(sKet);
                        }
                    }
                });
            }
        }, 5000);
    }


    @Override
    public void onStop() {
        super.onStop();

        if(timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        Log.d("Timer : ","Stop Timer Gps !!");

    }

    private void kirimPresensi(String sKet){
        Bundle bund = new Bundle();
        bund.putString("Ket", sKet);

        bund.putString("sCovidLatitude", xLat);
        bund.putString("sCovidLongitude", xLong);
        bund.putString("sCovidAlamatLengkap", xAlamatLengkap);
        bund.putString("sCovidKota", xCity);

        Intent i = new Intent(WebViewBaruActivity.this, PresensiProsesActivity.class);
        i.putExtras(bund);
        startActivity(i);
    }
}

