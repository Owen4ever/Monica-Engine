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
import engine.monica.core.engine.CoreEngine;
import engine.monica.core.object.BloodlineFactory;
import engine.monica.core.object.RaceFactory;
import engine.monica.core.plugin.PluginManager;
import engine.monica.core.world.DateTime.DateTimeConvertor;
import engine.monica.core.world.WorldDate.SleepTimeSupplier;
import engine.monica.core.world.WorldDate.TimeCalc;
import java.util.HashMap;

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

    public void addRaceFactory(RaceFactory f) {
        if (isStart())
            throw new WorldException("The world has already started.");
        if (f == null)
            throw new NullPointerException("The RaceFactory is null.");
        if (raceFactories.containsKey(f.getID()))
            throw new WorldException("The RaceFactory has already contained.");
        raceFactories.put(f.getID(), f);
    }

    public boolean removeRaceFactory(RaceFactory f) {
        return raceFactories.remove(f.getID(), f);
    }

    public String[] getRaceFactoryNames() {
        return raceFactories.values().parallelStream().map(RaceFactory::getName).toArray(String[]::new);
    }
    private final HashMap<String, RaceFactory> raceFactories = new HashMap<>(CoreEngine.getDefaultQuantity());

    public void addBloodlineFactory(BloodlineFactory f) {
        if (isStart())
            throw new WorldException("The world has already started.");
        if (f == null)
            throw new NullPointerException("The RaceFactory is null.");
        if (bloodlineFactories.containsKey(f.getID()))
            throw new WorldException("The RaceFactory has already contained.");
        bloodlineFactories.put(f.getID(), f);
    }

    public boolean removeBloodlineFactory(BloodlineFactory f) {
        return bloodlineFactories.remove(f.getID(), f);
    }

    public String[] getBloodlineFactoryNames() {
        return bloodlineFactories.values().parallelStream().map(BloodlineFactory::getName).toArray(String[]::new);
    }
    private final HashMap<String, BloodlineFactory> bloodlineFactories = new HashMap<>(CoreEngine.getDefaultQuantity());

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
        isStart = true;
        isContinuing = true;
        date.start();
    }

    public boolean isContinuing() {
        return isStart && isContinuing;
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

    public void setWorldDate(WorldDate worldDate) {
        if (isContinuing())
            throw new WorldException("The world has already started.");
        if (worldDate == null)
            throw new NullPointerException("The world date is null.");
        date = worldDate;
    }

    public void setCurrentTime(long time) {
        date.setCurrentTime(time);
    }

    public void setTimeCalculator(TimeCalc calc) {
        if (isContinuing())
            throw new WorldException("The world has already started.");
        date.setTimeCalculator(calc);
    }

    public void setSleepTimeSupplier(SleepTimeSupplier sleepTime) {
        if (isContinuing())
            throw new WorldException("The world has already started.");
        date.setSleepTimeSupplier(sleepTime);
    }

    public long getCurrentTime() {
        return date.getCurrentTime();
    }

    public DateTime getCurrentDateTime() {
        return getCurrentDateTime(convertor);
    }

    public DateTime getCurrentDateTime(DateTimeConvertor convertor) {
        if (convertor == null)
            throw new NullPointerException("The convertor is null.");
        return convertor.convert(date.getCurrentTime());
    }
    private WorldDate date;

    public void setDefaultDateTimeConvertor(DateTimeConvertor convertor) {
        if (convertor == null)
            throw new NullPointerException("The convertor is null.");
        this.convertor = convertor;
    }

    public DateTimeConvertor getDefaultDateTimeConvertor() {
        return convertor;
    }
    private DateTimeConvertor convertor;
}
