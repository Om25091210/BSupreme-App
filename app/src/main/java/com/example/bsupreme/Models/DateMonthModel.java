package com.example.bsupreme.Models;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateMonthModel {
    public static List<String> monthsList = Stream.of(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    ).collect(Collectors.toList());

    public DateMonthModel(List<String> monthsList) {
        this.monthsList = monthsList;
    }

    public static String getMonth(Long index) {
        return monthsList.get(index.intValue());
    }
}
