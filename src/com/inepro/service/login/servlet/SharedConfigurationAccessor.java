package com.inepro.service.login.servlet;

import java.util.HashMap;
import java.util.Map;

import com.canon.meap.service.conf.Configuration;
import com.canon.meap.service.conf.ConfigurationListener;
import com.canon.meap.service.conf.ConfigurationService;
import com.canon.meap.service.conf.OutOfSpaceException;

/**
 * Accessor for shared configuration
 */
public class SharedConfigurationAccessor {

    private ConfigurationService _confService;

    /**
     * Constructor
     * @param service
     */
    public SharedConfigurationAccessor(ConfigurationService service) {
        _confService = service;
    }

    /**
     * Shared configuration acquisition
     * @param key
     * @return
     */
    public Object getConfigurationValue(String key) {
        Configuration targetConf = _confService.getSharedConfiguration(key);

        Map properties = targetConf.getProperties();

        if (properties != null) {
            return properties.get(key);
        }

        return null;
    }

    /**
     * Shared configuration setting
     * @param key
     * @param value
     */
    public void setConfigurationValue(String key, Object value) {
        Configuration targetConf = _confService.getSharedConfiguration(key);

        Map properties = targetConf.getProperties();
        if (properties == null) {
            properties = new HashMap();
        }

        properties.put(key, value);

        try {
            targetConf.setProperties(properties);
        } catch (OutOfSpaceException oe) {
            // Ignore processing size exceptions
            oe.printStackTrace();
        }
    }

    /**
     * Listener registration to shared configuration
     * @param listener
     * @param target
     */
    public void addConfigurationListener(ConfigurationListener listener, String[] target) {
        _confService.addSharedConfigurationListener(listener, target);
    }

    /**
     * Listener deletion from shared configuration
     * @param listener
     * @param target
     */
    public void removeConfigurationListener(ConfigurationListener listener) {
        _confService.removeSharedConfigurationListener(listener);
    }

    /**
     * Destroy Accessor.
     */
    public void destroy() {
        _confService = null;
    }

}
