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

package engine.monica.core.world;

import engine.monica.core.element.ElementEngine;
import engine.monica.core.plugin.PluginManager;
import java.math.BigInteger;

public final class World {

    public World(ConfigInterface c) {
        if (c == null)
            throw new NullPointerException("The World Configure is null.");
        configs = c;
        pluginMgr = new PluginManager(this);
    }

    public ConfigInterface getConfigures() {
        return configs;
    }
    private final ConfigInterface configs;

    public static final String CFG_ALLROLES_KEY = "CFG-Role-All";

    public PluginManager getPluginManager() {
        if (isStart())
            throw new WorldException("The world has already started.");
        return pluginMgr;
    }
    private final PluginManager pluginMgr;

    public ElementEngine getElementEngine() {
        return elementEngine;
    }

    public void setElementEngine(ElementEngine e) {
        if (isStart())
            throw new WorldException("The world has already started.");
        if (e == null)
            throw new NullPointerException("The ElementEngine is null.");
        elementEngine = e;
    }
    private ElementEngine elementEngine = new ElementEngine();

    public boolean isStart() {
        return isStart;
    }

    public void start() {
        if (isStart)
            throw new WorldException("The world has already started.");
        if (date == null)
            throw new WorldException("Cannot start the world until WorldDate sets up.");
        date.ready();
        isStart = true;
        isContinuing = true;
        date.start();
    }

    public boolean isContinuing() {
        return isContinuing;
    }

    public void stop() {
        if (!isStart)
            throw new WorldException("The world has not started yet.");
        isStart = false;
        isContinuing = false;
        date.stop();
    }

    public void setContinue() {
        if (!isStart())
            throw new WorldException("The world has not started yet.");
        if (isContinuing)
            throw new WorldException("The world has already started.");
        date.ready();
        isContinuing = true;
        date.start();
    }

    public void setPause() {
        if (!isStart())
            throw new WorldException("The world has not started yet.");
        if (!isContinuing)
            throw new WorldException("The world has already set to continue.");
        isContinuing = false;
        date.stop();
    }
    private transient boolean isStart = false;
    private transient boolean isContinuing = false;

    public void setWorldDate(DateTime dateTime) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date = new WorldDate(dateTime);
    }

    public int getCurrentYear() {
        return date.getYear();
    }

    public int getCurrentMonth() {
        return date.getMonth();
    }

    public int getCurrentDay() {
        return date.getDay();
    }

    public int getCurrentHour() {
        return date.getHour();
    }

    public int getCurrentMinute() {
        return date.getMinute();
    }

    public int getCurrentSecond() {
        return date.getSecond();
    }

    public int getCurrentMilliSecond() {
        return date.getMilliSecond();
    }

    public DateTime getCurrentDateTime() {
        return date.getCurrentDateTime();
    }

    public void setLoopMonth(int mon) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date.setLoopMonth(mon);
    }

    public void setLoopDay(int day) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date.setLoopDay(day);
    }

    public void setLoopHour(int hour) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date.setLoopHour(hour);
    }

    public void setLoopMinute(int min) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date.setLoopMinute(min);
    }

    public void setLoopSecond(int sec) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date.setLoopSecond(sec);
    }

    public void setLoopMilliSecond(int msec) {
        if (isStart())
            throw new WorldException("The world has already started.");
        date.setLoopMilliSecond(msec);
    }

    public BigInteger dateTimeToInteger() {
        if (!isStart())
            throw new WorldException("The world has not started yet.");
        return date.getCurrentDateTime().toInteger(date);
    }
    private volatile WorldDate date;
}
