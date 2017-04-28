package com.example.user.myplace;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jbt on 12/11/2016.
 */
public class Place implements Parcelable{

    private long id;
    private String name, address;
    private double lat, lng;
    private Bitmap pic;

    public Place(long id, String name, String address, double lat, double lng, Bitmap pic) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.pic = pic;

    }

    public Place(String name, String address, double lat, double lng, Bitmap pic) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.pic = pic;
    }

    protected Place(Parcel in) {
        id = in.readLong();
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        pic = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeParcelable(pic, i);
    }

    @Override
    public String toString() {
        return "Place " +
                "name is: " + name + '\'' +
                ", address: " + address + '\'' +
                ", picture: " + pic ;
    }
}
