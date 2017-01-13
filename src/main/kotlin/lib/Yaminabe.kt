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

package lib

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*

class Yaminabe {
    companion object {
        val maxListLen = 10
        val spacing = "    "
        val idPattern = Regex("\\w{8}")
    }

    fun getList(path: String, isBaha: Boolean, isNormal: Boolean = false, filter: Any, inverseNormal: Boolean = false): ArrayList<kotlin.String>? {
        try {
            val list = ArrayList<String>()
            val con = URL(path).openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(con.inputStream, Charset.forName("EUC-JP")))
            var buffer: String

            while (true) {
                buffer = java.lang.String(reader.readLine()?.toByteArray() ?: break, "UTF-8") as String
                if (buffer.contains("<span class=\"comment_date\">")) {
                    val temp = buffer.split(Regex("\\W")).filter { it.matches(idPattern) }
                    if (temp.size == 0) {
                        continue
                    }
                    val tempBuffer = StringBuilder()
                    try {
                        if (!isBaha) {
                            if (isNormal) {
                                val target = buffer.split("<li>")[1].split(idPattern)[0].replace(" ", "").replace("　", "")
                                if (!inverseNormal) {
                                    if (filter is String) {
                                        if (target == filter) {
                                            tempBuffer.append(target)
                                            tempBuffer.append(spacing)
                                        } else {
                                            continue
                                        }
                                    } else {
                                        if ((filter as List<String>).contains(target)) {
                                            tempBuffer.append(target)
                                            tempBuffer.append(spacing)
                                        } else {
                                            continue
                                        }
                                    }
                                } else {
                                    if (!(filter as List<String>).contains(target)) {
                                        tempBuffer.append(target)
                                        tempBuffer.append(spacing)
                                    } else {
                                        continue
                                    }
                                }
                            } else {
                                tempBuffer.append(buffer.split("<li>")[1].split(idPattern)[0].replace(" ", "").replace("　", ""))
                                tempBuffer.append(spacing)
                            }
                        }

                        tempBuffer.append(temp[0].toUpperCase().replace(" ", ""))
                        tempBuffer.append(spacing)
                        tempBuffer.append(buffer.split("<span class=\"comment_date\">")[1].split("<")[0].toUpperCase())
                        while (list.size >= maxListLen)
                            list.remove(list[0])
                        list.add(tempBuffer.toString())

                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("The buffer: $buffer")
                    }
                }
            }

            return list.reversed() as ArrayList<String>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}