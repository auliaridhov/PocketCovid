package tik.itera.covid.model.login;

import com.google.gson.annotations.SerializedName;

public class MoUserList {

    @SerializedName("email")
    private String email;
    @SerializedName("id_pegawai")
    private String id_pegawai;
    @SerializedName("nama_unit")
    private String nama_unit;
    @SerializedName("nama_pegawai")
    private String nama_pegawai;
    @SerializedName("foto")
    private String foto;
    @SerializedName("token")
    private String token;

    public MoUserList(String email, String id_pegawai, String nama_unit, String nama_pegawai, String foto, String token) {
        this.email = email;
        this.id_pegawai = id_pegawai;
        this.nama_unit = nama_unit;
        this.nama_pegawai = nama_pegawai;
        this.foto = foto;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId_pegawai() {
        return id_pegawai;
    }

    public void setId_pegawai(String id_pegawai) {
        this.id_pegawai = id_pegawai;
    }

    public String getNama_unit() {
        return nama_unit;
    }

    public void setNama_unit(String nama_unit) {
        this.nama_unit = nama_unit;
    }

    public String getNama_pegawai() {
        return nama_pegawai;
    }

    public void setNama_pegawai(String nama_pegawai) {
        this.nama_pegawai = nama_pegawai;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}