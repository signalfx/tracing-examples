package com.splunk.rum.demoApp.util;

import java.util.ArrayList;

public class MonthUtil {

    private static final String[][] monthArray =
            {
                    {
                            "January", "Jan"
                    },
                    {
                            "February", "Feb"
                    },
                    {
                            "March", "Mar"
                    },
                    {
                            "April", "April"
                    },
                    {
                            "May", "May"
                    },
                    {
                            "June", "June"
                    },
                    {
                            "July", "July"
                    },
                    {
                            "August", "Aug"
                    },
                    {
                            "September", "Sept"
                    },
                    {
                            "October", "Oct"
                    },
                    {
                            "November", "Nov"
                    },
                    {
                            "December", "Dec"
                    }
            };


    /**
     * @return Short month name list
     */
    public static ArrayList<String> getShortMonthNameList() {
        ArrayList<String> monthList = new ArrayList<>();
        for (String[] strings : monthArray) {
            monthList.add(strings[1]);
        }
        return monthList;
    }
}