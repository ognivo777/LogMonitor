package ru.lanit.dibr.utils.utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 * User: Vova
 * Date: 13.12.12
 * Time: 1:18
 */
public class Utils {

    public static String inputStreamToString(InputStream is){
        return "";
    }

    public static int indexOf(String context, boolean caseSensitive, int from, String toFind) {
        if(from >= context.length()) {
            from = context.length()-1;
        }
        if (from < 0) {
            from = 0;
        }
        int toFindIndex = 0;
        char l = toFind.charAt(toFindIndex); //char to match now
        if (!caseSensitive) {
            l = Character.toLowerCase(l);
        }
        for (int i = from; i < context.length(); i++) {
            char c = context.charAt(i);
            if (!caseSensitive) {
                c = Character.toLowerCase(c);
            }
            if (c != l) {
                if(toFindIndex!=0) {
                    i-=toFindIndex;
                    toFindIndex = 0;
                }
            } else if (toFindIndex + 1 == toFind.length()) {
                return i - toFind.length() + 1;
                // all characters have been found, return index of match
            } else {
                ++toFindIndex;
            }
            l = toFind.charAt(toFindIndex);// set character to find
            if (!caseSensitive) {
                l = Character.toLowerCase(l);
            }
        }
        return -1;
    }

    public static int lastIndexOf(String context, boolean caseSensitive, int from, String toFind) {
        if(from >= context.length()) {
            from = context.length()-1;
        }
        if (from < 0) {
            from = 0;
        }
        int toFindIndex = toFind.length()-1;
        char l = toFind.charAt(toFindIndex);//char to match now
        if (!caseSensitive) {
            l = Character.toLowerCase(l);
        }
        for (int i = from; i >= 0; i--) {
            char c = context.charAt(i);
            if (!caseSensitive) {
                c = Character.toLowerCase(c);
            }
            if (c != l) {
                if(toFindIndex != toFind.length()-1) {
                    i+=(toFind.length()-toFindIndex-1);
                    toFindIndex = toFind.length()-1;
                }
            } else if (toFindIndex == 0) {
                return i;
                // all characters have been found, return index of match
            } else {
                --toFindIndex;
            }
            l = toFind.charAt(toFindIndex);// set character to find
            if (!caseSensitive) {
                l = Character.toLowerCase(l);
            }
        }
        return -1;
    }


    /**
     * <p>Find the Levenshtein distance between two Strings if it's less than or equal to a given
     * threshold.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * <p>This implementation follows from Algorithms on Strings, Trees and Sequences by Dan Gusfield
     * and Chas Emerick's implementation of the Levenshtein distance algorithm from
     * <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, *, -1)               = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","", 0)               = 0
     * StringUtils.getLevenshteinDistance("aaapppp", "", 8)       = 7
     * StringUtils.getLevenshteinDistance("aaapppp", "", 7)       = 7
     * StringUtils.getLevenshteinDistance("aaapppp", "", 6))      = -1
     * StringUtils.getLevenshteinDistance("elephant", "hippo", 7) = 7
     * StringUtils.getLevenshteinDistance("elephant", "hippo", 6) = -1
     * StringUtils.getLevenshteinDistance("hippo", "elephant", 7) = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant", 6) = -1
     * </pre>
     *
     * @param s  the first String, must not be null
     * @param t  the second String, must not be null
     * @param threshold the target threshold, must not be negative
     * @return result distance, or {@code -1} if the distance would be greater than the threshold
     * @throws IllegalArgumentException if either String input {@code null} or negative threshold
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, final int threshold) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }

        /*
        This implementation only computes the distance if it's less than or equal to the
        threshold value, returning -1 if it's greater.  The advantage is performance: unbounded
        distance is O(nm), but a bound of k allows us to reduce it to O(km) time by only
        computing a diagonal stripe of width 2k + 1 of the cost table.
        It is also possible to use this to compute the unbounded Levenshtein distance by starting
        the threshold at 1 and doubling each time until the distance is found; this is O(dm), where
        d is the distance.

        One subtlety comes from needing to ignore entries on the border of our stripe
        eg.
        p[] = |#|#|#|*
        d[] =  *|#|#|#|
        We must ignore the entry to the left of the leftmost member
        We must ignore the entry above the rightmost member

        Another subtlety comes from our stripe running off the matrix if the strings aren't
        of the same size.  Since string s is always swapped to be the shorter of the two,
        the stripe will always run off to the upper right instead of the lower left of the matrix.

        As a concrete example, suppose s is of length 5, t is of length 7, and our threshold is 1.
        In this case we're going to walk a stripe of length 3.  The matrix would look like so:

           1 2 3 4 5
        1 |#|#| | | |
        2 |#|#|#| | |
        3 | |#|#|#| |
        4 | | |#|#|#|
        5 | | | |#|#|
        6 | | | | |#|
        7 | | | | | |

        Note how the stripe leads off the table as there is no possible way to turn a string of length 5
        into one of length 7 in edit distance of 1.

        Additionally, this implementation decreases memory usage by using two
        single-dimensional arrays and swapping them back and forth instead of allocating
        an entire n by m matrix.  This requires a few minor changes, such as immediately returning
        when it's detected that the stripe has run off the matrix and initially filling the arrays with
        large values so that entries we don't compute are ignored.

        See Algorithms on Strings, Trees and Sequences by Dan Gusfield for some discussion.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        // if one string is empty, the edit distance is necessarily the length of the other
        if (n == 0) {
            return m <= threshold ? m : -1;
        } else if (m == 0) {
            return n <= threshold ? n : -1;
        }

        if (n > m) {
            // swap the two strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // fill in starting table values
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }
        // these fills ensure that the value above the rightmost entry of our
        // stripe will be ignored in following loop iterations
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        // iterates through t
        for (int j = 1; j <= m; j++) {
            final char t_j = t.charAt(j - 1); // jth character of t
            d[0] = j;

            // compute stripe indices, constrain to array size
            final int min = Math.max(1, j - threshold);
            final int max = (j > Integer.MAX_VALUE - threshold) ? n : Math.min(n, j + threshold);

            // the stripe may lead off of the table if s and t are of different sizes
            if (min > max) {
                return -1;
            }

            // ignore entry left of leftmost
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            // iterates through [min, max] in s
            for (int i = min; i <= max; i++) {
                if (s.charAt(i - 1) == t_j) {
                    // diagonally left and up
                    d[i] = p[i - 1];
                } else {
                    // 1 + minimum of cell to the left, to the top, diagonally left and up
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                }
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // if p[n] is greater than the threshold, there's no guarantee on it being the correct
        // distance
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }

    public static void writeToDebugQueue(BlockingQueue queue, String message) {
        System.out.println(message);
        if(queue!=null && queue.remainingCapacity()>0) {
            queue.add(message);
        }
    }

    public static Throwable getFirstCause(Exception e) {
        Throwable res = e;
        while(res.getCause()!=null) {
            res = res.getCause();
        }
        return res;
    }

}
