package com.example.bsupreme.Models.DropdownItems;

public class PriceItem {
    private String roomPrice;

    public String getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(String roomPrice) {
        this.roomPrice = roomPrice;
    }

    public PriceItem(){}

    public PriceItem(String roomPrice) {
        this.roomPrice = roomPrice;
    }
}
