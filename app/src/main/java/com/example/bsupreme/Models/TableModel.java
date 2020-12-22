package com.example.bsupreme.Models;

public class TableModel {
    private boolean is_counter;
    private boolean is_ac;
    private boolean is_grill;

    public long getSeating() {
        return seating;
    }

    public void setSeating(long seating) {
        this.seating = seating;
    }

    private long seating;

    public TableModel(){}

    public boolean isIs_counter() {
        return is_counter;
    }

    public void setIs_counter(boolean is_counter) {
        this.is_counter = is_counter;
    }

    public boolean isIs_ac() {
        return is_ac;
    }

    public void setIs_ac(boolean is_ac) {
        this.is_ac = is_ac;
    }

    public boolean isIs_grill() {
        return is_grill;
    }

    public void setIs_grill(boolean is_grill) {
        this.is_grill = is_grill;
    }

    public TableModel(boolean is_counter, boolean is_ac, boolean is_grill, long seating) {
        this.is_counter = is_counter;
        this.is_ac = is_ac;
        this.is_grill = is_grill;
        this.seating = seating;
    }
}
