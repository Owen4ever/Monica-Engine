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

package engine.monica.core.map;

import static engine.monica.util.OMath.minTimes;
import java.math.BigInteger;

public final class DateTime {

    public DateTime(int year, int mon, int day,
            int hour, int min, int sec, int msec) {
        this.year = year;
        this.month = mon;
        this.day = day;
        this.hour = hour;
        this.minute = min;
        this.second = sec;
        this.millisecond = msec;
        int h = this.year << 4 + this.month;
        h <<= 4;
        h += this.day;
        h <<= 4;
        h += this.hour;
        h <<= 4;
        h += this.minute;
        h <<= 4;
        h += this.second;
        h <<= 4;
        h += this.millisecond;
        this.hash = h;
    }

    public BigInteger toInteger(WorldDate d) {
        int pmon = minTimes(d.loopMon);
        int pday = minTimes(d.loopDay);
        int phour = minTimes(d.loopHour);
        int pmin = minTimes(d.loopMin);
        int psec = minTimes(d.loopSec);
        int pmsec = minTimes(d.loopMSec);
        return BigInteger.valueOf(year + 1)
                .shiftLeft(pmon).add(BigInteger.valueOf(month))
                .shiftLeft(pday).add(BigInteger.valueOf(day))
                .shiftLeft(phour).add(BigInteger.valueOf(hour))
                .shiftLeft(pmin).add(BigInteger.valueOf(minute))
                .shiftLeft(psec).add(BigInteger.valueOf(second))
                .shiftLeft(pmsec).add(BigInteger.valueOf(millisecond));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DateTime))
            return false;
        DateTime t = (DateTime) obj;
        return year == t.year && month == t.month && day == t.day
                && hour == t.hour && minute == t.minute
                && second == t.second && millisecond == t.millisecond;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return "[ Year = " + year + ", Month = " + month + ", Day = " + day
                + ", Hour = " + hour + ", Minute = " + minute
                + ", Second = " + second + ", MilliSecond = " + millisecond + " ]";
    }
    public final int year, month, day, hour, minute, second, millisecond;
    private final int hash;

    public static DateTime getDateTime(WorldDate d, BigInteger i) {
        int pmon = minTimes(d.loopMon);
        int pday = minTimes(d.loopDay);
        int phour = minTimes(d.loopHour);
        int pmin = minTimes(d.loopMin);
        int psec = minTimes(d.loopSec);
        int pmsec = minTimes(d.loopMSec);
        String s2 = i.toString(2);
        int pppppp = pmsec + psec + pmin + phour + pday + pmon;
        int pyear = s2.length() - pppppp;
        int msec = getNum(pppppp - pmsec,
                s2.substring(s2.length() - pmsec),
                0, i);
        int sec = getNum(pmon + pday + phour + pmin,
                s2.substring(s2.length() - pmsec - psec,
                        s2.length() - pmsec),
                pmsec, i);
        int min = getNum(pmon + pday + phour,
                s2.substring(s2.length() - pmsec - psec - pmin,
                        s2.length() - pmsec - psec),
                pmsec + psec, i);
        int hour = getNum(pmon + pday,
                s2.substring(pyear + pmon + pday,
                        s2.length() - pmsec - psec - pmin),
                pmsec + psec + pmin, i);
        int day = getNum(pmon,
                s2.substring(pyear + pmon, pyear + pmon + pday),
                pppppp - pday - pmon, i);
        int mon = getNum(pyear,
                s2.substring(pyear, pyear + pmon), pppppp - pmon, i);
        int year = getNum(0, s2.substring(0, pyear), pppppp, i) - 1;
        return new DateTime(year, mon, day, hour, min, sec, msec);
    }

    private static int getNum(int f, String m, int r, BigInteger i) {
        return i.and(new BigInteger(createStringWithSepcialChar('0', f) + m
                + createStringWithSepcialChar('0', r), 2))
                .and(new BigInteger(createStringWithSepcialChar('1', f) + m
                                + createStringWithSepcialChar('1', r), 2))
                .shiftRight(r).intValue();
    }

    private static String createStringWithSepcialChar(char c, int length) {
        StringBuilder sb = new StringBuilder(length);
        while (length > 0) {
            sb.append(c);
            --length;
        }
        return sb.toString();
    }
}
