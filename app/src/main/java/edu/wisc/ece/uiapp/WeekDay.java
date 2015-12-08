package edu.wisc.ece.uiapp;

/**
 * Created by CCS on 12/8/2015.
 */

class WeekDay{
    String open;
    String close;
    String special;
    public WeekDay(String open, String close, String special){
        this.open = open;
        this.close = close;
        this.special = special;
    }
}
class WeekInformation{
    WeekDay monday;
    WeekDay tuesday;
    WeekDay wednesday;
    WeekDay thursday;
    WeekDay friday;
    WeekDay saturday;
    WeekDay sunday;
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
}