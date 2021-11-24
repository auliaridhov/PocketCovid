package tik.itera.covid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tik.itera.covid.BuildConfig;
import tik.itera.covid.R;
import tik.itera.covid.model.presensi.FakeGps;
import tik.itera.covid.model.presensi.FakeGpsList;
import tik.itera.covid.network.BaseApiService;
import tik.itera.covid.network.RetrofitInstance;
import tik.itera.covid.session.SessionManager;
import tik.itera.covid.utils.MyUtils;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Request Options
    private RequestOptions options;

    //Services
    private SessionManager session;
    private BaseApiService baseApiService;

    //String
    private String sEmail, sIDPegawai, sNamaUnit, sNamaPegawai, sFoto, sToken;

    //Palate
    private DrawerLayout drawer;
    private ImageView imgToolBar;
    private NavigationView navigationView;

    //Array permissions
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };


    //Utils
    private MyUtils myUtils;
    private Activity mActivity;

    //Permission
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    //fake gps variable
    boolean isMockLocation = false;
    ArrayList<String> arrayFake = new ArrayList<String>();
    MyTimerTask myTimerTask;
    Timer timer = new Timer();
    private Dialog myDialog;
    private boolean cekYOU = false;
    //end fake gps variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);

        options = new RequestOptions()
                .centerCrop();

        imgToolBar = findViewById(R.id.imgMenu);
        drawer = findViewById(R.id.drawer_layout);

        session = new SessionManager(getApplicationContext());
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

        mActivity = DrawerActivity.this;
        myUtils = new MyUtils(DrawerActivity.this);

        if (MyUtils.isEmulator() || android.os.Build.MODEL.equals("google_sdk")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Mohon Maaf !!\nAplikasi Pocket Covid tidak dapat berjalan di Emulator...");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finishAffinity();
                }
            });
            AlertDialog mAlertDialog = builder.create();
            mAlertDialog.show();
        }

        baseApiService = RetrofitInstance.getRetrofitInstance(sToken).create(BaseApiService.class);

        imgToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) drawer.openDrawer(GravityCompat.START);
                else drawer.closeDrawer(GravityCompat.END);
            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = findViewById(R.id.drawer_layout);

        View header = navigationView.getHeaderView(0);
        TextView txtNama = (TextView) header.findViewById(R.id.txt_user);
        if (sNamaPegawai != null){
            txtNama.setText(sNamaPegawai);
        }

        ImageView imageUser = (ImageView) header.findViewById(R.id.img_user);
        Glide.with(DrawerActivity.this).
                load(sFoto)
                .apply(options)
                .into(imageUser);

        if(timer != null){
            timer.cancel();
        }

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        if (cekYOU){
            timer.schedule(myTimerTask, 1000);
        }else{
            timer.schedule(myTimerTask, 1000, 1000);
        }

        checkPermission();
        initFake();
    }

    /**
     * Fake GPS Function
     * **/
    private void initFake(){
        Call<FakeGpsList> call = baseApiService.getFake();
        Log.wtf("URL Called : ", call.request().url() + "");
        call.enqueue(new Callback<FakeGpsList>() {
            @Override
            public void onResponse(Call<FakeGpsList> call, Response<FakeGpsList> response) {
                try {
                    if (response.body() != null) {
                        Log.wtf("Data : ", response.body().toString());
                        getFakeGpro(response.body().getFakeGpsList());
                    } else if (response.errorBody() != null) {
                        Log.wtf("Data : ", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("Error : ", e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<FakeGpsList> call, Throwable t) {
                t.printStackTrace();
                Log.wtf("Failure : ", t.getMessage());
            }
        });
    }

    private void getFakeGpro(ArrayList<FakeGps> fakeList){
        arrayFake.clear();
        for (int i=0; i<fakeList.size(); i++){
            String sListFake = fakeList.get(i).getList_fake();
            arrayFake.add(sListFake);
        }
    }

    private boolean isMockSettingsON() {
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
            } else {
                isMockLocation = !android.provider.Settings.Secure.getString(this.getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return isMockLocation;
        }
        return isMockLocation;
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    private void hasilCek(){
        nampilData();
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                finishAffinity();
            }
        }, 5000);
    }

    private void nampilData(){
        myDialog = new Dialog(DrawerActivity.this);
        myDialog.setContentView(R.layout.hadist_layout);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void cekFakeAvail(){
        int fakeSize = arrayFake.size();
        PackageManager pm = getApplicationContext().getPackageManager();
        for (int i=0; i< fakeSize; i++){
            String sFake = arrayFake.get(i);
            Log.d("Fake GPS : ",sFake);
            if (isPackageInstalled(sFake, pm)){
                hasilCek();
                if (timer!=null){
                    timer.cancel();
                    timer = null;
                }
            }
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    if (isMockSettingsON()) {
                        hasilCek();
                        if (timer!=null){
                            timer.cancel();
                            timer = null;
                        }
                    }
                    cekFakeAvail();
                    cekYOU = true;
                }
            });
        }
    }
    //end fake gps

    private void setFirstItemNavigationView() {
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displaySelectedScreen(int id){
        Fragment fragment = null;
        switch(id){
            case R.id.nav_home:
                fragment = new FragmentPresensi();
                break;
            case R.id.nav_info:
                fragment = new FragmentAbout();
                break;
            case R.id.nav_user:
                fragment = new FragmentUser();
                break;
            case R.id.nav_power:
                TampilLogout();
                break;
        }
        if (fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_layout, fragment);
            ft.commit();
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    public void TampilLogout(){
        new AlertDialog.Builder(DrawerActivity.this)
            .setTitle("Logout")
            .setMessage("Anda Ingin Logout?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int i){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }
                        session.logoutUser();
                    }
                }, 2000);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int i){
                    dialog.cancel();
                }
            }).show();
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)){

                ActivityCompat.requestPermissions(mActivity, PERMISSIONS, MY_PERMISSIONS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(mActivity, PERMISSIONS, MY_PERMISSIONS_REQUEST_CODE);
            }
        }else {
            Log.e("Permission: ", "Permissions Already Granted !!");
            setFirstItemNavigationView();
            displaySelectedScreen(R.id.nav_home);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(permissions.length == 0){
            return;
        }

        boolean allPermissionsGranted = true;
        if(grantResults.length>0){
            for(int grantResult: grantResults){
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    allPermissionsGranted = false;
                    break;
                }
            }
        }

        if(!allPermissionsGranted){
            boolean somePermissionsForeverDenied = false;
            for(String permission: permissions){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                    checkPermission();
                    Log.e("Permission: ", "User Has Denied Permission !!");
                }else{
                    if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                        Log.e("Permission: ", "User Has Allowed Permission");
                    } else{
                        Log.e("Permission: ", "Dont'Ask True...");
                        somePermissionsForeverDenied = true;
                    }
                }
            }

            if(somePermissionsForeverDenied){
                AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.this);
                builder.setTitle("Permissions Required");
                builder.setMessage("This app won't work properly unless you allow Permission. Please open Settings, then Permissions, and allow all listed items.");
                builder.setCancelable(false);
                builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        myUtils.isPermissionGranted(false);
                    }
                });
                AlertDialog mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        } else {
            Log.e("Permission: ", "Permissions Has Been Granted...");

            setFirstItemNavigationView();
            displaySelectedScreen(R.id.nav_home);

            switch (requestCode) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(timer != null){
            timer.cancel();
        }

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        if(cekYOU){
            timer.schedule(myTimerTask, 1000);
        }else{
            timer.schedule(myTimerTask, 1000, 1000);
        }

        initFake();
    }

}