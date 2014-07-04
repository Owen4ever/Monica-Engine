/*
 * The MIT License
 *
 * Copyright 2014 Owen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package game.engine.monica.util;

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
