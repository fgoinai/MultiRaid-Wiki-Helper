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
import src.category.CatCommonFun.spacing
import src.category.CbUpdateList
import src.category.ICategory
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*
import kotlin.coroutines.experimental.buildSequence
import kotlin.system.measureTimeMillis

class Yaminabe {
    private val maxListLen = 5

    fun getList(cat: ICategory, cb: CbUpdateList) {
        val queue = ConcurrentLinkedQueue<String>()
        val con = URL(cat.url).openConnection() as HttpURLConnection
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0") //Anti Detection
        val reader = InputStreamReader(con.inputStream, Charset.forName("EUC-JP")).buffered()
        val buffer = buildSequence {
            while (true) {
                yield(java.lang.String(reader.readLine()?.toByteArray() ?: break, "UTF-8") as String)
            }
        }

        val time = measureTimeMillis {
            try {
                buffer.forEach {
                    if (it.contains("<span class=\"comment_date\">")) {
                        if (isTargetEmpty(it)) return
                        val ret = cat.filter(it) ?: return

                        while (queue.size >= maxListLen) {
                            queue.sortedWith(
                                    compareBy(
                                            {
                                                SimpleDateFormat("yyyy-MM-dd").parse(
                                                        it.split(spacing)
                                                                .filter { it.contains(Regex("\\d{4}-\\d{2}-\\d{2}")) }[0]
                                                                .split(" ")[0]
                                                )
                                            },
                                            {
                                                SimpleDateFormat("HH:mm:ss").parse(
                                                        it.split(spacing)
                                                                .filter { it.contains(Regex("\\d{2}:\\d{2}:\\d{2}")) }[0]
                                                                .split(" ")
                                                                .filter { it.contains(Regex("\\d{2}:\\d{2}:\\d{2}")) }[0]
                                                )
                                            }
                                    )
                            )
                            queue.poll()
                        }
                        queue.offer(ret)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (queue.isNotEmpty()) {
                cb.update(queue.reversed() as ArrayList<String>)
            } else {
                cb.update(null)
            }
        }
        println("${javaClass.name} cost $time")
    }

    fun isTargetEmpty(src: String) : Boolean {
        return src.split(nonWordPattern)
                .filter { it.matches(idPattern) }
                .isEmpty()
    }
}