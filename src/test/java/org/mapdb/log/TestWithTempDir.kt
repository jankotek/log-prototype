package org.mapdb

import org.junit.After
import java.io.File

/**
 * Test case with temporary dir,
 * dir is deleted after tests finishes
 */
abstract class TestWithTempDir{

    private var tempDirCreated = false

    fun tempFile() = File(tempDir, Math.random().toString())

    val  tempDir: File by lazy{
        tempDirCreated = true
        tempDir()
    }

    @After fun deleteTempDir(){
        if(tempDirCreated)
            tempDeleteRecur(tempDir)
    }

    /*
     * Create temporary file in temp folder. All associated db files will be deleted on JVM exit.
     */
    private fun tempFile2(): File {
        fun sanitize(name:String) = java.lang.String(name).replaceAll("[^a-zA-Z0-9_\\.]+","")
        val tempDir = System.getProperty("java.io.tmpdir");
        val stackTrace = Thread.currentThread().stackTrace;
        val elem = stackTrace[2];
        val prefix = "mapdbTest_"+sanitize(elem.className)+"-"+sanitize(elem.methodName)+"-"+elem.lineNumber+"_"
        while(true){
            val file = File(tempDir+File.separator+prefix+System.currentTimeMillis()+"_"+Math.random());
            if(file.exists().not()) {
                file.deleteOnExit()
                return file
            }
        }
    }

    private fun tempDir(): File {
        val ret = tempFile2()
        ret.mkdir()
        return ret
    }

    private fun tempDeleteRecur(file: File) {
        if(file.isDirectory){
            for(child in file.listFiles())
                tempDeleteRecur(child)
        }
        file.delete()
    }


}