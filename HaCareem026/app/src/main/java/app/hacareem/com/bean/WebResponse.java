package app.hacareem.com.bean;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Numair Qadir on 4/29/2017.
 */

public class WebResponse {

    @SerializedName("message")
    private String message = "";
    @SerializedName("error")
    private boolean error = false;
    @SerializedName("places")
    private List<Places> places;
    @SerializedName("calender")
    private List<Calendar> calender;
    @SerializedName("facebook")
    private List<Facebook> facebook;

    public WebResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Places> getPlaces() {
        return places;
    }

    public void setPlaces(List<Places> places) {
        this.places = places;
    }

    public List<Calendar> getCalender() {
        return calender;
    }

    public void setCalender(List<Calendar> calender) {
        this.calender = calender;
    }

    public List<Facebook> getFacebook() {
        return facebook;
    }

    public void setFacebook(List<Facebook> facebook) {
        this.facebook = facebook;
    }

    @Override
    public String toString() {
        return "WebResponse{" +
                "message='" + message + '\'' +
                ", error=" + error +
                ", places=" + places +
                ", calender=" + calender +
                ", facebook=" + facebook +
                '}';
    }
}