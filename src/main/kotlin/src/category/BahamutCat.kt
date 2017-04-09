/*
 * File: BahamutCat.kt
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

class BahamutCat : ICategory {
    override val url = Conf.getUrl("大巴")
    override var tag = "大巴 150LV"

    override fun filter(src: String): String? {
        return CatCommonFun.commonFilter(src, ArrayList<String>())
    }

}