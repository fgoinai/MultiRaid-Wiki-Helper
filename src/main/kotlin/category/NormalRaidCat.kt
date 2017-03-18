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

package category

import category.CatCommonFun.idPattern
import category.CatCommonFun.spacing

class NormalRaidCat(val type: Types) : ICategory {
    override val url = "http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%C4%CC%BE%EF%A5%DE%A5%EB%A5%C1%A5%D0%A5%C8%A5%EB%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4"
    override var tag = ""

    override fun filter(src: String): String? {
        val tempBuffer = StringBuffer()
        val target = getTarget(src)

        if (getFlag(target)) {
            tempBuffer.append(target).append(spacing)
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
            WEAK_BAHA("よわバハ/よわばは"),
            KIRIN_RYU("黒麒麟/黄龍"),
            MICHAEL("ミカエル"), //火天司
            GABRIEL("ガブリエル"), //水天司
            URIEL("ウリエル"), //土天司
            RAPHAEL("ラファエル") //風天司
        }
    }

}