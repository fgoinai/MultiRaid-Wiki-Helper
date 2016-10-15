package view

import javafx.application.Application
import javafx.collections.FXCollections
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
import lib.Yaminabe
import java.util.*

class MainWindow : Application() {

    companion object {
        private val thread = Thread()
        private val urlList = ArrayList<String>()
        private var listView = ListView<String>()

        private val tutContent = "此程式會以30秒的間隔自動更新\r\n" +
                "看到有場就點一下好了,會自動複製到剪貼版\r\n" +
                "\r\n" +
                "此程式的暗鍋場來自GBF WIKI,請小心使用"

        @JvmStatic fun main(args: Array<String>) {
            launch(MainWindow::class.java)
        }

        private fun items(path: String, isBaha: Boolean) :ObservableList<String> {
            val list = Yaminabe().getList(path, isBaha)
            return FXCollections.observableArrayList<String>(list)
        }
    }

    override fun start(primaryStage: Stage) {
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%A5%D7%A5%ED%A5%D0%A5%CFHL%B0%C7%C6%E9%CA%E7%BD%B8%C8%C4")
        urlList.add("http://gbf-wiki.com/index.php?%A5%B3%A5%E1%A5%F3%A5%C8%2F%BE%A4%B4%AD%C0%D0%BA%C7%BD%AA%B2%F2%CA%FC%A5%AF%A5%A8%A5%B9%A5%C8%B5%DF%B1%E7%CA%E7%BD%B8%C8%C4")

        primaryStage.title = "暗鍋助手"
        val root = Group()
        val scene = Scene(root, 400.0, 250.0, Color.WHITE)
        val tabPane = TabPane()
        val borderPane = BorderPane()

        listView.prefWidthProperty().bind(scene.widthProperty())

        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabPane.tabs.add(buildTutorialTab())
        tabPane.tabs.add(buildNabeTab("大巴 150LV"))
        tabPane.tabs.add(buildNabeTab("其他"))
//        fillTabContent(tabPane.tabs[1])
//        fillTabContent(tabPane.tabs[2], renewList(scene, items(urlList[1], false)))

        tabPane.selectionModel.selectedItemProperty().addListener { observableValue, srcTab, destTab ->
            for (i in tabPane.tabs) {
                if (i != destTab) {
                    clearTabContent(i)
                }
            }
            if (destTab.text == "教學") {
                fillTutorialTabContent(destTab)
            } else {
                renewList(items(urlList[tabPane.tabs.indexOf(destTab) - 1], tabPane.tabs.indexOf(destTab) == 1))
                fillTabContent(destTab)
            }
        }

        // match parent
        borderPane.prefHeightProperty().bind(scene.heightProperty())
        borderPane.prefWidthProperty().bind(scene.widthProperty())

        borderPane.center = tabPane
        root.children.add(borderPane)
        primaryStage.scene = scene
        primaryStage.show()
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

    fun fillTabContent (tab: Tab, list: ListView<String> = listView) {
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

    fun renewList(listItems: ObservableList<String>) {
        listView.items = listItems
    }

}

