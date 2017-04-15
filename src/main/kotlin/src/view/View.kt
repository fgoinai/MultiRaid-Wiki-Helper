/*
 * File: View.kt
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

import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
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
import src.category.CatCommonFun.getCatList
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class View {
    val listView = ListView<String>()

    fun setStage(stage: Stage, scene: Scene) {
        stage.title = "Wiki募集版助手"
        stage.scene = scene
    }

    fun getScene(root: Group, obList: ObservableList<String>): Scene {
        val scene = Scene(root, 600.0, 200.0, Color.WHITE)
        initListView(scene, obList)

        return scene
    }

    fun getTabPane() : TabPane = tabPaneInit(TabPane())

    fun addSelectionModelListener(tabPane: TabPane, listener: ChangeListener<Tab>) {
        tabPane.selectionModel.selectedItemProperty().addListener(listener)
    }

    fun getBorderPane(scene: Scene, tabPane: TabPane) : BorderPane {
        val ret = BorderPane()
        borderPaneInit(ret, scene)
        ret.center = tabPane

        return ret
    }

    private fun initListView(scene: Scene, obList: ObservableList<String>) {
        listView.prefWidthProperty().bind(scene.widthProperty())
        listView.items.clear()
        listView.items = obList
        listView.setOnMouseClicked {
            try {
                val temp = listView.selectionModel.selectedItem
                if (!temp.contains("沒有場") && !temp.contains("更新中")) {
                    val list = temp.split("    ")
                    val item: String
                    if (list.size == 2) {
                        item = list[0]
                    } else {
                        item = list[1]
                    }
                    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(item), null)
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun tabPaneInit(tabPane: TabPane) : TabPane {
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabPane.tabs.add(buildTutorialTab())
        getCatList().forEach { tabPane.tabs.add(buildNabeTab(it.key)) }

        return tabPane
    }

    //TODO?
    private fun getMenuList(): HashMap<String, Array<String>> {
        val ret = HashMap<String, Array<String>>()

        ret.put("巴哈", arrayOf("大巴", "小巴"))
        ret.put("召喚終突", arrayOf("召喚終突"))
        ret.put("HL", arrayOf("方陣HL", "6人HL"))
        ret.put("四天司", arrayOf("火天司", "水天司", "土天司", "風天司"))
        ret.put("其他", arrayOf("丁丁", "麒麟/黃龍", "其他"))

        return ret
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

    private fun borderPaneInit(borderPane: BorderPane, scene: Scene) {
        borderPane.prefHeightProperty().bind(scene.heightProperty())
        borderPane.prefWidthProperty().bind(scene.widthProperty())
    }

    fun fillTutorialTabContent(tab: Tab) {
        val tutorialHbox = HBox()
        val tutorialText = Text()
        tutorialText.text = arrayOf(
                "此程式會以30秒的間隔自動更新",
                "看到有場就點一下好了,會自動複製到剪貼版",
                "彈出通知時也會自動複製",
                "如網址失效請手動更新conf.ini",
                "",
                "此程式的暗鍋場來自GBF WIKI,請小心使用"
        ).reduce { x, y -> x + "\r\n" + y }
        tutorialText.textAlignment = TextAlignment.CENTER
        tutorialHbox.children.add(tutorialText)
        tutorialHbox.alignment = Pos.CENTER
        tab.content = tutorialHbox
    }

    fun fillTabContent(tab: Tab) {
        val hbox = HBox()
        hbox.children.add(listView)
        hbox.alignment = Pos.CENTER
        tab.content = hbox
    }

    fun clearTabContent(tab: Tab) {
        tab.content = null
    }
}