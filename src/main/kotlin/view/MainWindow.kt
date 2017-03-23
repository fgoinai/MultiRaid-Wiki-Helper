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

import category.*
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
        private var listView = lazy { ListView<String>() }
        private val obList = FXCollections.observableArrayList<String>()
        private var topMsg = ""
        private var threadPool = Executors.newSingleThreadScheduledExecutor()
        val RENEW_INTERVAL = 30L

        private val tutContent = "此程式會以30秒的間隔自動更新\r\n" +
                "看到有場就點一下好了,會自動複製到剪貼版\r\n" +
                "彈出通知時也會自動複製\r\n" +
                "\r\n" +
                "此程式的暗鍋場來自GBF WIKI,請小心使用"

        @JvmStatic fun main(args: Array<String>) {
            launch(MainWindow::class.java)
        }

        private fun renewItems(cat: ICategory) {
            Platform.runLater { obList.clear() }
            val list = Yaminabe().getList(cat)
            if (list == null) {
                Platform.runLater { obList.add("沒有場") }
                return
            }

            Platform.runLater {
                obList.addAll(list as ArrayList<out String>)
                if (topMsg != obList[0]) {
                    topMsg = obList[0]
                    showNoti(topMsg, topMsg.split(" ").filter { it.matches(Regex("\\w{8}")) }[0])
                }
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
            }
            instance?.setOnHideNotification {
                instance?.stop()
            }
            instance?.isAlwaysOnTop = true
            instance?.notify(noti)
        }
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "暗鍋助手"
        val root = Group()
        val scene = Scene(root, 600.0, 250.0, Color.WHITE)
        val tabPane = TabPane()
        val borderPane = BorderPane()

        initListView(scene)
        tabPaneInit(tabPane)

        tabPane.selectionModel.selectedItemProperty().addListener { observableValue, srcTab, destTab ->
            tabPane.tabs
                    .filter { it != destTab }
                    .forEach { clearTabContent(it) }
            if (destTab.text == "教學") {
                fillTutorialTabContent(destTab)
            } else {
                threadPool.shutdownNow()
                threadPool = Executors.newSingleThreadScheduledExecutor()
                val runnable = Runnable {
                    primaryStage.setOnCloseRequest {
                        threadPool.shutdownNow()
                        Platform.exit()
                    }

                    renewItems(getCatList()[destTab.text]!!)
                }
                threadPool.scheduleAtFixedRate(runnable, 0, RENEW_INTERVAL, TimeUnit.SECONDS)
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

    private fun getCatList(): LinkedHashMap<String, ICategory> {
        val ret = LinkedHashMap<String, ICategory>()

        ret.put("大巴 150LV", BahamutCat())
        ret.put("召喚終突", NormalSummonCat())
        ret.put("方陣HL", MagunaHlCat())
        ret.put("6人HL", SixManHlCat())

        ret.put("火天司", FourAngelCat(FourAngelCat.Companion.Types.MICHAEL))
        ret.put("水天司", FourAngelCat(FourAngelCat.Companion.Types.GABRIEL))
        ret.put("土天司", FourAngelCat(FourAngelCat.Companion.Types.URIEL))
        ret.put("風天司", FourAngelCat(FourAngelCat.Companion.Types.RAPHAEL))

        ret.put("丁丁", NormalRaidCat(NormalRaidCat.Companion.Types.GRANDEE))
        ret.put("小巴", NormalRaidCat(NormalRaidCat.Companion.Types.WEAK_BAHA))
        ret.put("麒麟/黃龍", NormalRaidCat(NormalRaidCat.Companion.Types.KIRIN_RYU))
        ret.put("其他", NormalRaidCat(NormalRaidCat.Companion.Types.OTHER))


        return ret
    }

    private fun tabPaneInit(tabPane: TabPane) {
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabPane.tabs.add(buildTutorialTab())
        getCatList().forEach { tabPane.tabs.add(buildNabeTab(it.key)) }
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

