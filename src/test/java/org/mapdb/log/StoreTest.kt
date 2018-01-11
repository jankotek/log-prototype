package org.mapdb.log

import org.junit.Test
import org.mapdb.TestWithTempDir
import io.kotlintest.matchers.*

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

}

class LogStoreTest:StoreTest(){
    override fun create(): Store = LogStore(dir=tempDir)

}

/*
class ShardedStoreTest:StoreTest(){
    override fun create(): Store = ShardedStore(dir=tempDir)

}
*/