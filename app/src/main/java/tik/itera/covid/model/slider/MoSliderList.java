package tik.itera.covid.model.slider;

import com.google.gson.annotations.SerializedName;

public class MoSliderList {
    @SerializedName("status")
    private boolean status;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("description")
    private String description;

    public MoSliderList(boolean status, String title, String image, String description) {
        this.status = status;
        this.title = title;
        this.image = image;
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
