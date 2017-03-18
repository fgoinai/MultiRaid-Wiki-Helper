/*
 * File: SixManHlCat.kt
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

class SixManHlCat : ICategory {
    override val url = "http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F6%BF%CDHL%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4"
    override var tag = "6人HL"

    override fun filter(src: String): String? {
        if (src.contains("<br class=\"spacer\" />")) {
            val tempBuffer = StringBuffer()
            val extractedStr = src.split("<li>")
                    .filter { it.contains(CatCommonFun.idPattern) }[0]
            val target = extractedStr.split("<br class=\"spacer\" />")
                    .filter { it.contains(CatCommonFun.idPattern) }[0]
                    .split(CatCommonFun.idPattern)[0]
                    .replace("】", "")
                    .replace("【", "")
                    .replace("ID", "")
                    .replace("　", "")
                    .replace(" ", "")
                    .replace("募集", "")

            var requirement: String = ""
            if (extractedStr.contains("【募集】")) {
                requirement = extractedStr.split("<br class=\"spacer\" />")
                        .filter { it.contains("【募集】") }[0]
                        .replace("【募集】", "")
            } else {
                return null
            }

            tempBuffer.append(target).append(CatCommonFun.spacing)
            CatCommonFun.commonFilter(src, tempBuffer)
            tempBuffer.append("需要:").append(requirement).append(CatCommonFun.spacing)
            return tempBuffer.toString()
        }
        return null
    }

}