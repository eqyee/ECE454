package edu.wisc.ece.uiapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CCS on 12/8/2015.
 */

class WeekDay implements Parcelable{
    public String open;
    public String close;
    public String special;
    public WeekDay(String open, String close, String special){
        this.open = open;
        this.close = close;
        this.special = special;
    }

    protected WeekDay(Parcel in) {
        open = in.readString();
        close = in.readString();
        special = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(open);
        dest.writeString(close);
        dest.writeString(special);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WeekDay> CREATOR = new Parcelable.Creator<WeekDay>() {
        @Override
        public WeekDay createFromParcel(Parcel in) {
            return new WeekDay(in);
        }

        @Override
        public WeekDay[] newArray(int size) {
            return new WeekDay[size];
        }
    };
}
class WeekInformation implements Parcelable{
    public WeekDay monday;
    public WeekDay tuesday;
    public WeekDay wednesday;
    public WeekDay thursday;
    public WeekDay friday;
    public WeekDay saturday;
    public WeekDay sunday;
    public WeekInformation(WeekDay sunday, WeekDay monday, WeekDay tuesday, WeekDay wednesday,
                           WeekDay thursday, WeekDay friday, WeekDay saturday){
            this.sunday = sunday;
            this.monday = monday;
            this.tuesday = tuesday;
            this.wednesday = wednesday;
            this.thursday = thursday;
            this.friday = friday;
            this.saturday = saturday;
    }

    protected WeekInformation(Parcel in) {
        monday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
        tuesday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
        wednesday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
        thursday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
        friday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
        saturday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
        sunday = (WeekDay) in.readValue(WeekDay.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(monday);
        dest.writeValue(tuesday);
        dest.writeValue(wednesday);
        dest.writeValue(thursday);
        dest.writeValue(friday);
        dest.writeValue(saturday);
        dest.writeValue(sunday);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WeekInformation> CREATOR = new Parcelable.Creator<WeekInformation>() {
        @Override
        public WeekInformation createFromParcel(Parcel in) {
            return new WeekInformation(in);
        }

        @Override
        public WeekInformation[] newArray(int size) {
            return new WeekInformation[size];
        }
    };
}