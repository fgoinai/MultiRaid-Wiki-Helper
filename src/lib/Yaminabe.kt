package lib

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*

class Yaminabe {
    val maxListLen = 10

    fun getList(path: String, isBaha: Boolean) : ArrayList<kotlin.String>? {
        try {
            val list = ArrayList<String>()
            val con = URL(path).openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(con.inputStream, Charset.forName("EUC-JP")))
            var buffer: String

            while (true) {
                buffer = java.lang.String(reader.readLine()?.toByteArray() ?: break, "UTF-8")  as String
                if (buffer.contains("<span class=\"comment_date\">")) {
                    val temp = buffer.split(Regex("\\W")).filter { it.matches(Regex("\\w{8}")) }
                    if (temp.size == 0) {
                        continue
                    }
                    val tempBuffer = StringBuilder()
                    if (!isBaha) {
                        tempBuffer.append(buffer.split("<li>")[1].split(Regex("\\w{8}"))[0])
                        tempBuffer.append("    ")
                    }
                    tempBuffer.append(temp[0].toUpperCase())
                    tempBuffer.append("    ")
                    tempBuffer.append(buffer.split("<span class=\"comment_date\">")[1].split("<")[0].toUpperCase())
                    while (list.size >= maxListLen)
                        list.remove(list[0])
                    list.add(tempBuffer.toString())

                }
            }

            return list.reversed() as ArrayList<String>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}