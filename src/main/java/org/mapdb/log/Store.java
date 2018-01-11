package org.mapdb.log;

import kotlin.Pair;

public interface Store {

    /** Update, produce new snapshot. {@code Long.MIN_VALUE} marks keys to delete */
    Store update(Iterable<Pair<Long,Long>> keyvalues);

    /** return value associated with key, Long.MIN_VALUE marks deleted or non existent key */
    long get(long key);
}
