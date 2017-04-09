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

package src.category

import java.util.*

interface ICategory {
    val url: String
    var tag: String
    fun filter(src: String): String?
}

interface CbUpdateList {
    fun update(src: ArrayList<String>?)
}

object CatCommonFun {
    val spacing = "    "
    val idPattern = Regex("\\w{8}")
    val nonWordPattern = Regex("\\W")

    private fun strToList(src: String): List<String>? {
        if (src.contains("<span class=\"comment_date\">")) {
            val temp = src.split(nonWordPattern)
            if (temp.filter { it.matches(idPattern) }.isEmpty()) return null

            return temp.filter { it.matches(idPattern) }
        }
        return null
    }

    fun commonFilter(src: String, srcBuffer: ArrayList<String>): String? {
        if (strToList(src) != null) {
            srcBuffer.add(strToList(src)!![0].toUpperCase().replace(" ", ""))
            srcBuffer.add(spacing)
            srcBuffer.add(src.split("<span class=\"comment_date\">")[1].split("<")[0].toUpperCase())
            return srcBuffer.reduce { x, y -> x + y }
        }
        return null
    }

    fun getCatList(): LinkedHashMap<String, Lazy<ICategory>> {
        val ret = LinkedHashMap<String, Lazy<ICategory>>()

        ret.put("大巴", lazy { BahamutCat() })
        ret.put("召喚終突", lazy { NormalSummonCat() })
        ret.put("方陣HL", lazy { MagunaHlCat() })
        ret.put("6人HL", lazy { SixManHlCat() })

        ret.put("火天司", lazy { FourAngelCat(FourAngelCat.Companion.Types.MICHAEL) })
        ret.put("水天司", lazy { FourAngelCat(FourAngelCat.Companion.Types.GABRIEL) })
        ret.put("土天司", lazy { FourAngelCat(FourAngelCat.Companion.Types.URIEL) })
        ret.put("風天司", lazy { FourAngelCat(FourAngelCat.Companion.Types.RAPHAEL) })

        ret.put("丁丁", lazy { NormalRaidCat(NormalRaidCat.Companion.Types.GRANDEE) })
        ret.put("小巴", lazy { NormalRaidCat(NormalRaidCat.Companion.Types.WEAK_BAHA) })
        ret.put("麒麟/黃龍", lazy { NormalRaidCat(NormalRaidCat.Companion.Types.KIRIN_RYU) })
        ret.put("其他", lazy { NormalRaidCat(NormalRaidCat.Companion.Types.OTHER) })


        return ret
    }
}