package org.mapdb.log;

import java.nio.ByteBuffer;

public class Util {

    public static int binarySearch(ByteBuffer buf, long key, int size){
        int low = 16;
        int high = size - 8;

        assert(low% 8 == 0);
        assert(high% 8 == 0);
        while (low <= high) {
            int mid = ((low + high) /16) *8;

            assert(mid% 8 == 0);
            assert(mid < size);
            long midVal = buf.getLong(mid);

            if (midVal < key)
                low = mid + 8;
            else if (midVal > key)
                high = mid - 8;
            else
                return mid; // key found
        }
        return -(low + 8);  // key not found.
    }
}
