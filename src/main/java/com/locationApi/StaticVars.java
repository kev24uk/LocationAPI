package com.locationApi;

import java.util.HashMap;
import java.util.Map;

public class StaticVars {

    public static Double homeLong = 50.764923;

    public static Double homeLat = -1.924081;

    public static Integer homeRadius = 500;

    public static String homeStatus = "Home";

    public static Map<String, Boolean> homeStatuses = createMap();

    public static Map<String, Boolean> createMap() {
        Map<String,Boolean> myMap = new HashMap<String,Boolean>();
        myMap.put("Kev", true);
        myMap.put("Caz", true);
        return myMap;
    }

    public void setHomeStatuses(Map<String, Boolean> newStatuses) {
        this.homeStatuses = newStatuses;
    }
}
