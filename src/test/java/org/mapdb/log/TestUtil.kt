package org.mapdb.log

import java.io.File

object TestUtil {

    /*
     * Create temporary file in temp folder. All associated db files will be deleted on JVM exit.
     */
    fun tempFile2(): File {
        fun sanitize(name:String) = java.lang.String(name).replaceAll("[^a-zA-Z0-9_\\.]+","")
        val tempDir = System.getProperty("java.io.tmpdir");
        val stackTrace = Thread.currentThread().stackTrace;
        val elem = stackTrace[2];
        val prefix = "mapdbTest_"+sanitize(elem.className)+"-"+sanitize(elem.methodName)+"-"+elem.lineNumber+"_"
        while(true){
            val file = File(tempDir+ File.separator+prefix+System.currentTimeMillis()+"_"+Math.random());
            if(file.exists().not()) {
                file.deleteOnExit()
                return file
            }
        }
    }

    fun tempDir(): File {
        val ret = tempFile2()
        ret.mkdir()
        return ret
    }

    fun tempDeleteRecur(file: File) {
        if(file.isDirectory){
            for(child in file.listFiles())
                tempDeleteRecur(child)
        }
        file.delete()
    }

}