package tik.itera.covid.model.slider;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MoSlider {
    @SerializedName("status")
    private boolean status;
    @SerializedName("pesan")
    private String pesan;
    @SerializedName("sliderList")
    private ArrayList<MoSliderList> sliderLists;

    public MoSlider(boolean status, String pesan, ArrayList<MoSliderList> sliderLists) {
        this.status = status;
        this.pesan = pesan;
        this.sliderLists = sliderLists;
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

    public ArrayList<MoSliderList> getSliderLists() {
        return sliderLists;
    }

    public void setSliderLists(ArrayList<MoSliderList> sliderLists) {
        this.sliderLists = sliderLists;
    }
}
