package com.locationApi.resources;

import com.gpsutils.Distance;
import com.locationApi.api.Location;
import com.locationApi.db.LocationDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {

    private final static Logger LOGGER = Logger.getLogger(LocationResource.class.getName());

    private LocationDAO locationDAO;

    public LocationResource(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
    }

    @GET
    @UnitOfWork
    public List<Location> findByName(@QueryParam("name") Optional<String> name) {
        if (name.isPresent()) {
            return locationDAO.findByName(name.get());
        } else {
            return locationDAO.findAll();
        }

    }

    @POST
    @UnitOfWork
    public Location createLocation(Location location) {
        LOGGER.info(location.getLog());
        return locationDAO.create(location);
    }

}