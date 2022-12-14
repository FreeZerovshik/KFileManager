package com.pmv.jfilemanager

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import java.awt.Desktop
import java.io.File
import java.net.URL
import java.nio.file.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

class PanelController: Initializable {

    @FXML
    lateinit var filesTable: TableView<FileInfo>

    @FXML
    lateinit var disksBox: ComboBox<String>

    @FXML
    lateinit var pathField: TextField

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        //create columns in Table
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
        filesTable.selectionModel.selectionMode = SelectionMode.MULTIPLE

        // add disk to ComboBox
        disksBox.items.clear()
        FileSystems.getDefault().rootDirectories.forEach {
            disksBox.items.add(it.toString())
        }
        disksBox.selectionModel.select(0)

        // двойной клик
        filesTable.setOnMouseClicked() { mouseEvent ->
            if (mouseEvent.clickCount == 2) {
                if (!filesTable.selectionModel.isEmpty) {
                    val path =
                        filesTable.selectionModel.let { Paths.get(pathField.text).resolve(it.selectedItem.filename) }

                    if (Files.isDirectory(path)) { // переход если это каталог
                        updateList(path)
                    } else { // если файл  то открываем
                        val file = File(path.toString())
                        val dt = Desktop.getDesktop().open(file)
                    }
                }
           }
        }

        //update info in tables
        updateList(Paths.get("."))
    }

     fun updateList(path: Path) {
        pathField.text = path.normalize().toAbsolutePath().toString()

        filesTable.items.clear()

        val fileList = mutableListOf<FileInfo>()
        Files.list(path).forEach {
            fileList.add(FileInfo(it))
        }

        filesTable.items.addAll(fileList)
        filesTable.sort()
        filesTable.refresh()
    }

    fun onPathUpClick(actionEvent: ActionEvent) {
        val upperPath = Paths.get(pathField.text).parent

        if (upperPath != null) {
            updateList(upperPath)
        }
    }

    fun onSelectDisk(actionEvent: ActionEvent) {
        val element = actionEvent.source as ComboBox<String>
        updateList(Paths.get(element.selectionModel.selectedItem))
    }

    fun getSelectedFilename() = if (!filesTable.isFocused) null else filesTable.selectionModel.selectedItem.filename

    fun getSelectedFiles() = if (!filesTable.isFocused) null else filesTable.selectionModel.selectedItems


    fun getCurrentPath() =  pathField.text
}