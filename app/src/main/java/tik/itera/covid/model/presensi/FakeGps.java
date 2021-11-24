package tik.itera.covid.model.presensi;

import com.google.gson.annotations.SerializedName;

public class FakeGps {

    @SerializedName("list_fake")
    private String list_fake;

    public FakeGps(String list_fake) {
        this.list_fake = list_fake;
    }

    public String getList_fake() {
        return list_fake;
    }

    public void setList_fake(String list_fake) {
        this.list_fake = list_fake;
    }
}
