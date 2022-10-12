package com.pmv.jfilemanager

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

class PanelController: Initializable {

    @FXML
    lateinit var filesTable: TableView<FileInfo>

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val fileTypeColumn = TableColumn<FileInfo, String>()
        fileTypeColumn.setCellValueFactory { param -> SimpleStringProperty(param.value.type.name) }
        fileTypeColumn.prefWidth = 24.0

        val fileNameColumn = TableColumn<FileInfo, String>("Имя файла")
        fileNameColumn.setCellValueFactory { param -> SimpleStringProperty(param.value.filename) }
        fileNameColumn.prefWidth = 240.0

        val fileSizeColumn = TableColumn<FileInfo, Long>("Размер")
        fileSizeColumn.setCellValueFactory { param -> SimpleObjectProperty(param.value.size) }
        fileSizeColumn.prefWidth = 120.0
        fileSizeColumn.setCellFactory {
            object : TableCell<FileInfo, Long>() {
                public override fun updateItem(item: Long?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (!empty) {
                        var text = String.format("%,d bytes", item )
                        if (item == 1L) {
                            text = "[DIR]"
                        }
                        this.text = text
                    }
                }
            }
        }

        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val fileDateColumn = TableColumn<FileInfo, String>("Дата изменений")
        fileDateColumn.setCellValueFactory { param -> SimpleStringProperty(param.value.lastModified.format(df)) }
        fileSizeColumn.prefWidth = 120.0

        filesTable.columns.addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn)
        filesTable.sortOrder.add(fileTypeColumn)

        updateList(Paths.get("."))
    }

    private fun updateList(path: Path) {
        filesTable.items.clear()
        filesTable.items.addAll(Files.list(path).map { f -> FileInfo(f) }.collect(Collectors.toList()))
        filesTable.sort()
    }
}