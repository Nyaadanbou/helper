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

package me.lucko.helper.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;

/**
 * Utility for interacting with the server's {@link CommandMap} instance.
 */
public final class CommandMapUtil {

    private static final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;

    static {
        Constructor<PluginCommand> commandConstructor;
        try {
            commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        COMMAND_CONSTRUCTOR = commandConstructor;
    }

    private static CommandMap getCommandMap() {
        return Bukkit.getCommandMap(); // Compatible with Paper 1.9.1+
    }

    private static Map<String, Command> getKnownCommandMap() {
        return Bukkit.getCommandMap().getKnownCommands(); // Compatible with Paper 1.9.1+
    }

    /**
     * Registers a CommandExecutor with the server
     *
     * @param plugin  the plugin instance
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T>     the command executor class type
     *
     * @return the command executor
     */
    @Nonnull
    public static <T extends CommandExecutor> T registerCommand(@Nonnull Plugin plugin, @Nonnull T command, @Nonnull String... aliases) {
        return registerCommand(plugin, command, null, null, null, aliases);
    }

    /**
     * Registers a CommandExecutor with the server
     *
     * @param plugin            the plugin instance
     * @param command           the command instance
     * @param permission        the command permission
     * @param permissionMessage the message sent when the sender doesn't the required permission
     * @param description       the command description
     * @param aliases           the command aliases
     * @param <T>               the command executor class type
     *
     * @return the command executor
     */
    @Nonnull
    public static <T extends CommandExecutor> T registerCommand(
            @Nonnull Plugin plugin,
            @Nonnull T command,
            @Nullable String permission,
            @Nullable String permissionMessage,
            @Nullable String description,
            @Nonnull String[] aliases
    ) {
        Preconditions.checkArgument(aliases.length != 0, "No aliases");
        for (String alias : aliases) {
            try {
                PluginCommand cmd = COMMAND_CONSTRUCTOR.newInstance(alias, plugin);

                getCommandMap().register(plugin.getDescription().getName(), cmd);
                getKnownCommandMap().put(plugin.getDescription().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
                getKnownCommandMap().put(alias.toLowerCase(), cmd);
                cmd.setLabel(alias.toLowerCase());
                if (permission != null) {
                    cmd.setPermission(permission);
                    if (permissionMessage != null) {
                        cmd.setPermissionMessage(permissionMessage);
                    }
                }
                if (description != null) {
                    cmd.setDescription(description);
                }

                cmd.setExecutor(command);
                if (command instanceof TabCompleter) {
                    cmd.setTabCompleter((TabCompleter) command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return command;
    }

    /**
     * Unregisters a CommandExecutor with the server
     *
     * @param command the command instance
     * @param <T>     the command executor class type
     *
     * @return the command executor
     */
    @Nonnull
    public static <T extends CommandExecutor> T unregisterCommand(@Nonnull T command) {
        CommandMap map = getCommandMap();
        try {
            Iterator<Command> iterator = getKnownCommandMap().values().iterator();
            while (iterator.hasNext()) {
                Command cmd = iterator.next();
                if (cmd instanceof PluginCommand) {
                    CommandExecutor executor = ((PluginCommand) cmd).getExecutor();
                    if (command == executor) {
                        cmd.unregister(map);
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not unregister command", e);
        }

        return command;
    }

    private CommandMapUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
