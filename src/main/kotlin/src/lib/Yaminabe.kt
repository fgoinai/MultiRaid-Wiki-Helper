/*
 * File: Yaminabe.kt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3
 * of the License.
 *
 * Project: MultiRaid Wiki Helper
 * Author: FGO
 * Copyright (c) 2016. FGO Production. All right reserved
 */

package src.lib

import src.category.CatCommonFun.idPattern
import src.category.CatCommonFun.nonWordPattern
import src.category.CbUpdateList
import src.category.ICategory
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import kotlin.system.measureTimeMillis

class Yaminabe {
    val maxListLen = 5

    fun getList(cat: ICategory, cb: CbUpdateList) {
        val list = ArrayList<String>()
        val con = URL(cat.url).openConnection() as HttpURLConnection
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0") //Anti Detection
        val reader = InputStreamReader(con.inputStream, Charset.forName("EUC-JP")).buffered()
        var buffer: String

        val time = measureTimeMillis {
            while (true) {
                try {
                    buffer = java.lang.String(reader.readLine()?.toByteArray() ?: break, "UTF-8") as String
                    if (buffer.contains("<span class=\"comment_date\">")) {
                        val temp = buffer.split(nonWordPattern).parallelStream().filter { it.matches(idPattern) }.toArray()
                        if (temp.isEmpty()) {
                            continue
                        }
                        val ret = cat.filter(buffer) ?: continue

                        while (list.size >= maxListLen) {
                            list.remove(list[0])
                        }
                        list.add(ret)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (list.isNotEmpty()) {
                cb.update(list.reversed() as ArrayList<String>)
            } else {
                cb.update(null)
            }
        }
        println("${javaClass.name} cost $time")
    }

}