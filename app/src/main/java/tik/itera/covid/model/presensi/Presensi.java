package tik.itera.covid.model.presensi;

import com.google.gson.annotations.SerializedName;

public class Presensi {



    @SerializedName("libur")
    private String libur;

    @SerializedName("co_status")
    private String coStatus;

    @SerializedName("tgl_bln_thn")
    private String tgl_bln_thn;

    @SerializedName("jam_masuk")
    private String jam_masuk;

    @SerializedName("jam_pulang")
    private String jam_pulang;

    @SerializedName("jam_siang")
    private String jam_siang;

    @SerializedName("status")
    private String status;

    @SerializedName("terlambat")
    private String terlambat;

    public String getLibur() {
        return libur;
    }

    public void setLibur(String libur) {
        this.libur = libur;
    }

    public String getCoStatus() {
        return coStatus;
    }

    public void setCoStatus(String coStatus) {
        this.coStatus = coStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTerlambat() {
        return terlambat;
    }

    public void setTerlambat(String terlambat) {
        this.terlambat = terlambat;
    }

    public String getTgl_bln_thn() {
        return tgl_bln_thn;
    }

    public void setTgl_bln_thn(String tgl_bln_thn) {
        this.tgl_bln_thn = tgl_bln_thn;
    }

    public String getJam_masuk() {
        return jam_masuk;
    }

    public void setJam_masuk(String jam_masuk) {
        this.jam_masuk = jam_masuk;
    }

    public String getJam_pulang() {
        return jam_pulang;
    }

    public void setJam_pulang(String jam_pulang) {
        this.jam_pulang = jam_pulang;
    }

    public String getJam_siang() {
        return jam_siang;
    }

    public void setJam_siang(String jam_siang) {
        this.jam_siang = jam_siang;
    }
}
