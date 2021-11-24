package tik.itera.covid.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import tik.itera.covid.model.login.MoUser;
import tik.itera.covid.model.presensi.FakeGpsList;
import tik.itera.covid.model.presensi.MoPresensiResult;
import tik.itera.covid.model.presensi.PresensiList;
import tik.itera.covid.model.slider.MoSlider;

public interface BaseApiService {

    //Kirim Presensi Covid
    @FormUrlEncoded
    @POST("yukAbsenXYZ")
    Call<MoPresensiResult> getKirimPresensiCovid(@Field("id_peg") String id_peg,
                                                 @Field("keterangan") String keterangan,
                                                 @Field("co_lat") String co_lat,
                                                 @Field("co_lang") String co_lang,
                                                 @Field("co_alamat_lengkap") String co_alamat_lengkap,
                                                 @Field("co_kota") String co_kota,
                                                 @Field("imei") String imei,
                                                 @Field("device_name") String device_name,
                                                 @Field("versi") String versi);
    //Login
    @FormUrlEncoded
    @POST("getCovidLogin")
    Call<MoUser> getCovidLogin(@Field("username") String username,
                               @Field("password") String password,
                               @Field("imei") String imei,
                               @Field("device_name") String device_name);

    //Slider
    @GET("getSlider")
    Call<MoSlider> getSlider();

    //List Presensi
    @FormUrlEncoded
    @POST("listPresensiCovid")
    Call<PresensiList> listPresensiCovid(@Field("kode") String kode);

    //fake gps
    @GET("getFake")
    Call<FakeGpsList> getFake();

}
