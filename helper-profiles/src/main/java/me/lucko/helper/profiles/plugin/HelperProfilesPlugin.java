/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.profiles.plugin;

import me.lucko.helper.internal.HelperImplementationPlugin;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.profiles.KProfileRepository;
import me.lucko.helper.profiles.ProfileRepository;
import me.lucko.helper.sql.DatabaseCredentials;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.sql.SqlProvider;

import org.bukkit.configuration.file.YamlConfiguration;

@HelperImplementationPlugin
public class HelperProfilesPlugin extends ExtendedJavaPlugin {

    @Override
    protected void enable() {
        SqlProvider sqlProvider = getService(SqlProvider.class);
        Sql sql;

        // load sql instance
        YamlConfiguration config = loadConfig("config.yml");
        if (config.getBoolean("use-global-credentials", true)) {
            sql = sqlProvider.getSql();
        } else {
            sql = sqlProvider.getSql(DatabaseCredentials.fromConfig(config));
        }

        // init the table
        String tableName = config.getString("table-name", "helper_profiles");
        int preloadAmount = config.getInt("preload-amount", 2000);

        // provide the ProfileRepository service
        HelperProfileInternal internal = new HelperProfileInternal(sql, tableName, preloadAmount);
        provideService(ProfileRepository.class, bindModule(new HelperProfileRepository(internal)));
        provideService(KProfileRepository.class, new KHelperProfileRepository(internal));
    }

}
