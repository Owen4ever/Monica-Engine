/*
 * Copyright (C) 2014 Owen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package engine.monica.core.plugin;

import engine.monica.core.world.World;
import engine.monica.util.result.BoolMsgResult;
import engine.monica.util.result.IntMsgResult;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginManager {

    public PluginManager(World world) {
        if (world == null)
            throw new NullPointerException("The world is null.");
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public IntMsgResult addPlugin(Plugin plugin) {
        if (plugin == null)
            return new IntMsgResult(RET_ERR_NULL, "The Plugin is null.");
        String name = "Plugin(" + plugin.getPluginName() + ")";
        if (plugins.containsKey(plugin.getPluginName()))
            return new IntMsgResult(RET_ERR_LOAD_LOADED, name + " has already loaded.");
        String[] needs = plugin.needSupportPlugins();
        ArrayList<String> dontContain = new ArrayList<>(needs.length);
        for (String needPluginName : needs)
            if (!plugins.containsKey(needPluginName))
                dontContain.add(needPluginName);
        if (!dontContain.isEmpty()) {
            StringJoiner strJoiner = new StringJoiner(", ");
            dontContain.forEach(n -> {
                strJoiner.add(n);
            });
            return new IntMsgResult(RET_ERR_LOAD_NEEDPLUGIN,
                    name + " needs to load follow Plugins first: (" + strJoiner + ").");
        }
        BoolMsgResult ret = plugin.load(this);
        if (ret == null)
            return new IntMsgResult(RET_ERR_LOAD_FAILED, "Failed while loading " + name + ".");
        if (ret.success) {
            plugins.put(plugin.getPluginName(), plugin);
            return new IntMsgResult(RET_SUCCEED,
                    ret.message == null || ret.message.isEmpty() ? "Succeeded in loading " + name + "." : ret.message);
        } else
            return new IntMsgResult(RET_ERR_LOAD_FAILED,
                    ret.message == null || ret.message.isEmpty() ? "Failed while loading " + name + "." : ret.message);
    }

    public IntMsgResult removePlugin(String name) {
        return removePlugin(name, false);
    }

    public IntMsgResult removePlugin(String name, boolean force) {
        if (name == null || name.isEmpty())
            return new IntMsgResult(RET_ERR_NULL, "The Plugin name is null.");
        Plugin p = plugins.get(name);
        if (p != null)
            return removePlugin(p, force);
        else
            return new IntMsgResult(RET_ERR_REMOVE_REMOVED, name + " did not load or has already removed.");
    }

    public IntMsgResult removePlugin(Plugin plugin) {
        return removePlugin(plugin, false);
    }

    public IntMsgResult removePlugin(Plugin plugin, boolean force) {
        if (plugin == null)
            return new IntMsgResult(RET_ERR_NULL, "The Plugin is null.");
        String name = "Plugin(" + plugin.getPluginName() + ")";
        if (!plugins.containsKey(plugin.getPluginName()))
            return new IntMsgResult(RET_ERR_REMOVE_REMOVED, name + " did not load or has already removed.");
        if (!force) {
            ArrayList<String> needRemoveFirst = new ArrayList<>(plugins.size() - 1);
            plugins.forEach((n, p) -> {
                if (!n.equals(plugin.getPluginName()))
                    for (String needPluginName : p.needSupportPlugins())
                        if (needPluginName.equals(plugin.getPluginName())) {
                            needRemoveFirst.add(n);
                            break;
                        }
            });
            if (!needRemoveFirst.isEmpty()) {
                StringJoiner strJoiner = new StringJoiner(", ");
                needRemoveFirst.forEach(n -> {
                    strJoiner.add(n);
                });
                return new IntMsgResult(RET_ERR_REMOVE_OTHERFIRST,
                        name + " needs to remove follow Plugins first: (" + strJoiner + ").");
            }
        }
        BoolMsgResult ret = plugin.remove(this);
        if (ret == null)
            return new IntMsgResult(RET_ERR_REMOVE_FAILED, "Failed while removing " + name + ".");
        if (ret.success && plugins.remove(plugin.getPluginName(), plugin)) {
            return new IntMsgResult(RET_SUCCEED,
                    ret.message == null || ret.message.isEmpty() ? "Succeeded in removing " + name + "." : ret.message);
        } else
            return new IntMsgResult(RET_ERR_REMOVE_FAILED,
                    ret.message == null || ret.message.isEmpty() ? "Failed while removing " + name + "." : ret.message);
    }

    public BoolMsgResult canLoad(Plugin plugin) {
        if (plugin == null)
            return new BoolMsgResult(false, "The Plugin is null.");
        String name = "Plugin(" + plugin.getPluginName() + ")";
        String[] needs = plugin.needSupportPlugins();
        ArrayList<String> dontContain = new ArrayList<>(needs.length);
        for (String needPluginName : needs)
            if (!plugins.containsKey(needPluginName))
                dontContain.add(needPluginName);
        if (!dontContain.isEmpty()) {
            StringJoiner strJoiner = new StringJoiner(", ");
            dontContain.forEach(n -> {
                strJoiner.add(n);
            });
            return new BoolMsgResult(false,
                    name + " needs to load follow Plugins first: (" + strJoiner + ").");
        } else
            return new BoolMsgResult(true, "");
    }

    public BoolMsgResult canRemove(Plugin plugin) {
        if (plugin == null)
            return new BoolMsgResult(false, "The Plugin is null.");
        String name = "Plugin(" + plugin.getPluginName() + ")";
        if (!plugins.containsKey(plugin.getPluginName()))
            return new BoolMsgResult(false, name + " did not load or has already removed.");
        ArrayList<String> needRemoveFirst = new ArrayList<>(plugins.size() - 1);
        plugins.forEach((n, p) -> {
            if (!n.equals(plugin.getPluginName()))
                for (String needPluginName : p.needSupportPlugins())
                    if (needPluginName.equals(plugin.getPluginName())) {
                        needRemoveFirst.add(n);
                        break;
                    }
        });
        if (!needRemoveFirst.isEmpty()) {
            StringJoiner strJoiner = new StringJoiner(", ");
            needRemoveFirst.forEach(n -> {
                strJoiner.add(n);
            });
            return new BoolMsgResult(false,
                    name + " needs to remove follow Plugins first: (" + strJoiner + ").");
        } else
            return new BoolMsgResult(true, "");
    }

    public BoolMsgResult removeAll() {
        ArrayList<String> names = new ArrayList<>(plugins.size());
        plugins.values().parallelStream().forEach(p -> {
            if (removePlugin(p, true).success == RET_ERR_REMOVE_FAILED)
                names.add(p.getPluginName());
        });
        plugins.clear();
        if (names.size() > 1) {
            StringJoiner strJoiner = new StringJoiner(", ");
            names.forEach(n -> {
                strJoiner.add(n);
            });
            return new BoolMsgResult(false, "Failed to remove follow Plugins: (" + strJoiner + ").");
        } else if (names.size() == 1)
            return new BoolMsgResult(false, "Failed to remove follow Plugin: (" + names.iterator().next() + ").");
        else
            return new BoolMsgResult(true, "Succeeded in removing all plugins.");
    }

    public Set<String> getLoadedPluginNames() {
        return plugins.keySet();
    }
    private final World world;
    private final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();

    public static final int RET_SUCCEED = 0;
    public static final int RET_ERR_NULL = 1;
    public static final int RET_ERR_LOAD_LOADED = 11;
    public static final int RET_ERR_LOAD_NEEDPLUGIN = 12;
    public static final int RET_ERR_LOAD_FAILED = 13;
    public static final int RET_ERR_REMOVE_REMOVED = 21;
    public static final int RET_ERR_REMOVE_OTHERFIRST = 22;
    public static final int RET_ERR_REMOVE_FAILED = 23;
}
