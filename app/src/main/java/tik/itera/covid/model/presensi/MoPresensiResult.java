package tik.itera.covid.model.presensi;

import com.google.gson.annotations.SerializedName;

public class MoPresensiResult {

    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private boolean status;

    public MoPresensiResult(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}