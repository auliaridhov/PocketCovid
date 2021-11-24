package tik.itera.covid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tik.itera.covid.R;
import tik.itera.covid.model.login.MoUser;
import tik.itera.covid.model.login.MoUserList;
import tik.itera.covid.network.BaseApiService;
import tik.itera.covid.network.RetrofitInstance;
import tik.itera.covid.session.SessionManager;
import tik.itera.covid.utils.MyUtils;

public class LoginActivity extends AppCompatActivity {

    //Session
    private SessionManager session;
    private BaseApiService baseApiService;

    //Result Json
    private String sPesan;
    private boolean bStatus = false;

    //Utils
    private MyUtils myUtils;
    private Activity mActivity;

    //Validate Login
    private String PasswordHolder, UserHolder;
    private Boolean CheckEditText;

    //Edit User
    private EditText edUser, edPass;
    private Button bLogin;

    //Var Sessions
    private String sEmail, sIDPegawai, sNamaUnit, sNamaPegawai, sFoto, sToken;

    //Permission
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    //Array permissions
    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        edUser = findViewById(R.id.edUser);
        edPass = findViewById(R.id.edPass);
        bLogin = findViewById(R.id.bLogin);

        mActivity = LoginActivity.this;
        myUtils = new MyUtils(LoginActivity.this);
        session = new SessionManager(LoginActivity.this);

        if (MyUtils.isEmulator() || android.os.Build.MODEL.equals("google_sdk")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

        if(session.isLoggedIn()){
            HashMap<String, String> sUsernya = session.getUserDetails();
            sToken = sUsernya.get(SessionManager.KEY_TOKEN);
            baseApiService = RetrofitInstance.getRetrofitInstance(sToken).create(BaseApiService.class);
            Intent intent = new Intent(
                    LoginActivity.this, DrawerActivity.class);
            startActivity(intent);
            finish();
        }else {
            checkPermission();
        }

        baseApiService = RetrofitInstance.getRetrofitInstance("Auth").create(BaseApiService.class);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekLoginTwo();
            }
        });
    }

    public void CheckEditTextIsEmptyOrNot(){
        UserHolder = edUser.getText().toString();
        PasswordHolder = edPass.getText().toString();
        if(TextUtils.isEmpty(UserHolder) || TextUtils.isEmpty(PasswordHolder)){
            CheckEditText = false;
        }else {
            CheckEditText = true ;
        }
    }

    private void cekLoginTwo(){
        CheckEditTextIsEmptyOrNot();
        if(CheckEditText){
            SyncCek();
        }else {
            Toast.makeText(LoginActivity.this,
                    "Data Masih Kosong !", Toast.LENGTH_LONG).show();
        }
    }

    private void SyncCek() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    UserLoginFunction(UserHolder, PasswordHolder);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void UserLoginFunction(String sUser, String sPass){
        Call<MoUser> funCall = baseApiService.getCovidLogin(sUser, sPass, myUtils.getDeviceId(), myUtils.getDeviceName());
        funCall.enqueue(new Callback<MoUser>() {
            @Override
            public void onResponse(Call<MoUser> call, Response<MoUser> response) {
                try {
                    if (response.body() != null){
                        sPesan = response.body().getPesan();
                        bStatus = response.body().isStatus();

                        if (bStatus){
                            callUserDetail(response.body().getUserList());
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Pocket Notification");
                            builder.setMessage(sPesan);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }

                    } else {
                        Log.wtf("Login : ", response.errorBody().string());
                        Toast.makeText(getApplicationContext(), "Username/Passsword Tidak Sesuai !!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.wtf("Error : ",e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<MoUser> call, Throwable t) {
                t.printStackTrace();
                Log.wtf("Failure : ",t.getMessage());
            }
        });
    }

    private void callUserDetail(MoUserList userList) {
        sEmail = userList.getEmail();
        sIDPegawai = userList.getId_pegawai();
        sNamaUnit = userList.getNama_unit();
        sNamaPegawai = userList.getNama_pegawai();
        sFoto = userList.getFoto();
        sToken = userList.getToken();

        JSONObject tags = new JSONObject();
        try {
            tags.put("email", sEmail);
            tags.put("role", "Covid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OneSignal.sendTags(tags);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Pocket Notification");
        builder.setMessage(sPesan);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                session.createLoginSession(sEmail, sIDPegawai, sNamaUnit, sNamaPegawai, sFoto, sToken);
                RetrofitInstance.clearRetrofitInstance();
                Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
        });
        AlertDialog mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) +
                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_PHONE_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)){

                ActivityCompat.requestPermissions(mActivity, PERMISSIONS, MY_PERMISSIONS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(mActivity, PERMISSIONS, MY_PERMISSIONS_REQUEST_CODE);
            }
        }else {
            Log.e("Permission: ", "Permissions Already Granted !!");
            Log.e("IMEI: ", myUtils.getDeviceId());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
            Log.e("IMEI: ", myUtils.getDeviceId());

            switch (requestCode) {

            }
        }
    }

}
