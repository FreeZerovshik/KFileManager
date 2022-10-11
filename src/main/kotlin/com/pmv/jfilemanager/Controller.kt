package com.pmv.jfilemanager

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors


class Controller: Initializable {

    @FXML
    lateinit var filesTable: TableView<FileInfo>

    @FXML
    private fun onExitAction() {
        Platform.exit()
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val fileTypeColumn = TableColumn<FileInfo, String>()
        fileTypeColumn.setCellValueFactory { param -> SimpleStringProperty(param.value.type.name) }
        fileTypeColumn.prefWidth = 24.0

        val fileNameColumn = TableColumn<FileInfo, String>("Имя файла")
        fileNameColumn.setCellValueFactory { param -> SimpleStringProperty(param.value.filename) }
        fileNameColumn.prefWidth = 240.0

        filesTable.columns.addAll(fileTypeColumn, fileNameColumn)

        updateList(Paths.get("."))
    }

    fun updateList(path: Path) {
        filesTable.items.clear()
        filesTable.items.addAll(Files.list(path).map { f -> FileInfo(f) }.collect(Collectors.toList()))
        filesTable.sort()
    }
}