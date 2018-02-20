package org.mapdb.log.bench

import org.mapdb.log.LogStore
import org.mapdb.log.Store
import org.mapdb.log.TestUtil

object continous_insert_get_oldest {

    val updates = 1e5.toLong()
    val max = 1e4.toLong()



    @JvmStatic
    fun main(args: Array<String>) {
        val tempDir = TestUtil.tempDir()
        var store: Store = LogStore(tempDir)

        for(update in 0 until updates){
            val keys = updates*max until updates*max+max
            val keyVals = keys.map { Pair(it, it) }
            store = store.update(keyVals)
        }


        //print time to get oldest key

        while(System.`in`.available()==0){
            val t = System.currentTimeMillis()
            store.get(0L)
            println("Get old key: ${System.currentTimeMillis() - t} ms")
        }
        TestUtil.tempDeleteRecur(tempDir)
    }


}