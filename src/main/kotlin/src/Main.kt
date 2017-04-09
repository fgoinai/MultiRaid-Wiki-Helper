/*
 * File: MainWindow.kt
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
package src

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.scene.Group
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage
import src.category.CatCommonFun
import src.category.CatCommonFun.getCatList
import src.category.CbUpdateList
import src.category.ICategory
import src.lib.Conf
import src.lib.Yaminabe
import src.view.Noti
import src.view.View
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class Main : Application(), CbUpdateList {

    companion object {
        private val RENEW_LIMIT_LOW = 10L
        val RENEW_INTERVAL = Conf.getIntervalConf(RENEW_LIMIT_LOW)

        @JvmStatic fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }

    private val nabe = lazy { Yaminabe() }
    private val view = lazy { View() }
    private val noti = lazy { Noti() }

    private var threadPool = Executors.newSingleThreadScheduledExecutor()
    private val obList = FXCollections.observableArrayList<String>()
    private var topMsg = ""
    var future: ScheduledFuture<*>? = null

    override fun start(primaryStage: Stage) {
        //update GUI drawing
        val root = Group()
        val scene = view.value.getScene(root, obList)
        val tabPane = view.value.getTabPane()
        val borderPane = view.value.getBorderPane(scene, tabPane)

        //add listener
        //TODO - will be removed due to MVVM???
        view.value.addSelectionModelListener(
                tabPane,
                getChangeListener(tabPane, primaryStage)
        )

        root.children.add(borderPane)
        view.value.setStage(primaryStage, scene)
        primaryStage.show()
    }

    private fun getChangeListener(tabPane: TabPane, stage: Stage): ChangeListener<Tab> {
        return ChangeListener { observable, srcTab, destTab ->
            try {
                tabPane.tabs
                        .filter { it != destTab }
                        .forEach { view.value.clearTabContent(it) }
                future?.cancel(true)
                future = null
                if (destTab.text == "教學") {
                    view.value.fillTutorialTabContent(destTab)
                } else {
                    Platform.runLater { obList.clear() }
                    val task = Runnable {
                        stage.setOnCloseRequest {
                            future?.cancel(true)
                            threadPool.shutdownNow()
                            Platform.exit()
                        }

                        renewItems(getCatList()[destTab.text]!!.value)
                    }
                    future = threadPool.scheduleAtFixedRate(task, 0, RENEW_INTERVAL, TimeUnit.SECONDS)

                    view.value.fillTabContent(destTab)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun renewItems(cat: ICategory) {
        Platform.runLater {
            obList.clear()
            obList.add("更新中")
        }
        nabe.value.getList(cat, this)
    }

    override fun update(src: java.util.ArrayList<String>?) {
        Platform.runLater { obList.clear() }
        if (src == null) {
            Platform.runLater {
                obList.add("沒有場")
            }
            return
        }

        Platform.runLater {
            obList.addAll(src as ArrayList<out String>)
            if (topMsg != obList[0]) {
                topMsg = obList[0]
                noti.value.show(topMsg, topMsg.split(" ").filter { it.matches(CatCommonFun.idPattern) }[0])
            }
        }
    }
}