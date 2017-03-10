package com.locationApi.db;

import com.locationApi.StaticVars;
import com.locationApi.api.Location;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import com.gpsutils.Distance;

import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;
import com.locationApi.LightsManager;

import static com.locationApi.StaticVars.*;

public class LocationDAO extends AbstractDAO<Location> {

    public LocationDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    private final static Logger LOGGER = Logger.getLogger(LocationDAO.class.getName());

    StaticVars vars = new StaticVars();

    public List<Location> findAll() {
        return list(namedQuery("com.locationApi.findAll"));
    }

    public List<Location> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("com.locationApi.findByName")
                        .setParameter("name", builder.toString())
        );
    }

    public List<Location> findLastPos(String name) {
        return list(namedQuery("com.locationApi.findLastPos")
                .setParameter("name", name));
    }

    public Location create(Location location) {
        locationCheck(location);
        return persist(location);
    }

    public void locationCheck(Location location) {
        //Init variables
        Map<String, Integer> locsCounter = new HashMap<String, Integer>();
        List<Location> lastLocations = list(namedQuery("com.locationApi.findAll"));

        //Get Unique Names in DB
        List<String> names = new ArrayList();

        for (int i=0; i < lastLocations.size(); i++) {
            if (!names.contains(lastLocations.get(i).getName())) {
                names.add(lastLocations.get(i).getName());
            }
        }

        //Get last 5 locations for each name and add to counter
        for (int i=0; i < names.size(); i++) {
            List<Location> lastLocs = list(namedQuery("com.locationApi.findByName").setParameter("name", names.get(i)));
            int cnt = 0;
            for (int j=0; j < 5; j++) {
                LOGGER.info("Name: " + names.get(i) + " / Distance: " + Distance.getDistance(lastLocs.get(j).getLatitude(), lastLocs.get(i).getLongitude(), homeLat, homeLong) + " / Long:" + lastLocs.get(i).getLongitude().toString() + " / Lat: " + lastLocs.get(j).getLatitude().toString());
                if (j == 4 && location.getName() == names.get(i)) {
                    if (Distance.getDistance(location.getLatitude(), location.getLongitude(), homeLat, homeLong) > homeRadius) {
                        cnt++;
                    }
                } else {
                    if (Distance.getDistance(lastLocs.get(j).getLatitude(), lastLocs.get(i).getLongitude(), homeLat, homeLong) > homeRadius) {
                        cnt++;
                    }
                }
            }
            //set homestatus per name
            if (cnt > 3) {
                homeStatuses.put(names.get(i), false);
            } else {
                homeStatuses.put(names.get(i), true);
            }
        }
        setHomeStatus();
    }

    public void setHomeStatus() {
        Boolean status = false;
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, Boolean> entry : homeStatuses.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            builder.append(System.lineSeparator() + key + ": " + value.toString());
            if (value) {
                status = true;
            }
        }
        LOGGER.info(status.toString());
        if (status && homeStatus == "Not Home") { //
            homeStatus = "Home";
            LightsManager.changeLights(Arrays.asList("Bedroom Main Light","Rear Living Room Lamp","Front Living Room Lamp","Kitchen Light"), true);
        } else if (!status && homeStatus =="Home") {
            homeStatus = "Not Home";
            LightsManager.changeLights(Arrays.asList("All"),false);
        }
        LOGGER.info(System.lineSeparator() + "Overall Home Status: " + homeStatus + builder.toString());
    }
}
