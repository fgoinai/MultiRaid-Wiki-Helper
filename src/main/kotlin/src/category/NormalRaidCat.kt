/*
 * File: NormalRaidCat.kt
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

import src.category.CatCommonFun.idPattern
import src.category.CatCommonFun.spacing
import src.lib.Conf

class NormalRaidCat(val type: Types) : ICategory {
    override val url = Conf.getUrl("通常")
    override var tag = ""

    override fun filter(src: String): String? {
        val tempBuffer = ArrayList<String>()
        val target = getTarget(src)

        if (getFlag(target)) {
            tempBuffer.add(target)
            tempBuffer.add(spacing)
        } else {
            return null
        }

        return CatCommonFun.commonFilter(src, tempBuffer)
    }

    fun getFlag(target: String): Boolean {
        if (type == Types.OTHER) {
            return Types.values().filter { it.tag == target }.isEmpty()
        } else {
            return type.tag.split("/").contains(target)
        }
    }

    private fun getTarget(src: String): String {
        return src.split("<li>")[1].split(idPattern)[0].replace(" ", "").replace("　", "")
    }

    companion object {
        enum class Types(val tag: String) {
            OTHER("其他"),
            GRANDEE("グランデ"),
            WEAK_BAHA("よわバハ/よわばは/バハ/ばは"),
            KIRIN_RYU("黒麒麟/黄龍")
        }
    }

}