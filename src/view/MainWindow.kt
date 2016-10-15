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

import javafx.application.Application
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
import lib.Yaminabe
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

class MainWindow : Application() {

    companion object {
        private var thread = Thread()
        private val urlList = ArrayList<String>()
        private var listView = lazy { ListView<String>() }
        private val obList = FXCollections.observableArrayList<String>()

        private val tutContent = "此程式會以30秒的間隔自動更新\r\n" +
                "看到有場就點一下好了,會自動複製到剪貼版\r\n" +
                "\r\n" +
                "此程式的暗鍋場來自GBF WIKI,請小心使用"

        @JvmStatic fun main(args: Array<String>) {
            launch(MainWindow::class.java)
        }

        private fun renewItems(path: String, isBaha: Boolean) {
            obList.removeAll(obList)
            obList.addAll(Yaminabe().getList(path, isBaha) as ArrayList)
            System.gc()
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
        tabPaneInit(tabPane)

        tabPane.selectionModel.selectedItemProperty().addListener { observableValue, srcTab, destTab ->
            for (i in tabPane.tabs) {
                if (i != destTab) {
                    clearTabContent(i)
                }
            }
            thread.interrupt()
            if (destTab.text == "教學") {
                fillTutorialTabContent(destTab)
            } else {
                val runnable = Runnable {
                    while (true) {
                        println(destTab.text)
                        renewItems(urlList[tabPane.tabs.indexOf(destTab) - 1], tabPane.tabs.indexOf(destTab) == 1)
                        Thread.sleep(30 * 1000)
                    }
                }
                thread = Thread(runnable)
                thread.isDaemon = true
                thread.start()
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
    }

    private fun initUrlList() {
        /***
         * 0: baha
         * 1: summon
         * 2: 6 maguna HL
         */
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%A5%D7%A5%ED%A5%D0%A5%CFHL%B0%C7%C6%E9%CA%E7%BD%B8%C8%C4")
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%BE%A4%B4%AD%C0%D0%BA%C7%BD%AA%B2%F2%CA%FC%A5%AF%A5%A8%A5%B9%A5%C8%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4")
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F18%BF%CDHL%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4")
    }

    fun buildTutorialTab () : Tab {
        val tutorialTab = Tab()
        tutorialTab.text = "教學"
        fillTutorialTabContent(tutorialTab)

        return tutorialTab
    }

    fun buildNabeTab (title: String) : Tab {
        val tab = Tab()
        tab.text = title

        return tab
    }

    fun fillTabContent (tab: Tab, list: ListView<String> = listView.value) {
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

    fun clearTabContent (tab: Tab) {
        tab.content = null
    }

}

