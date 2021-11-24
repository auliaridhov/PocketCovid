package tik.itera.covid.model.login;

import com.google.gson.annotations.SerializedName;

public class MoUser {

    @SerializedName("status")
    private boolean status;
    @SerializedName("pesan")
    private String pesan;

    @SerializedName("userList")
    private MoUserList userList;

    public MoUser(boolean status, String pesan, MoUserList userList) {
        this.status = status;
        this.pesan = pesan;
        this.userList = userList;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public MoUserList getUserList() {
        return userList;
    }

    public void setUserList(MoUserList userList) {
        this.userList = userList;
    }
}
