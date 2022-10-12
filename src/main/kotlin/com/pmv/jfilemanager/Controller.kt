package com.pmv.jfilemanager

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors


class Controller {

    @FXML
    private fun onExitAction() {
        Platform.exit()
    }

}