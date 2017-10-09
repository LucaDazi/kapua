/*******************************************************************************
 * Copyright (c) 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.app.console.server.util;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.eclipse.kapua.commons.jpa.JdbcConnectionUrlResolvers.resolveJdbcUrl;
import static org.eclipse.kapua.commons.setting.system.SystemSettingKey.DB_JDBC_DRIVER;
import static org.eclipse.kapua.commons.setting.system.SystemSettingKey.DB_PASSWORD;
import static org.eclipse.kapua.commons.setting.system.SystemSettingKey.DB_SCHEMA;
import static org.eclipse.kapua.commons.setting.system.SystemSettingKey.DB_SCHEMA_ENV;
import static org.eclipse.kapua.commons.setting.system.SystemSettingKey.DB_SCHEMA_UPDATE;
import static org.eclipse.kapua.commons.setting.system.SystemSettingKey.DB_USERNAME;

import java.util.Optional;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.ConsoleJAXBContextProvider;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.vertx.VertxLocator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.module.AccountServiceVerticle;
import org.eclipse.kapua.service.liquibase.KapuaLiquibaseClient;
import org.eclipse.kapua.service.scheduler.quartz.SchedulerServiceInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class ConsoleListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleListener.class);
    private Vertx vertx;

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        LOGGER.info("Initialize Console JABContext Provider");
        JAXBContextProvider consoleProvider = new ConsoleJAXBContextProvider();
        XmlUtil.setContextProvider(consoleProvider);

        SystemSetting config = SystemSetting.getInstance();
        if (config.getBoolean(DB_SCHEMA_UPDATE, false)) {
            LOGGER.info("Initialize Liquibase embedded client.");
            String dbUsername = config.getString(DB_USERNAME);
            String dbPassword = config.getString(DB_PASSWORD);
            String schema = firstNonNull(config.getString(DB_SCHEMA_ENV), config.getString(DB_SCHEMA));

            // initialize driver
            try {
                Class.forName(config.getString(DB_JDBC_DRIVER));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find jdbc driver: {}", config.getString(DB_JDBC_DRIVER));
            }

            LOGGER.debug("Starting Liquibase embedded client update - URL: {}, user/pass: {}/{}", new Object[] { resolveJdbcUrl(), dbUsername, dbPassword });
            new KapuaLiquibaseClient(resolveJdbcUrl(), dbUsername, dbPassword, Optional.of(schema)).update();
        }

        // start quarz scheduler
        LOGGER.info("Starting job scheduler...");
        try {
            SchedulerServiceInit.initialize();
        } catch (KapuaException e) {
            LOGGER.error("Cannot start scheduler service: {}", e.getMessage(), e);
        }
        LOGGER.info("Starting job scheduler... DONE");

        // start vertx container
        LOGGER.info("Starting vertex...");
        System.setProperty("vertx.disableFileCPResolving", "true");
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

        VertxOptions vertxOpt = new VertxOptions();
        vertx = Vertx.vertx(vertxOpt);
        vertx.deployVerticle(new AccountServiceVerticle(), ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Verticle {} successfully deployed", AccountServiceVerticle.class.getName());
            } else {
                LOGGER.warn("Something went wrong deployng vericle {}", AccountServiceVerticle.class.getName(), ar.cause());
            }
        });

        LOGGER.info("Starting vertex...DONE");

        VertxLocator vertxLocator = VertxLocator.newInstace(vertx);
        
        LOGGER.info(">>>>>>> accountServiceRequest....");
        AccountService accountService = vertxLocator.getService(AccountService.class);
        LOGGER.info(">>>>>>> accountService: " + accountService);
        LOGGER.info(">>>>>>> accountFactoryRequest....");
        AccountFactory accountFactory = vertxLocator.getFactory(AccountFactory.class);
        LOGGER.info(">>>>>>> accountFactory: " + accountFactory);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {

        LOGGER.info("Stopping vertex...");
        vertx.close();
        LOGGER.info("Stopping vertex...DONE");

        // stop quarz scheduler
        LOGGER.info("Stopping job scheduler...");
        SchedulerServiceInit.close();
        LOGGER.info("Stopping job scheduler... DONE");
    }

}
