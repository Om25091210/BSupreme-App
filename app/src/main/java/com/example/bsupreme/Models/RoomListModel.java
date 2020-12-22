package com.example.bsupreme.Models;

public class RoomListModel {
    private Long beds, price;
    private String type;

    public RoomListModel(){}

    public Long getBeds() {
        return beds;
    }

    public void setBeds(Long beds) {
        this.beds = beds;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RoomListModel(Long beds, Long price, String type) {
        this.beds = beds;
        this.price = price;
        this.type = type;
    }
}
