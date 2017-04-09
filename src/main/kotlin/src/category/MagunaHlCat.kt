/*
 * File: MagunaHlCat.kt
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

import src.lib.Conf

class MagunaHlCat : ICategory {
    override val url = Conf.getUrl("方陣HL")
    override var tag = "方陣HL"

    override fun filter(src: String): String? {
        if (src.contains("<li>")) {
            val tempBuffer = ArrayList<String>()

            tempBuffer.add(src.split("<li>")[1].split(CatCommonFun.idPattern)[0].replace(" ", "").replace("　", ""))
            tempBuffer.add(CatCommonFun.spacing)

            return CatCommonFun.commonFilter(src, tempBuffer)
        }
        return null
    }

}