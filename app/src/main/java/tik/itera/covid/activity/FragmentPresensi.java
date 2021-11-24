package tik.itera.covid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tik.itera.covid.BuildConfig;
import tik.itera.covid.R;
import tik.itera.covid.activity.gps.GPSTracker;
import tik.itera.covid.activity.presensi.PresensiProsesActivity;
import tik.itera.covid.adapter.presensi.PresensiAdapter;
import tik.itera.covid.model.presensi.FakeGps;
import tik.itera.covid.model.presensi.FakeGpsList;
import tik.itera.covid.model.presensi.Presensi;
import tik.itera.covid.model.presensi.PresensiList;
import tik.itera.covid.network.BaseApiService;
import tik.itera.covid.network.RetrofitInstance;
import tik.itera.covid.session.SessionManager;
import tik.itera.covid.utils.MyUtils;

public class FragmentPresensi extends Fragment  implements PresensiAdapter.RecyclerPresensiAdapter{

    //Session
    private SessionManager session;
    private BaseApiService baseApiService;

    //String
    private String sEmail, sIDPegawai, sNamaUnit, sNamaPegawai, sFoto, sToken;
    private static String xLat, xLong, xAlamatLengkap, xCity;

    //My Utils
    private View view;
    private MyUtils myUtils;

    //Pallate
    private TextView txtRiwayatTanggal;
    public static ImageView imgPresensi, imgLapor;

    //Adapter
    private RecyclerView listView;
    private PresensiAdapter presensiAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //Gps Tracker
    private static GPSTracker gpsTracker;

    //Timer
    private Timer timer = new Timer();


