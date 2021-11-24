package tik.itera.covid.model.presensi;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PresensiList {
    @SerializedName("list_presensi")
    private ArrayList<Presensi> listPresensi;

    @SerializedName("status")
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<Presensi> getListPresensi() {
        return listPresensi;
    }

    public void setListPresensi(ArrayList<Presensi> listPresensi) {
        this.listPresensi = listPresensi;
    }

}
