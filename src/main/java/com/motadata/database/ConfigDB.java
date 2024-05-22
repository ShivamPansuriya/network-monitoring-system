package com.motadata.database;

import com.motadata.constants.Constants;
import com.motadata.utils.Utils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfigDB
{
    private ConfigDB()
    {}

    public static final Logger logger = LoggerFactory.getLogger(ConfigDB.class);

    private static HashMap<String, ConfigDB> instances = new HashMap<>();

    final ConcurrentMap<Long, JsonObject> profiles = new ConcurrentHashMap<>();

    static final ConcurrentMap<Long, JsonObject> validProfiles = new ConcurrentHashMap<>();

    public static void createDatabase(String name)
    {
        if(!(instances.containsKey(name)))
        {
            ConfigDB database = new ConfigDB();

            instances.put(name, database);

            logger.info("New database is created for {}", name);
        }
        else
        {
            logger.info("Database with name {} already exists", name);
        }
    }

    public static ConfigDB getDatabase(String name)
    {
        logger.info("Get database request has been served for database {}", name);

        return instances.get(name);

    }

    public long create(JsonObject data)
    {
        var id = Utils.generateID();

        profiles.put(id, data);

        logger.info("Data added to database and id is {}", id);

        return id;

    }

    public void create(Long id, JsonObject data)
    {
        validProfiles.put(id, data);

        logger.info("Data added to database and id is {}", id);

    }

    public JsonArray get(String name)
    {
        var result = new JsonArray();

        switch(name)
        {
            case Constants.PROFILES->
            {
                profiles.forEach((id, profile) ->

                        result.add(profile.copy().put(Constants.ID, id)));

                logger.debug("Get all the data has been called");
            }

            case Constants.VALID_PROFILES->
            {
                profiles.forEach((id, profile) ->

                        result.add(new JsonObject().put(Constants.PROVISION_ID, id).put(Constants.IP, profile.getString(Constants.IP)).put(Constants.DEVICE_TYPE, profile.getString(Constants.DEVICE_TYPE))));

                logger.debug("Get all the data has been called");

            }
        }
        return result.copy();
    }

    public JsonObject get(Long id, String name)
    {
        var result = new JsonObject();

        switch(name)
        {
            case Constants.PROFILES->
            {
                logger.debug("Get has been called for id {}", id);

                result =  profiles.getOrDefault(id, new JsonObject()).copy();
            }

            case Constants.VALID_PROFILES->
            {
                logger.debug("Get has been called for id {}", id);

                result =  validProfiles.getOrDefault(id, new JsonObject()).copy();
            }
        }
        return result;
    }

    public void update(JsonObject profile, long id, String name)
    {
        switch(name)
        {
            case Constants.PROFILES->
            {
                profiles.replace(id, profile);

                logger.debug("profile {} updated", id);
            }

            case "validProfiles"->
            {
                validProfiles.replace(id, profile);

                logger.debug("valid profile {} updated", id);
            }

        }
    }

    public void delete(long id, String name)
    {
        switch(name)
        {
            case Constants.PROFILES->
            {
                profiles.remove(id);

                logger.debug("profile {} deleted", id);
            }

            case Constants.VALID_PROFILES->
            {
                logger.debug("Get has been called for id {}", id);

                validProfiles.remove(id);
            }

        }
    }

    public boolean present(long id)
    {
        return profiles.containsKey(id);
    }

}
