package com.pmv.jfilemanager

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.file.*
import kotlin.jvm.Throws


class Controller {

    @FXML
    lateinit var leftPanel: VBox

    @FXML
    lateinit var rightPanel: VBox

    @FXML
    private fun onExitAction() {
        Platform.exit()
    }

    private var silentReplace = false

    @Throws(FileAlreadyExistsException::class)
    private fun actionByFile(src: Source, actionMethod: () -> Path, additionalAction: () -> Path ) {
        try {
            actionMethod()
        } catch (e: FileAlreadyExistsException) {
            if (!silentReplace) {
                val alert = Alert(
                    Alert.AlertType.ERROR,
                    "Файл ${e.message} уже сущетвует, перезапиcать?",
                    ButtonType.YES,
                    ButtonType.NO,
                    ButtonType("All", ButtonBar.ButtonData.APPLY)
                )
                alert.showAndWait()

                if (alert.result == ButtonType.YES) {
                    actionFilesWithException(src, additionalAction())
                } else if (alert.result.text == "All") {
                    silentReplace = true
                }
            } else {
                actionFilesWithException(src, additionalAction())
            }
        }
    }

    @Throws(FileAlreadyExistsException::class)
    private fun actionFilesWithException(src: Source, actionMethod: Path)  = try {
            actionMethod
            src.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))
        } catch (e: IOException) {
            Alert(Alert.AlertType.ERROR, "Ошибка копирования файла", ButtonType.OK).showAndWait()
        }


    private fun getSources(): Source? {
        val src =
            Source(leftPanel.properties["ctrl"] as PanelController, rightPanel.properties["ctrl"] as PanelController)

        if (src.leftPC?.getSelectedFiles().isNullOrEmpty() && src.rightPC?.getSelectedFiles().isNullOrEmpty()) {
            val alert: Alert = Alert(Alert.AlertType.ERROR, "Файл не выбран!", ButtonType.OK)
            alert.showAndWait()
            return null
        }

        if (!src.leftPC?.getSelectedFiles().isNullOrEmpty()) {
            src.srcPC = src.leftPC
            src.dstPC = src.rightPC
        }

        if (!src.rightPC?.getSelectedFiles().isNullOrEmpty()) {
            src.srcPC = src.rightPC
            src.dstPC = src.leftPC
        }

        src.srcPath = src.srcPC?.getCurrentPath()?.let { Paths.get(it) }
        src.dstPath = src.dstPC?.getCurrentPath()?.let { Paths.get(it) }

        src.selectedFiles = src.srcPC?.getSelectedFiles()

        return src
    }

    @Throws(IOException::class)
    fun onCopyFiles(actionEvent: ActionEvent) {
        val src = getSources()

        if (src?.srcPath != null && src.dstPath != null) {

            src.selectedFiles?.forEach {
                val srcFile  = src.srcPath!!.resolve(it.filename)
                val dstFile = src.dstPath!!.resolve(it.filename)

                actionByFile(src,
                 { Files.copy(srcFile, dstFile) }
                 , {Files.copy(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING )})
            }

        }
        src?.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))
    }

    fun onMoveFile(actionEvent: ActionEvent) {
        val src = getSources()

        if (src?.srcPath != null && src.dstPath != null) {
            src.selectedFiles?.forEach {
                val srcFile  = src.srcPath!!.resolve(it.filename)
                val dstFile = src.dstPath!!.resolve(it.filename)

                actionByFile(src,
                    { Files.move(srcFile, dstFile) }
                    , {Files.move(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING )})
            }
        }
        src?.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))
    }

    fun onDeleteFile(actionEvent: ActionEvent) {
        val src = getSources()

        if (src?.srcPath != null && src.dstPath != null) {
            val alert = Alert(
                Alert.AlertType.CONFIRMATION,
                "Вы уверены что хотите удалить ${src.selectedFiles?.count()} файлов?",
                ButtonType.YES,
                ButtonType.NO
            )
            alert.showAndWait()

            if (alert.result == ButtonType.YES) {
                src.selectedFiles?.forEach {
                    try {
                        Files.delete(src.srcPath!!.resolve(it.filename))
                    } catch (e: IOException) {
                        Alert(Alert.AlertType.ERROR, "Ошибка удаления файла ${e.message} ", ButtonType.OK).showAndWait()
                    }
                }
            }
        }
        src?.srcPC?.updateList(Paths.get(src.srcPC!!.getCurrentPath()))

    }

    fun onOpenFile(actionEvent: ActionEvent) {
        val src = getSources()

        try {
            if (src?.srcPath != null) {
                val ft = src.srcPC?.filesTable

                if (!ft?.selectionModel?.isEmpty!!) {
                    val path = ft.selectionModel.let {
                        Paths.get(src.srcPC?.pathField?.text!!).resolve(it.selectedItem.filename)
                    }

                    if (!Files.isDirectory(path)) {
                        val file = File(path.toString())
                        Desktop.getDesktop().open(file)
                    }
                }

            }
        } catch (e: IOException) {
            Alert(Alert.AlertType.ERROR, "Ошибка открытия файла ${e.message} ", ButtonType.OK).showAndWait()
        }

    }

}