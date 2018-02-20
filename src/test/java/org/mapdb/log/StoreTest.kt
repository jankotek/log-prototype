package org.mapdb.log

import org.junit.Test
import io.kotlintest.matchers.*
import java.util.*

abstract class StoreTest: TestWithTempDir() {

    abstract fun create():Store

    @Test fun get(){
        var store=create()

        store.get(1) shouldBe Long.MIN_VALUE

        store = store.update(mutableListOf(Pair(1L,1L)))

        store.get(1) shouldBe 1L
        store.get(0) shouldBe Long.MIN_VALUE
    }

    @Test fun getMulti(){
        var store=create()

        store.get(1) shouldBe Long.MIN_VALUE


        for(i in 1L until 10000) {
            store = store.update(mutableListOf(Pair(1L, i)))
            store.get(1) shouldBe i
        }
    }


    @Test fun getMulti2(){
        var store=create()

        store.get(1) shouldBe Long.MIN_VALUE


        for(i in 1L until 100) {
            val v:Iterable<Pair<Long,Long>> = (1L until 100).map {a-> Pair(a, a*i) }
            store = store.update(v)
            for((key,value) in v){
                store.get(key) shouldBe value
            }
        }
    }



    @Test fun oldNotModified(){
        var store=create()

        store.get(1) shouldBe Long.MIN_VALUE

        val max = 100

        val stores = (1L until max).map{a->
            store = store.update(listOf(Pair(a,a)))
            Pair(a,store)
        }

        for((a, store) in stores){
            for(i in 1L until max){
                val expected = if(i<=a) i else Long.MIN_VALUE
                store.get(i) shouldBe expected
            }
        }
    }

    @Test fun all_removed(){
        var store=create()

        val rounds = 100
        val max = 100
        var r = Random(0)

        for(round in 0 until rounds){
            val keyVals = (0 until max).map { Pair(r.nextLong(), r.nextLong()) }.toList().sortedBy { it.first }
            store = store.update(keyVals)
        }

        //check content
        r = Random(0)
        for(round in 0 until rounds){
            for (i in (0 until max)){
                val key = r.nextLong()
                val value = r.nextLong()
                store.get(key) shouldBe value
            }
        }

        //delete
        r = Random(0)
        for(round in 0 until rounds){
            val keyVals = (0 until max).map { Pair(r.nextLong(), r.nextLong()) }.toList().sortedBy { it.first }
            store = store.update(keyVals.map {Pair(it.first, Long.MIN_VALUE) })
        }

        //check deleted

        //check content
        r = Random(0)
        for(round in 0 until rounds){
            for (i in (0 until max)){
                val key = r.nextLong()
                val value = r.nextLong()
                store.get(key) shouldBe Long.MIN_VALUE
            }
        }
    }

    @Test fun old_snapshots(){
        var store=create()

        val rounds = 10
        val max = 10
        var r = Random(0)

        data class Snapshot(val round:Int, val removed:Boolean, val store:Store)

        val snapshots = ArrayList<Snapshot>()

        fun checkSnapshots(){
            for(s in snapshots){
                val r = Random(0)
                for(round in 0 until rounds){
                    val exists =
                            if(s.removed) round>s.round // was not yet deleted, if round is higher
                            else round<=s.round // was not yet inserted

                    for(i in 0 until max){
                        val key = r.nextLong()
                        val value = r.nextLong()

                        s.store.get(key) shouldBe
                                if(exists) value else Long.MIN_VALUE
                    }

                }
            }
        }

        for(round in 0 until rounds){
            val keyVals = (0 until max).map { Pair(r.nextLong(), r.nextLong()) }.toList().sortedBy { it.first }
            store = store.update(keyVals)
            snapshots+=Snapshot(round=round, removed=false, store=store)
            checkSnapshots()
        }

        //delete
        r = Random(0)
        for(round in 0 until rounds){
            val keyVals = (0 until max).map { Pair(r.nextLong(), r.nextLong()) }.toList().sortedBy { it.first }
            store = store.update(keyVals.map {Pair(it.first, Long.MIN_VALUE) })
            snapshots+=Snapshot(round=round, removed=true, store=store)
            checkSnapshots()
        }
    }


}

class LogStoreTest:StoreTest(){
    override fun create(): Store = LogStore(dir=tempDir)
}

/*
class ShardedStoreTest:StoreTest(){
    override fun create(): Store = ShardedStore(dir=tempDir)

}
*/