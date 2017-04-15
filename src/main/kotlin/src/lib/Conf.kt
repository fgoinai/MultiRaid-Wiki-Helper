/*
 * File: Conf.kt
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

import org.ini4j.Ini
import java.io.File

object Conf {
    private val ini = Ini(javaClass.classLoader.getResource("conf.ini"))

    fun getIntervalConf(lower: Long): Long {
        var ret = ini.get("Other", "RenewInterval", Long::class.java)
        if (ret <= lower) {
            ret = lower
        }
        return ret
    }

    fun getUrlConf(): HashMap<String, String> {
        val ret = HashMap<String, String>()
        getIndex.forEach { ret.put(it, ini.get("URL", it)) }
        return ret
    }

    fun getUrl(tag: String): String {
        return ini.get("URL", tag)
    }

    private val getIndex = arrayOf(
            "大巴",
            "四天司",
            "方陣HL",
            "通常",
            "召喚終突",
            "6人HL"
    )
}