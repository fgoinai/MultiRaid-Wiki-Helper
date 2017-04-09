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

package src.category

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import src.lib.Conf

class SixManHlCat : ICategory {
    override val url = Conf.getUrl("6人HL")
    override var tag = "6人HL"

    override fun filter(src: String): String? {
        if (src.contains("<br class=\"spacer\" />")) {


            if (!src.contains("【募集】")) {
                return null
            }

            return runBlocking { collectors(src) }
        }
        return null
    }

    suspend private fun collectors(src: String): String {
        val tempBuffer = ArrayList<String>()

        val extractedStr = async(CommonPool) {
            src.split("<li>")
                    .filter { it.contains(CatCommonFun.idPattern) }[0]
        }

        val target = async(CommonPool) {
            extractedStr.await().split("<br class=\"spacer\" />")
                    .filter { it.contains(CatCommonFun.idPattern) }[0]
                    .split(CatCommonFun.idPattern)[0]
                    .replace("】", "")
                    .replace("【", "")
                    .replace("ID", "")
                    .replace("　", "")
                    .replace(" ", "")
                    .replace("募集", "")
        }

        val requirement = async(CommonPool) {
            extractedStr.await().split("<br class=\"spacer\" />")
                    .filter { it.contains("【募集】") }[0]
                    .replace("【募集】", "")
        }


        tempBuffer.add(target.await())
        tempBuffer.add(CatCommonFun.spacing)
        val temp = CatCommonFun.commonFilter(src, tempBuffer) ?: throw Exception("${javaClass.name} has null value on CatcommonFun.commonFilter")
        tempBuffer.clear()
        tempBuffer.add(temp)
        tempBuffer.add("需要:")
        tempBuffer.add(requirement.await())
        tempBuffer.add(CatCommonFun.spacing)

        return tempBuffer.reduce { x, y -> x + y }
    }

}