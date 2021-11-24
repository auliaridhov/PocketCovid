package tik.itera.covid.model.presensi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FakeGpsList {

    @SerializedName("list_fake")
    private ArrayList<FakeGps> fakeGpsList;

    public ArrayList<FakeGps> getFakeGpsList() {
        return fakeGpsList;
    }

    public void setFakeGpsList(ArrayList<FakeGps> fakeGpsList) {
        this.fakeGpsList = fakeGpsList;
    }
}
