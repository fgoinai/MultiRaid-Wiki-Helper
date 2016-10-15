package lib

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

class DuplicateFileChecker(val path: String) {

    val duplicateList = HashMap<String, SameFileList>()
    private val finishedFile = ArrayList<File>()

    class SameFileList(dupFile: File) {
        val filesList = ArrayList<File>()
        init {
            filesList.add(dupFile)
        }
    }

    fun startCheck(localPath: String = path) {
        val file = File(localPath)
        if (file.isFile)
            return

        file.listFiles().forEach {
            if (it.isDirectory) {
                startCheck(it.absolutePath)
            } else {
                if (finishedFile.isEmpty())
                    finishedFile.add(it)
                else {
                    val localFile = it
                    finishedFile.forEach {
                        if (FileUtils.contentEquals(it, localFile))
                            duplicateList[it.name]?.filesList?.add(localFile) ?: duplicateList.put(it.name, SameFileList(localFile))
                    }
                }
            }
        }
    }

    fun showResult() {
        duplicateList.forEach { s, sameFileList ->
            println(s + ":")
            sameFileList.filesList.forEach {
                println(it.absolutePath)
            }
        }
    }
}