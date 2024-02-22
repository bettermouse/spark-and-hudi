/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cdc;


import io.debezium.config.Configuration;
import io.debezium.connector.mysql.MySqlConnectorConfig;
import io.debezium.relational.RelationalTableFilters;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;


/** A MySql Source configuration which is used by {@link }. */
public class MySqlSourceConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String hostname;
    private final int port;

    private final String username;
    private final String password;
    private final List<String> databaseList;
    private final List<String> tableList;

    // --------------------------------------------------------------------------------------------
    // Debezium Configurations
    // --------------------------------------------------------------------------------------------
    private final Properties dbzProperties;
    private final Configuration dbzConfiguration;
    private final MySqlConnectorConfig dbzMySqlConfig;


    public MySqlSourceConfig(String hostname, int port, String username, String password, List<String> databaseList, List<String> tableList, Properties dbzProperties, Configuration dbzConfiguration, MySqlConnectorConfig dbzMySqlConfig) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseList = databaseList;
        this.tableList = tableList;
        this.dbzProperties = dbzProperties;
        this.dbzConfiguration = dbzConfiguration;
        this.dbzMySqlConfig = dbzMySqlConfig;
    }

    public MySqlSourceConfig(String hostname, int port, String username, String password, List<String> databaseList, List<String> tableList, Properties dbzProperties) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseList = databaseList;
        this.tableList = tableList;
        this.dbzProperties = checkNotNull(dbzProperties);
        this.dbzConfiguration = Configuration.from(dbzProperties);
        this.dbzMySqlConfig = new MySqlConnectorConfig(dbzConfiguration);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getDatabaseList() {
        return databaseList;
    }

    public List<String> getTableList() {
        return tableList;
    }

    public Properties getDbzProperties() {
        return dbzProperties;
    }

    public Configuration getDbzConfiguration() {
        return dbzConfiguration;
    }

    public MySqlConnectorConfig getDbzMySqlConfig() {
        return dbzMySqlConfig;
    }

    public MySqlConnectorConfig getMySqlConnectorConfig() {
        return dbzMySqlConfig;
    }

    public RelationalTableFilters getTableFilters() {
        return dbzMySqlConfig.getTableFilters();
    }

}
