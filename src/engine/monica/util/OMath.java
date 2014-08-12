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

package engine.monica.util;

public final class OMath {

    public static int minTimes(int n) {
        if (n > 1) {
            int p = 0;
            int t = n;
            while (t != 0) {
                t >>= 1;
                ++p;
            }
            int pow = 2 << p;
            if (pow < n || pow % n == 0)
                --p;
            return p;
        }
        return 1;
    }

    public static int minTimes(long n) {
        if (n > 1) {
            int p = 0;
            long t = n;
            while (t != 0) {
                t >>= 1;
                ++p;
            }
            long pow = 2L << p;
            if (pow < n || pow % n == 0)
                --p;
            return p;
        }
        return 1;
    }

    /*public static int countOfOne(int n) {
     if (n < 256 && n > -1)
     return cooTable[n];
     return cooTable[n & 0xFF]
     + cooTable[(n >> 8) & 0xFF]
     + cooTable[(n >> 16) & 0xFF]
     + cooTable[(n >> 24) & 0xFF];
     }

     public static int countOfOne(long n) {
     if (n < 256L && n > -1L)
     return cooTable[(int) (n & 0xFF)];
     return cooTable[(int) (n & 0xFF)]
     + cooTable[(int) ((n >> 8) & 0xFF)]
     + cooTable[(int) ((n >> 16) & 0xFF)]
     + cooTable[(int) ((n >> 24) & 0xFF)]
     + cooTable[(int) ((n >> 32) & 0xFF)]
     + cooTable[(int) ((n >> 40) & 0xFF)]
     + cooTable[(int) ((n >> 48) & 0xFF)]
     + cooTable[(int) ((n >> 56) & 0xFF)];
     }

     private static final int[] cooTable = new int[]{
     0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
     4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};*/

    public static int radix(int n, int from, int to) {
        if (n < to)
            return n;
        return radix(n / to, from, to) * from + n % to;
    }

    public static long radix(long n, int from, int to) {
        if (n < to)
            return n;
        return radix(n / to, from, to) * from + n % to;
    }

    public static int isPowerOfTwo(int n) {
        if (n == 2)
            return 1;
        else if (n > 2) {
            int p = 0;
            int t = n;
            int oc = 0;
            do {
                if ((t & 1) == 0)
                    ++p;
                else if (oc == 1)
                    return -1;
                else
                    ++oc;
                t >>= 1;
            } while (t != 0);
            return p;
        } else
            return -1;
    }

    public static long isPowerOfTwo(long n) {
        if (n == 2L)
            return 1L;
        else if (n > 2) {
            int p = 0;
            long t = n;
            int oc = 0;
            do {
                if ((t & 1L) == 0)
                    ++p;
                else if (oc == 1)
                    return -1L;
                else
                    ++oc;
                t >>= 1;
            } while (t != 0);
            return p;
        } else
            return -1L;
    }
}
