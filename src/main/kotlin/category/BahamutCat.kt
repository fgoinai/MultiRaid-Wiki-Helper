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

package category

class BahamutCat : ICategory {
    override val url = "http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%A5%D7%A5%ED%A5%D0%A5%CFHL%B0%C7%C6%E9%CA%E7%BD%B8%C8%C4"
    override var tag = "大巴 150LV"

    override fun filter(src: String): String? {
        return CatCommonFun.commonFilter(src, StringBuffer())
    }

}