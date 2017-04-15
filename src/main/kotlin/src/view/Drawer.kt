package src.view

import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.JFXHamburger
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*

class Drawer : Initializable {
    @FXML lateinit private var drawer_root: AnchorPane
    @FXML lateinit private var drawer: JFXDrawer
    @FXML lateinit private var hamburger: JFXHamburger

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        try {
            val box = FXMLLoader.load<VBox>(javaClass.classLoader.getResource("view/SidePane.fxml"))
            drawer.setSidePane(box)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val transition = HamburgerBackArrowBasicTransition(hamburger)
        transition.rate = -1.0
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, {
            transition.rate *= -1
            transition.play()

            when {
                drawer.isShown  -> drawer.close()
                drawer.isHidden -> drawer.open()
            }
        })
    }
}