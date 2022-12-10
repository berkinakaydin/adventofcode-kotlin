package utils

import java.io.InputStream

class FileReader {
    companion object {
        fun readFileLineByLineUsingForEachLine(fileName: String): InputStream? = FileReader::class.java.getResourceAsStream("../${fileName}")
    }
}