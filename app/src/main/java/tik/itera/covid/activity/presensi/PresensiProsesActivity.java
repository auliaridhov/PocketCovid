package tik.itera.covid.activity.presensi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tik.itera.covid.R;
import tik.itera.covid.activity.DrawerActivity;
import tik.itera.covid.model.presensi.MoPresensiResult;
import tik.itera.covid.network.BaseApiService;
import tik.itera.covid.network.RetrofitInstance;
import tik.itera.covid.session.SessionManager;
import tik.itera.covid.utils.MyUtils;

public class PresensiProsesActivity extends AppCompatActivity {

    //Session
    private SessionManager session;
    private BaseApiService baseApiService;

    //String
    private String sEmail, sIDPegawai, sNamaUnit, sNamaPegawai, sFoto, sToken;
    private String sKet, sHasil;
    private String sCovidLatitude, sCovidLongitude, sCovidAlamatLengkap, sCovidKota;

    //My Utils
    private MyUtils myUtils;

    //Pallate
    private RippleBackground rippleBackground;
    private ImageView imageViewStatus;
    private TextView statusPresensi, txtStatusDeskripsi;
    private Button buttonOkay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presensi_proses_activity);

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

        Bundle bund = getIntent().getExtras();
        if (bund != null){
            sKet = bund.getString("Ket");
            sCovidLatitude = bund.getString("sCovidLatitude");
            sCovidLongitude = bund.getString("sCovidLongitude");
            sCovidAlamatLengkap = bund.getString("sCovidAlamatLengkap");
            sCovidKota = bund.getString("sCovidKota");
        }

        baseApiService = RetrofitInstance.getRetrofitInstance(sToken).create(BaseApiService.class);
        myUtils = new MyUtils(PresensiProsesActivity.this);

        rippleBackground = findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        imageViewStatus = findViewById(R.id.centerImageStatus);
        statusPresensi = findViewById(R.id.txtStatusPresensi);
        txtStatusDeskripsi = findViewById(R.id.txtStatusDeskripsi);
        buttonOkay = findViewById(R.id.buttonOkay);

        buttonOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DrawerActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                kirimPresensi();
            }
        }, 3000);

    }

    private void kirimPresensi(){
        Call<MoPresensiResult> callKirimPesan = baseApiService.getKirimPresensiCovid(
                    sIDPegawai, sKet, sCovidLatitude, sCovidLongitude,
                    sCovidAlamatLengkap, sCovidKota,
                    myUtils.getDeviceId(), myUtils.getDeviceName(), getResources().getString(R.string.version_apps));

        callKirimPesan.enqueue(new Callback<MoPresensiResult>() {
            @Override
            public void onResponse(Call<MoPresensiResult> call, Response<MoPresensiResult> response) {
                try {
                    if (response.body() != null) {
                        if (!response.body().getMessage().equals("Token Tidak Valid!")){
                            sHasil = response.body().getMessage();
                            boolean status = response.body().isStatus();
                            if (status){
                                nampilHasilBerhasil(sHasil);
                            } else {
                                nampilHasilGagal(sHasil);
                            }
                            Log.wtf("Result : ", response.body().getMessage());
                        }else{
                            nampilError(response.body().getMessage());
                            Log.wtf("Gagal Token : ", response.body().getMessage());
                        }
                    } else {
                        nampilError(response.body().getMessage());
                        Log.wtf("Error Body : ", response.errorBody().string());
                    }
                } catch (Exception e) {
                    nampilError(e.getMessage());
                    Log.wtf("Error Try Exception : ", e.getMessage());
                }


            }
            @Override
            public void onFailure(Call<MoPresensiResult> call, Throwable t) {
                t.printStackTrace();
                Log.wtf("Failure : ", t.getMessage());
                nampilError(t.getMessage());
            }
        });
    }

    private void nampilError(String xMsg){
        rippleBackground.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        imageViewStatus.setImageResource(R.drawable.ic_presensi_failed);
        statusPresensi.setText("Gagal Melakukan Presensi...");
        txtStatusDeskripsi.setText("Kirimkan Error Ini Kepada UPT TIK : "+xMsg);
    }

    private void nampilHasil(String sD){
        rippleBackground.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();

        //Masuk
        if (sD.equals("Sudah Melakukan Presensi Masuk...")){
            imageViewStatus.setImageResource(R.drawable.ic_presensi_failed);
            statusPresensi.setText(sD);
            txtStatusDeskripsi.setText("Presensi Masuk "+getDate()+" Sudah Tercatat Dalam Sistem...");
        }else if(sD.equals("Berhasil Mengirim Presensi Masuk...")){
            presensiTercatat(sD);
        }else if(sD.equals("Gagal Mengirim Presensi Masuk...")){
            presensiTidakTercatat(sD);
        }

        //Siang
        if (sD.equals("Sudah Melakukan Presensi Siang...")){
            imageViewStatus.setImageResource(R.drawable.ic_presensi_failed);
            statusPresensi.setText(sD);
            txtStatusDeskripsi.setText("Presensi Siang "+getDate()+" Sudah Tercatat Dalam Sistem...");
        }else if(sD.equals("Berhasil Mengirim Presensi Siang...")){
            presensiTercatat(sD);
        }else if(sD.equals("Gagal Mengirim Presensi Siang...")){
            presensiTidakTercatat(sD);
        }else if (sD.equals("Maaf Waktu Sudah Melebihi Batas Presensi Siang...")){
            presensiTidakTercatat(sD);
        }else if (sD.equals("Belum Waktunya Prensensi Siang...")){
            presensiTidakTercatat(sD);
        }else if (sD.equals("Maaf Waktu Sudah Melebihi Batas Presensi Siang...")){
            presensiTidakTercatat(sD);
        }else if (sD.equals("Gagal Mengirim Presensi Siang... Keterangan : Presensi Siang Telat...")){
            presensiTidakTercatat(sD);
        }

        //Pulang
       if(sD.equals("Berhasil Mengirim Presensi Pulang...")){
           presensiTercatat(sD);
        }else if(sD.equals("Gagal Mengirim Presensi Pulang...")){
           presensiTidakTercatat(sD);
        }

        //Hari Libur
        if (sD.equals("Hari Libur Tidak Dapat Melakukan Presensi...")){
            presensiTidakTercatat(sD);
        }

        //Data Kosong
        if (sD.equals("Data Kosong!")){
            presensiTidakTercatat(sD);
        }

        //Data Tidak Sesuai
        if (sD.equals("Data Tidak Sesuai!")){
            presensiTidakTercatat(sD);
        }
    }

    private void nampilHasilBerhasil(String keterangan){
        rippleBackground.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        imageViewStatus.setImageResource(R.drawable.ic_presensi_succes);
        statusPresensi.setText(keterangan);
        txtStatusDeskripsi.setText("Presensi tercatat Dalam Sistem...");
    }

    private void nampilHasilGagal(String keterangan){
        rippleBackground.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        imageViewStatus.setImageResource(R.drawable.ic_presensi_failed);
        statusPresensi.setText(keterangan);
        txtStatusDeskripsi.setText("Presensi Tidak Tercatat Dalam Sistem...");
    }

    private void presensiTidakTercatat(String sData){
        imageViewStatus.setImageResource(R.drawable.ic_presensi_failed);
        statusPresensi.setText(sData);
        txtStatusDeskripsi.setText("Presensi "+getDate()+" Tidak Tercatat Dalam Sistem...");
    }

    private void presensiTercatat(String sData){
        imageViewStatus.setImageResource(R.drawable.ic_presensi_succes);
        statusPresensi.setText(sData);
        txtStatusDeskripsi.setText("Presensi Pulang "+getDate()+" Sudah Tercatat Dalam Sistem...");
    }

    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMMM yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
}

