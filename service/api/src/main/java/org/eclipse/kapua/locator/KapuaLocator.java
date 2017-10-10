/*******************************************************************************
 * Copyright (c) 2011, 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.locator;

import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaLocatorExistsException;
import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface to load KapuaService instances in a given environment.<br>
 * Implementations of the KapuaServiceLocator can decide whether to return local instances or to acts as a proxy to remote instances.<br>
 * The locator is self initialized, it looks for the proper locator implementation class looking at {@link KapuaLocator#LOCATOR_CLASS_NAME_SYSTEM_PROPERTY} system property or falling back to the
 * {@link KapuaLocator#LOCATOR_CLASS_NAME_ENVIRONMENT_PROPERTY} (if the previous property is not defined).
 * 
 * @since 1.0
 * 
 */
public abstract class KapuaLocator implements KapuaServiceLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(KapuaLocator.class);

    private static KapuaLocator chachedInstance;
    private static String locatorClassName;

    /**
     * {@link KapuaLocator} implementation classname specified via "System property" constants
     */
    public final static String LOCATOR_CLASS_NAME_SYSTEM_PROPERTY = "locator.class.impl";

    /**
     * {@link KapuaLocator} implementation classname specified via "Environment property" constants
     */
    public final static String LOCATOR_CLASS_NAME_ENVIRONMENT_PROPERTY = "LOCATOR_CLASS_IMPL";

    static {
        createInstances();
    }

    /**
     * Return the {@link KapuaLocator} instance (singleton).
     * 
     * @return
     */
    public static KapuaLocator getInstance() {
        if (chachedInstance == null) {
            chachedInstance = KapuaLocatorManager.getInstance(locatorClassName);
        }

        return chachedInstance;
    }

    // TODO do we need synchronization?
    /**
     * Creates the {@link KapuaLocator} instance,
     * 
     * @return
     */
    private static void createInstances() {

        int registeredInstances = 0;

        locatorClassName = KapuaLocator.tryGetConfiguredClassName();
        if (locatorClassName != null && !locatorClassName.trim().isEmpty()) {
            try {
                LOGGER.info("Initializing Servicelocator with the configured instance... ");
                KapuaLocatorManager.registerInstance((KapuaLocator) Class.forName(locatorClassName).newInstance());
                registeredInstances++;
                LOGGER.info("Initialize Servicelocator with the configured instance... DONE");
                return;
            } catch (KapuaLocatorExistsException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOGGER.warn("An error occurred during Servicelocator initialization", e);
            }
        }

        // proceed with the default service locator instantiation if env variable is null or some error occurred
        // during the specific service locator instantiation

        LOGGER.info("Initialize Servicelocator with the default instance... ");
        ServiceLoader<KapuaLocator> serviceLocatorLoaders = ServiceLoader.load(KapuaLocator.class);
        for (KapuaLocator locator : serviceLocatorLoaders) {
            try {
                // simply return the first
                KapuaLocatorManager.registerInstance(locator);
                locatorClassName = locator.getClass().getName();
                registeredInstances++;
                LOGGER.info("Initialize Servicelocator with the default instance... DONE");
                return;
            } catch (KapuaLocatorExistsException e) {
                LOGGER.warn("An error occurred during Servicelocator initialization", e);
            }
        }

        // none returned
        if (registeredInstances == 0) {
            throw new KapuaRuntimeException(KapuaRuntimeErrorCodes.SERVICE_LOCATOR_UNAVAILABLE);
        }
    }

    /**
     * Get the locator classname implementation looking at the {@link KapuaLocator#LOCATOR_CLASS_NAME_SYSTEM_PROPERTY} system property or falling back to the
     * {@link KapuaLocator#LOCATOR_CLASS_NAME_ENVIRONMENT_PROPERTY} environment variable.
     * 
     * @return
     */
    protected static String tryGetConfiguredClassName() {
        String locatorClass = System.getProperty(LOCATOR_CLASS_NAME_SYSTEM_PROPERTY);
        if (locatorClass != null && !locatorClass.isEmpty()) {
            return locatorClass;
        }

        locatorClass = System.getenv(LOCATOR_CLASS_NAME_ENVIRONMENT_PROPERTY);
        if (locatorClass != null && !locatorClass.isEmpty()) {
            return locatorClass;
        }

        LOGGER.debug("No service locator class resolved. Falling back to default.");
        return null;
    }
}
