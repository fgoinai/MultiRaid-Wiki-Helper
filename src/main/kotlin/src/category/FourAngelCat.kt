/*
 * File: FourAngelCat.kt
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

class FourAngelCat(val type: Types) : ICategory {
    override val url = Conf.getUrl("四天司")
    override var tag = "四天司"

    override fun filter(src: String): String? {
        if (!src.contains(type.tag)) return null

        val tempBuffer = ArrayList<String>()
        val target = getTarget(src)
        tempBuffer.add(target)
        tempBuffer.add(spacing)

        return CatCommonFun.commonFilter(src, tempBuffer)
    }

    private fun getTarget(src: String): String {
        return src.split("<li>")[1].split(idPattern)[0].replace(" ", "").replace("　", "")
    }

    companion object {
        enum class Types(val tag: String) {
            MICHAEL("ミカエル"), //火天司
            GABRIEL("ガブリエル"), //水天司
            URIEL("ウリエル"), //土天司
            RAPHAEL("ラファエル") //風天司
        }
    }

}