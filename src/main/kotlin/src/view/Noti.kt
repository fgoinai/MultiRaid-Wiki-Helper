/*
 * File: Noti.kt
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

package src.view

import eu.hansolo.enzo.notification.Notification
import eu.hansolo.enzo.notification.NotifierBuilder
import javafx.geometry.Pos
import javafx.util.Duration
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


class Noti {
    companion object {
        private var instance: Notification.Notifier? = null
    }
    fun show(msg: String, rmNo: String) {
        val noti = Notification("來一場", msg)
        if (instance != null) {
            instance?.stop()
            instance = null
        }
        instance = NotifierBuilder
                .create()
                .popupLocation(Pos.BOTTOM_RIGHT)
                .popupLifeTime(Duration.seconds(5.0))
                .build() as Notification.Notifier

        instance?.setOnShowNotification {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(rmNo), null)
        }
        instance?.setOnHideNotification {
            instance?.stop()
        }
        instance?.isAlwaysOnTop = true
        instance?.notify(noti)
    }
}