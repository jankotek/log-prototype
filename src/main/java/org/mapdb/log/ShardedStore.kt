package org.mapdb.log

import java.io.File

class ShardedStore(val dir: File):Store{

    override fun update(keyvalues: MutableList<Pair<Long, Long>>?): Store {
       return this
    }

    override fun get(key: Long): Long {
       return 0L
    }


}