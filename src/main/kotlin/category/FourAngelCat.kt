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

package category

import category.CatCommonFun.idPattern
import category.CatCommonFun.spacing

class FourAngelCat(val type: Types) : ICategory {
    override val url = "http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%BB%CD%C2%E7%C5%B7%BB%CA%A5%DE%A5%EB%A5%C1%A5%D0%A5%C8%A5%EB_%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4"
    override var tag = "四天司"

    override fun filter(src: String): String? {
        if (!src.contains(type.tag)) return null

        val tempBuffer = StringBuffer()
        val target = getTarget(src)
        tempBuffer.append(target).append(spacing)

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