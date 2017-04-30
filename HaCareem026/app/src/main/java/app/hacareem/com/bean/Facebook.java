package app.hacareem.com.bean;

/**
 * Created by Numair Qadir on 4/29/2017.
 */

public class Facebook {
    public int id = 0;
    public String dest = "";
    public double latitude = 0;
    public double longitude = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Facebook{" +
                "id=" + id +
                ", dest='" + dest + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
