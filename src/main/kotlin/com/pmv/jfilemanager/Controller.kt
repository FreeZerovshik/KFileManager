package com.pmv.jfilemanager

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.VBox
import java.io.IOException
import java.nio.file.CopyOption
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileSystemException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class Controller {

    @FXML
    lateinit var leftPanel : VBox

    @FXML
    lateinit var rightPanel: VBox

    @FXML
    private fun onExitAction() {
        Platform.exit()
    }


    fun onCopyFile(actionEvent: ActionEvent) {
        val src = getSources()

        try {
            if (src?.srcPath != null && src.dstPath != null) {
                Files.copy(src.srcPath!!, src.dstPath!!)
            }
            src?.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))

        } catch (e: FileAlreadyExistsException) {
            var alert = Alert(Alert.AlertType.ERROR, "Файл ${e.message} уже сущетвует, перезапиcать?", ButtonType.YES, ButtonType.NO)
            alert.showAndWait()

            if (alert.result == ButtonType.YES) {

                try {
                    Files.copy(src?.srcPath!!, src.dstPath!!, StandardCopyOption.REPLACE_EXISTING)
                    src.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))
                } catch (e: IOException) {
                    alert = Alert(Alert.AlertType.ERROR, "Ошибка копирования файла", ButtonType.OK)
                    alert.showAndWait()
                }

            }
        }

    }

    private fun getSources(): Source?
    {
        val src = Source()

        src.leftPC = leftPanel.properties["ctrl"] as PanelController?
        src.rightPC = rightPanel.properties["ctrl"] as PanelController?

        if (src.leftPC?.getSelectedFilename() == null && src.rightPC?.getSelectedFilename() == null) {
            val alert: Alert = Alert(Alert.AlertType.ERROR, "Файл не выбран!", ButtonType.OK)
            alert.showAndWait()
            return null
        }

        if (src.leftPC?.getSelectedFilename() != null) {
            src.srcPC = src.leftPC
            src.dstPC = src.rightPC
        }

        if (src.rightPC?.getSelectedFilename() != null) {
            src.srcPC = src.rightPC
            src.dstPC = src.leftPC
        }

        src.srcPath = src.srcPC?.getCurrentPath()?.let { Paths.get(it, src.srcPC!!.getSelectedFilename()) }
        src.dstPath = src.dstPC?.getCurrentPath()?.let { Paths.get(it).resolve(src.srcPath?.fileName.toString()) }

        return src
    }

    fun onMoveFile(actionEvent: ActionEvent) {

        val src = getSources()

        try {
            if (src?.srcPath != null && src.dstPath != null) {
                Files.move(src.srcPath!!, src.dstPath!!)

                src.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))
                src.srcPC?.updateList(Paths.get(src.srcPC!!.getCurrentPath()))

           }

        } catch (e: FileAlreadyExistsException) {
            var alert = Alert(Alert.AlertType.ERROR, "Файл ${e.message} уже сущетвует, перезапиcать?", ButtonType.YES, ButtonType.NO)
            alert.showAndWait()

            if (alert.result == ButtonType.YES) {

                try {
                    Files.move(src?.srcPath!!, src.dstPath!!, StandardCopyOption.REPLACE_EXISTING)
                    src.dstPC?.updateList(Paths.get(src.dstPC!!.getCurrentPath()))
                    src.srcPC?.updateList(Paths.get(src.srcPC!!.getCurrentPath()))
                } catch (e: IOException) {
                    alert = Alert(Alert.AlertType.ERROR, "Ошибка перемещения файла", ButtonType.OK)
                    alert.showAndWait()
                }

            }
        }
    }

    fun onDeleteFile(actionEvent: ActionEvent) {
        val src = getSources()

        try {
            if (src?.srcPath != null ) {

                val alert = Alert(Alert.AlertType.CONFIRMATION, "Вы уверены что хотите удалить файл?", ButtonType.YES, ButtonType.NO)
                alert.showAndWait()

                if (alert.result == ButtonType.YES)  Files.delete(src.srcPath!!)
            }
            src?.srcPC?.updateList(Paths.get(src.srcPC!!.getCurrentPath()))

        } catch (e: IOException) {
            val alert = Alert(Alert.AlertType.ERROR, "Ошибка уадления файла ${e.message} ", ButtonType.OK)
            alert.showAndWait()
        }

    }

}