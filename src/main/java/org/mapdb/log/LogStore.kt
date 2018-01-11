package org.mapdb.log

import java.io.*
import java.nio.*
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.util.concurrent.atomic.AtomicLong

class LogStore(
        val dir: File,
        val referencedFiles: Map<Long, ByteBuffer> = HashMap<Long,ByteBuffer>(),
        val curFile:Long=0
    ):Store{

    companion object {
        //TODO static
        val fileCounter = AtomicLong(0)

    }


    override fun update(keyvalues: Iterable<Pair<Long,Long>>): Store {
        val newFileNum = fileCounter.incrementAndGet()
        val newFile = numToFile(newFileNum)
        newFile.deleteOnExit()
        assert(!newFile.exists())

        FileOutputStream(newFile).use { out1 ->
            val out2 = DataOutputStream(BufferedOutputStream(out1))


            //write prev file
            out2.writeLong(curFile)

            //write number of key-values
            //TODO do not count, use single iteration
            out2.writeLong(keyvalues.count().toLong())

            //TODO single pass, use temp files
            //assert sorted
            keyvalues.fold(Pair(Long.MIN_VALUE, Long.MIN_VALUE)) { prev: Pair<Long, Long>, cur: Pair<Long, Long> ->
                assert(prev.first < cur.first)
                cur
            }

            //write keys
            for ((key, value) in keyvalues) {
                out2.writeLong(key)
            }

            //write vals
            for ((key, value) in keyvalues) {
                out2.writeLong(value)
            }


            //flush
            out2.flush()

        }

        //mmap
        val mmap = FileChannel.open(newFile.toPath(), StandardOpenOption.READ).use{
            it.map(FileChannel.MapMode.READ_ONLY, 0, newFile.length())
        }
        // TODO file sync? out1.fd.sync()
        val refFiles2 = HashMap(referencedFiles)
        refFiles2.put(newFileNum, mmap)
        return LogStore(dir=dir, referencedFiles = refFiles2, curFile =newFileNum)
    }

    private fun numToFile(fileNum: Long) = File(dir.path + "/log" + fileNum)

    override fun get(key: Long): Long {
        //start at current file
        var fileNum = curFile

        while(fileNum!=0L){
            assert(numToFile(fileNum).exists())

            val mmap = referencedFiles[fileNum]!!
            //read prev file
            fileNum = mmap.getLong(0)
            val count = mmap.getLong(8).toInt()
            val size = count*16+16

            //run binary search
            val keyOffset = Util.binarySearch(mmap, key, size)
            if(keyOffset>0)
                return mmap.getLong(keyOffset+count*8)
        }
        //not found
        return Long.MIN_VALUE

    }


}