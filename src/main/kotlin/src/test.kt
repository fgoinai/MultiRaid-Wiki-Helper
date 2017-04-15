package src

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class test : Application() {

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            launch(test::class.java)
        }
    }

    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.classLoader.getResource("view/Drawer.fxml"))
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}