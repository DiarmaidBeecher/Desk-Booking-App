package com.example.beecherd_assign5;

/**
 * Model class for CurrentBookingAdapter Recycler Items
 * @author Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class CurrentBooking {
    private String deskNum;
    private long From;
    private long To;
    private String stringFrom;
    private String stringTo;

    public CurrentBooking() {
        // Empty public constructor
    }

    public CurrentBooking(String deskNum, long From, long To, String stringFrom, String stringTo) {
        this.deskNum = deskNum;
        this.From = From;
        this.To = To;
        this.stringFrom = stringFrom;
        this.stringTo = stringTo;
    }

    public String getDeskNum() { return deskNum; }

    public long getFrom() { return From; }

    public long getTo() { return To; }

    public String getStringFrom() { return stringFrom; }

    public String getStringTo() { return stringTo; }

}
