/*
 * File: ICategory.kt
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

package category

interface ICategory {
    val url: String
    var tag: String
    fun filter(src: String): String?
}

object CatCommonFun {
    val spacing = "    "
    val idPattern = Regex("\\w{8}")

    private fun strToList(src: String): List<String>? {
        if (src.contains("<span class=\"comment_date\">")) {
            val temp = src.split(Regex("\\W")).filter { it.matches(idPattern) }

            if (temp.isEmpty()) return null
            else return temp
        }
        return null
    }

    fun commonFilter(src: String, srcBuffer: StringBuffer): String? {
        if (strToList(src) != null) {
            srcBuffer.append(strToList(src)!![0].toUpperCase().replace(" ", ""))
            srcBuffer.append(spacing)
            srcBuffer.append(src.split("<span class=\"comment_date\">")[1].split("<")[0].toUpperCase())
            return srcBuffer.toString()
        }
        return null
    }
}