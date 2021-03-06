/**
 * Copyright (C) 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.flyway.core.info;

import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.util.DateUtils;
import com.googlecode.flyway.core.util.StringUtils;

/**
 * Dumps migrations in an ascii-art table in the logs and the console.
 */
public class MigrationInfoDumper {
    /**
     * Prevent instantiation.
     */
    private MigrationInfoDumper() {
        // Do nothing
    }

    /**
     * Dumps the info about all migrations into an ascii table.
     *
     * @param migrationInfos The list of migrationInfos to dump.
     * @return The ascii table, as one big multi-line string.
     */
    public static String dumpToAsciiTable(MigrationInfo[] migrationInfos) {
        StringBuilder table = new StringBuilder();

        table.append("+----------------+----------------------------+---------------------+---------+\n");
        table.append("| Version        | Description                | Installed on        | State   |\n");
        table.append("+----------------+----------------------------+---------------------+---------+\n");

        if (migrationInfos.length == 0) {
            table.append("| No migrations found                                                         |\n");
        } else {
            for (MigrationInfo migrationInfo : migrationInfos) {
                table.append("| ").append(StringUtils.trimOrPad(migrationInfo.getVersion().toString(), 14));
                table.append(" | ").append(StringUtils.trimOrPad(migrationInfo.getDescription(), 26));
                table.append(" | ").append(StringUtils.trimOrPad(DateUtils.formatDateAsIsoString(migrationInfo.getInstalledOn()), 19));
                table.append(" | ").append(StringUtils.trimOrPad(migrationInfo.getState().getDisplayName(), 7));
                table.append(" |\n");
            }
        }

        table.append("+----------------+----------------------------+---------------------+---------+");
        return table.toString();
    }
}
