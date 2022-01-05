package com.example.beecherd_assign5;

/**
 * Model class for AvailableBookingAdapter Recycler Items
 * @author Diarmaid Beecher 2021
 * @since 20 Apr 2021
 */
public class NewBooking {
    private String deskNum;

    public NewBooking() {
        // Empty public constructor
    }

    public NewBooking(String deskNum) {
        this.deskNum = deskNum;
    }

    public String getDeskNum() { return deskNum; }
}
