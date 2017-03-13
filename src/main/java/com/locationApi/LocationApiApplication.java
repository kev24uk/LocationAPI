package com.locationApi;

import com.locationApi.api.Location;
import com.locationApi.db.LocationDAO;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.locationApi.resources.LocationResource;
import com.locationApi.health.TemplateHealthCheck;
import com.locationApi.LightsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LocationApiApplication extends Application<LocationApiConfiguration> {

    public static void main(String[] args) throws Exception {
        new LocationApiApplication().run(args);
        LightsManager.initMe();
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    private final HibernateBundle<LocationApiConfiguration> hibernateBundle = new HibernateBundle<LocationApiConfiguration>(Location.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(LocationApiConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(final Bootstrap<LocationApiConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final LocationApiConfiguration configuration,final Environment environment) {
        final LocationDAO locationDAO = new LocationDAO(hibernateBundle.getSessionFactory());
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(new LocationResource(locationDAO));
    }

}