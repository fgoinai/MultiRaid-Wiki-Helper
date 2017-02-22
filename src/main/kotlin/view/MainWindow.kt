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

package view

import eu.hansolo.enzo.notification.Notification
import eu.hansolo.enzo.notification.NotifierBuilder
import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import javafx.util.Duration
import lib.Yaminabe
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainWindow : Application() {

    companion object {
        //        private var thread = Thread()
        private val urlList = ArrayList<String>()
        private val normalList = ArrayList<String>()
        private var listView = lazy { ListView<String>() }
        private val obList = FXCollections.observableArrayList<String>()
        private var flag = 0
        private var topMsg = ""
        private var threadPool = Executors.newSingleThreadScheduledExecutor()

        private val tutContent = "此程式會以30秒的間隔自動更新\r\n" +
                "看到有場就點一下好了,會自動複製到剪貼版\r\n" +
                "彈出通知時也會自動複製\r\n" +
                "\r\n" +
                "此程式的暗鍋場來自GBF WIKI,請小心使用"

        @JvmStatic fun main(args: Array<String>) {
            launch(MainWindow::class.java)
        }

        private fun renewItems(path: String, isBaha: Boolean, isNormal: Boolean = false, filter: Any = "", inverseNormal: Boolean = false) {
            var list: ArrayList<String>?
            Platform.runLater { obList.clear() }
            if (!isNormal) {
                list = Yaminabe().getList(path, isBaha, filter = filter)
            } else if (!inverseNormal) {
                list = Yaminabe().getList(path, isBaha, isNormal, filter)
            } else {
                list = Yaminabe().getList(path, isBaha, isNormal, filter, inverseNormal)
            }
            Platform.runLater {
                obList.addAll(list as ArrayList<out String>)
                list?.clear()
                list = null
                if (topMsg != obList[0]) {
                    topMsg = obList[0]
                    showNoti(topMsg, topMsg.split(" ").filter { it.matches(Regex("\\w{8}")) }[0])
                }
//                System.gc()
            }
        }

        private var instance: Notification.Notifier? = null
        private fun showNoti(msg: String, rmNo: String) {
            val noti = Notification("地雷騎士來了～", msg)
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
//            println("Show")
            }
            instance?.setOnHideNotification {
                //            println("Stop")
                instance?.stop()
            }
            instance?.isAlwaysOnTop = true
            instance?.notify(noti)
        }
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "暗鍋助手"
        val root = Group()
        val scene = Scene(root, 400.0, 250.0, Color.WHITE)
        val tabPane = TabPane()
        val borderPane = BorderPane()

        initUrlList()
        initListView(scene)
        initNormalList()
        tabPaneInit(tabPane)

        tabPane.selectionModel.selectedItemProperty().addListener { observableValue, srcTab, destTab ->
            tabPane.tabs
                    .filter { it != destTab }
                    .forEach { clearTabContent(it) }
//            thread.interrupt()
            flag = flag and (0 shl tabPane.tabs.indexOf(srcTab) - 1)
//            print(tabPane.tabs.indexOf(srcTab) - 1)
//            print("    ")
//            println(flag)
            if (destTab.text == "教學") {
                fillTutorialTabContent(destTab)
            } else {
//                flag = flag or (1 shl tabPane.tabs.indexOf(destTab) - 1)
//                print(tabPane.tabs.indexOf(destTab) - 1)
//                print("    ")
//                println(flag)
                threadPool.shutdownNow()
                threadPool = Executors.newSingleThreadScheduledExecutor()
                val runnable = Runnable {
                    primaryStage.setOnCloseRequest {
                        threadPool.shutdownNow()
                        Platform.exit()
                    }
                    //                        println(destTab.text)
                    when (destTab.text) {
                        "大巴 150LV", "召喚終突", "方陣HL" -> renewItems(urlList[tabPane.tabs.indexOf(destTab) - 1], tabPane.tabs.indexOf(destTab) == 1)
                        "丁丁" -> renewItems(urlList[3], tabPane.tabs.indexOf(destTab) == 1, true, normalList[0])
                        "小巴" -> renewItems(urlList[3], tabPane.tabs.indexOf(destTab) == 1, true, normalList[1])
                        "麒麟/黃龍" -> renewItems(urlList[3], tabPane.tabs.indexOf(destTab) == 1, true, normalList.subList(2, 4))
                        "其他" -> renewItems(urlList[3], tabPane.tabs.indexOf(destTab) == 1, true, normalList, true)
                    }
                }
//                thread = Thread(runnable)
//                thread.isDaemon = true
//                thread.start()
                threadPool.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS)
                fillTabContent(destTab)
            }
        }

        borderPaneInit(borderPane, scene)
        borderPane.center = tabPane

        root.children.add(borderPane)
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun borderPaneInit(borderPane: BorderPane, scene: Scene) {
        borderPane.prefHeightProperty().bind(scene.heightProperty())
        borderPane.prefWidthProperty().bind(scene.widthProperty())
    }

    private fun initListView(scene: Scene) {
        listView.value.prefWidthProperty().bind(scene.widthProperty())
        listView.value.items = obList
        listView.value.setOnMouseClicked {
            val list = listView.value.selectionModel.selectedItem.split("    ")
            val item: String
            if (list.size == 2) {
                item = list[0]
            } else {
                item = list[1]
            }
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(item), null)
        }
    }

    private fun tabPaneInit(tabPane: TabPane) {
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabPane.tabs.add(buildTutorialTab())
        tabPane.tabs.add(buildNabeTab("大巴 150LV"))
        tabPane.tabs.add(buildNabeTab("召喚終突"))
        tabPane.tabs.add(buildNabeTab("方陣HL"))
        // filter from normal
        tabPane.tabs.add(buildNabeTab("丁丁"))
        tabPane.tabs.add(buildNabeTab("小巴"))
        tabPane.tabs.add(buildNabeTab("麒麟/黃龍"))
        tabPane.tabs.add(buildNabeTab("其他"))
    }

    private fun initNormalList() {
        normalList.add("グランデ")
        normalList.add("よわバハ")
        normalList.add("黒麒麟")
        normalList.add("黄龍")
    }

    private fun initUrlList() {
        /***
         * 0: baha
         * 1: summon
         * 2: 6 maguna HL
         * 3: normal
         */
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%A5%D7%A5%ED%A5%D0%A5%CFHL%B0%C7%C6%E9%CA%E7%BD%B8%C8%C4")
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%BE%A4%B4%AD%C0%D0%BA%C7%BD%AA%B2%F2%CA%FC%A5%AF%A5%A8%A5%B9%A5%C8%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4")
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F18%BF%CDHL%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4")
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%C4%CC%BE%EF%A5%DE%A5%EB%A5%C1%A5%D0%A5%C8%A5%EB%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4")
    }

    fun buildTutorialTab(): Tab {
        val tutorialTab = Tab()
        tutorialTab.text = "教學"
        fillTutorialTabContent(tutorialTab)

        return tutorialTab
    }

    fun buildNabeTab(title: String): Tab {
        val tab = Tab()
        tab.text = title

        return tab
    }

    fun fillTabContent(tab: Tab, list: ListView<String> = listView.value) {
        val hbox = HBox()
        hbox.children.add(list)
        hbox.alignment = Pos.CENTER
        tab.content = hbox
    }

    fun fillTutorialTabContent(tab: Tab) {
        val tutorialHbox = HBox()
        val tutorialText = Text()
        tutorialText.text = tutContent
        tutorialText.textAlignment = TextAlignment.CENTER
        tutorialHbox.children.add(tutorialText)
        tutorialHbox.alignment = Pos.CENTER
        tab.content = tutorialHbox
    }

    fun clearTabContent(tab: Tab) {
        tab.content = null
    }

}