    public FragmentPresensi() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_presensi, container, false);

        nampilLayout();

        return view;
    }

    private void nampilLayout(){

        myUtils = new MyUtils(view.getContext());
        gpsTracker = new GPSTracker(view.getContext());

        session = new SessionManager(view.getContext());
        session.checkLogin();

        if(session.isLoggedIn()) {
            HashMap<String, String> sUser = session.getUserDetails();
            sEmail = sUser.get(SessionManager.KEY_EMAIL);
            sIDPegawai = sUser.get(SessionManager.KEY_ID_PEGAWAI);
            sNamaUnit = sUser.get(SessionManager.KEY_NAMA_UNIT);
            sNamaPegawai = sUser.get(SessionManager.KEY_NAMA_PEGAWAI);
            sFoto = sUser.get(SessionManager.KEY_FOTO);
            sToken = sUser.get(SessionManager.KEY_TOKEN);
        }

        baseApiService = RetrofitInstance.getRetrofitInstance(sToken).create(BaseApiService.class);

        txtRiwayatTanggal = view.findViewById(R.id.txtRiwayatTanggal);
        txtRiwayatTanggal.setText(myUtils.firstLastDate());

        imgPresensi = view.findViewById(R.id.imgPresensi);
        imgPresensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nampilBottomPresensi();
            }
        });

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initPresensi();
            }
        });

        imgLapor = view.findViewById(R.id.imgLapor);
        imgLapor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nampillapor();
            }
        });

        addRedToolTipViewPresensi();
        addRedToolTipViewLapor();

        initPresensi();

        if(myUtils.isOnline()) {
            getGPSActivate();
        } else {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Pocket Notification")
                    .setMessage("Mohon Maaf !!\nAplikasi ini membutuhkan koneksi internet yang stabil...")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int i){
                            getActivity().finish();
                        }
                    }).show();
        }
    }

    private void nampilPopupHome(String xStatus, String xTitle, String xUrl){
        TextView txtClose, txtTitle;
        WebView webView;

        final Dialog myDialog = new Dialog(view.getContext());
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.popup_home);
        myDialog.setCancelable(true); //Gak bisa dicancel

        txtClose = myDialog.findViewById(R.id.txtClose);
        txtTitle = myDialog.findViewById(R.id.txtTitle);
        webView = myDialog.findViewById(R.id.web_view);

        txtTitle.setText(xTitle);

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
        webView.loadUrl(xUrl);

        if (xStatus.equals("Iklan") || xStatus.equals("Info")){
            txtClose.setVisibility(View.VISIBLE);
            txtClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
        }

        if (xStatus.equals("Update") || xStatus.equals("Suspend") || xStatus.equals("Block")){
            txtClose.setVisibility(View.GONE);
        }

        myDialog.show();
    }

    public void nampillapor() {
        String xURL = "http://corona.itera.ac.id/covid?ids="+sIDPegawai+"&lat="+xLat+"&lon="+xLong+"&address="+xAlamatLengkap+"&tipe=staff";
        Log.d("Colorna : ", xURL);
        Intent i = new Intent(view.getContext(), WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Title","");
        bundle.putString("URL",xURL);
        i.putExtras(bundle);
        startActivity(i);
    }

    private void addRedToolTipViewLapor() {
        ViewTooltip
                .on(imgLapor)
                .position(ViewTooltip.Position.LEFT)
                .arrowSourceMargin(0)
                .arrowTargetMargin(0)
                .text("Klik Untuk Lapor...")
                .clickToHide(true)
                .color(getResources().getColor(R.color.colorGreen))
                .autoHide(true, 5000)
                .animation(new ViewTooltip.FadeTooltipAnimation(500))
                .onDisplay(new ViewTooltip.ListenerDisplay() {
                    @Override
                    public void onDisplay(View view) {
                        Log.d("ViewTooltip : ", "onDisplay...");
                    }
                })
                .show();
    }

    private void addRedToolTipViewPresensi() {
        ViewTooltip
                .on(imgPresensi)
                .position(ViewTooltip.Position.LEFT)
                .arrowSourceMargin(0)
                .arrowTargetMargin(0)
                .text("Klik Untuk Presensi...")
                .clickToHide(true)
                .color(getResources().getColor(R.color.colorGreen))
                .autoHide(true, 5000)
                .animation(new ViewTooltip.FadeTooltipAnimation(500))
                .onDisplay(new ViewTooltip.ListenerDisplay() {
                    @Override
                    public void onDisplay(View view) {
                        Log.d("ViewTooltip : ", "onDisplay...");
                    }
                })
                .show();
    }

    private void getGPSActivate(){
        gpsTracker.getLocation(); //Start Location

        if (gpsTracker.canGetLocation()) {
            xLat = String.valueOf(gpsTracker.getLatitude());
            xLong = String.valueOf(gpsTracker.getLongitude());

            xAlamatLengkap = (gpsTracker.getAlamatLengkap());
            xCity = (gpsTracker.getCity());
        }

        runTimerGps();
    }

    private void runTimerGps(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gpsTracker.canGetLocation()) {
                            xLat = String.valueOf(gpsTracker.getLatitude());
                            xLong = String.valueOf(gpsTracker.getLongitude());
                            xAlamatLengkap = (gpsTracker.getAlamatLengkap());
                            xCity = (gpsTracker.getCity());
                            /*Log.d("Timer Location : ", gpsTracker.sLocation);
                            Log.d("Timer Alamat : ", gpsTracker.getAlamatLengkap());
                            Log.d("Timer City : ", gpsTracker.getCity());*/
                        }
                    }
                });
            }
        }, 0,  5000);
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

        gpsTracker.stopUsingGPS();
    }

    private void nampilBottomPresensi(){
        ShowPresensi showPresensi = new ShowPresensi();
        showPresensi.show(getActivity().getSupportFragmentManager(), showPresensi.getTag());
    }

    public void initPresensi() {
        Call<PresensiList> callTicketStatus = baseApiService.listPresensiCovid(sIDPegawai);
        callTicketStatus.enqueue(new Callback<PresensiList>() {
            @Override
            public void onResponse(Call<PresensiList> call, Response<PresensiList> response) {
                try {
                    if (response.body() != null){
                        detailPresensi(response.body().getListPresensi());
                    }else {
                        Log.wtf("Status Presensi : ", response.errorBody().string());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<PresensiList> call, Throwable t) {
                t.printStackTrace();
                Log.wtf("Failure : ",t.getMessage());
            }
        });
    }

    private void detailPresensi(ArrayList<Presensi> presensis) {
        listView = view.findViewById(R.id.listPresensi);
        presensiAdapter = new PresensiAdapter(presensis,this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(presensiAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRecyclerpresensiSelected(Presensi presensi) {
    }

    public static class ShowPresensi extends BottomSheetDialogFragment{

        private View viewX;
        private TextView txtLocation, txtAlamat;
        private Spinner mySpinner;
        private Button btnProses;
        private SessionManager session;
        private String sIDPegawai = "kosong";

        private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            viewX = inflater.inflate(R.layout.popup_bottom_presensi, container, false);

            session = new SessionManager(viewX.getContext());
            if(session.isLoggedIn()) {
                HashMap<String, String> sUser = session.getUserDetails();
                sIDPegawai = sUser.get(SessionManager.KEY_ID_PEGAWAI);
            }
            nampilData();
            return viewX;
        }

        private void nampilData() {
            mySpinner = viewX.findViewById(R.id.spinerPresensi);
            txtLocation = viewX.findViewById(R.id.txtLocation);
            txtAlamat = viewX.findViewById(R.id.txtAlamat);
            btnProses = viewX.findViewById(R.id.btnProses);

            String[] sPresensiArray = new String[] {"Presensi Masuk", "Presensi Siang", "Presensi Pulang"};

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(viewX.getContext(), android.R.layout.simple_spinner_item, sPresensiArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(adapter);

            txtAlamat.setText(xAlamatLengkap);
            txtLocation.setText(xCity);

            btnProses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sArrayX = mySpinner.getSelectedItem().toString();
                    if (sArrayX.equals("Presensi Masuk")){
                        kirimPresensiWeb("Masuk");
                    }else if (sArrayX.equals("Presensi Siang")){
                        kirimPresensiWeb("Siang");
                    }else  if (sArrayX.equals("Presensi Pulang")){
                        kirimPresensiWeb("Pulang");
                    }
                }
            });
        }

        private void kirimPresensi(String sKet){


            Bundle bund = new Bundle();
            bund.putString("Ket", sKet);
            bund.putString("sCovidLatitude", xLat);
            bund.putString("sCovidLongitude", xLong);
            bund.putString("sCovidAlamatLengkap", xAlamatLengkap);
            bund.putString("sCovidKota", xCity);
            Intent i = new Intent(viewX.getContext(), PresensiProsesActivity.class);
            i.putExtras(bund);
            startActivity(i);
        }


        private void kirimPresensiWeb(String sKet){

            String xURL = "http://corona.itera.ac.id/covid?ids="+sIDPegawai+"&lat="+xLat+"&lon="+xLong+"&address="+xAlamatLengkap+"&tipe=staff";
            Log.d("Colorna : ", xURL);

            Bundle bund = new Bundle();
            bund.putString("Ket", sKet);

            bund.putString("sCovidLatitude", xLat);
            bund.putString("sCovidLongitude", xLong);
            bund.putString("sCovidAlamatLengkap", xAlamatLengkap);
            bund.putString("sCovidKota", xCity);
            bund.putString("Title","");
            bund.putString("URL",xURL);

            Intent i = new Intent(viewX.getContext(), WebViewBaruActivity.class);
            i.putExtras(bund);
            startActivity(i);
        }

    }

}
