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