package org.mapdb.log

import java.io.*
import java.util.concurrent.atomic.AtomicLong

class LogStore(
        val dir: File,
        val referencedFiles: List<File> = ArrayList<File>(),
        val curFile:Long=0
    ):Store{

    companion object {
        val fileCounter = AtomicLong(0)
    }


    override fun update(keyvalues: List<Pair<Long,Long>>): Store {
        val newFileNum = fileCounter.incrementAndGet()
        val newFile = numToFile(newFileNum)
        newFile.deleteOnExit()
        assert(!newFile.exists())

        val out1 = FileOutputStream(newFile)
        val out2 = DataOutputStream(BufferedOutputStream(out1))


        //write prev file
        out2.writeLong(curFile)

        //write number of key-values
        out2.writeLong(keyvalues.size.toLong())

        //assert sorted
        keyvalues.fold(Pair(Long.MIN_VALUE, Long.MIN_VALUE)) { prev: Pair<Long, Long>, cur: Pair<Long, Long> ->
            assert(prev.first< cur.first)
            cur
        }

        //write key-vals
        for((key,value) in keyvalues){
            out2.writeLong(key)
            //TODO write files into separate region
            out2.writeLong(value)
        }


        //flush
        out2.flush()
        // TODO file sync? out1.fd.sync()
        out1.close()

        val refFiles2 = referencedFiles+newFile

        return LogStore(dir=dir, referencedFiles = refFiles2, curFile =newFileNum)

    }

    private fun numToFile(fileNum: Long) = File(dir.path + "/log" + fileNum)

    override fun get(key: Long): Long {
        //start at current file
        var fileNum = curFile

        while(fileNum!=0L){
            val file = numToFile(fileNum)
            assert(file.exists())
            val in1 = FileInputStream(file)
            val in2 = DataInputStream(BufferedInputStream(in1))

            //read prev file number
            fileNum = in2.readLong()

            val keyValCount = in2.readLong()

            //TODO binary search
            for(i in 0 until keyValCount){
                val key2 = in2.readLong()
                val value = in2.readLong()

                if(key2==key)
                    return value
            }
        }
        //not found
        return Long.MIN_VALUE

    }

}