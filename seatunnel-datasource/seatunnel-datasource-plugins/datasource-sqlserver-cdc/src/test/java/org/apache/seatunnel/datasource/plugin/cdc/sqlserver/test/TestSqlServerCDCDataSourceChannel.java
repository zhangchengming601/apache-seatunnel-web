/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.datasource.plugin.cdc.sqlserver.test;

import org.apache.seatunnel.datasource.plugin.api.model.TableField;
import org.apache.seatunnel.datasource.plugin.cdc.sqlserver.SqlServerCDCDataSourceChannel;
import org.apache.seatunnel.datasource.plugin.cdc.sqlserver.SqlServerCDCDataSourceConfig;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestSqlServerCDCDataSourceChannel {

    @Test
    @Disabled
    public void testConnect() {
        SqlServerCDCDataSourceChannel channel = new SqlServerCDCDataSourceChannel();
        Map<String, String> requestParams = new TreeMap<>();
        requestParams.put("base-url", "jdbc:sqlserver://localhost:1433;databaseName=test");
        requestParams.put("username", "sa");
        requestParams.put("password", "MyPass@word");

        for (String database :
                channel.getDatabases(SqlServerCDCDataSourceConfig.PLUGIN_NAME, requestParams)) {
            final List<String> tables =
                    channel.getTables(
                            SqlServerCDCDataSourceConfig.PLUGIN_NAME, requestParams, database);
            final Map<String, List<TableField>> tableFields =
                    channel.getTableFields(
                            SqlServerCDCDataSourceConfig.PLUGIN_NAME,
                            requestParams,
                            database,
                            tables);
        }
    }
}
