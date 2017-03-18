/*
 * File: NormalSummonCat.kt
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

class NormalSummonCat : ICategory {
    override val url = "http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%BE%A4%B4%AD%C0%D0%BA%C7%BD%AA%B2%F2%CA%FC%A5%AF%A5%A8%A5%B9%A5%C8%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4"
    override var tag = "召喚終突"

    override fun filter(src: String): String? {
        if (src.contains("<li>")) {
            val tempBuffer = StringBuffer()

            tempBuffer.append(src.split("<li>")[1].split(CatCommonFun.idPattern)[0].replace(" ", "").replace("　", ""))
            tempBuffer.append(CatCommonFun.spacing)

            return CatCommonFun.commonFilter(src, tempBuffer)
        }
        return null
    }

}