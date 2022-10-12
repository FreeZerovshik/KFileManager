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
        val leftPC = leftPanel.properties["ctrl"] as PanelController?
        val rightPC = rightPanel.properties["ctrl"] as PanelController?

        if (leftPC?.getSelectedFilename() == null && rightPC?.getSelectedFilename() == null) {
            val alert: Alert = Alert(Alert.AlertType.ERROR, "Файл не выбран!", ButtonType.OK)
            alert.showAndWait()
            return
        }

        var srcPC: PanelController? = null
        var dstPC: PanelController? = null

        if (leftPC?.getSelectedFilename() != null) {
            srcPC = leftPC
            dstPC = rightPC
        }

        if (rightPC?.getSelectedFilename() != null) {
            srcPC = rightPC
            dstPC = leftPC
        }

        val srcPath = srcPC?.getCurrentPath()?.let { Paths.get(it, srcPC.getSelectedFilename()) }
        val dstPath = dstPC?.getCurrentPath()?.let { Paths.get(it).resolve(srcPath?.fileName.toString()) }

        try {
            if (srcPath != null && dstPath != null) {
                Files.copy(srcPath, dstPath)
            }
            dstPC?.updateList(Paths.get(dstPC.getCurrentPath()))

        } catch (e: FileAlreadyExistsException) {
            var alert = Alert(Alert.AlertType.ERROR, "Файл ${e.message} уже сущетвует, перезапиать", ButtonType.YES, ButtonType.NO)
            alert.showAndWait()

            if (alert.result == ButtonType.YES) {

                try {
                    Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING)
                    dstPC?.updateList(Paths.get(dstPC.getCurrentPath()))
                } catch (e: IOException) {
                    alert = Alert(Alert.AlertType.ERROR, "Ошибка копирования файла", ButtonType.OK)
                    alert.showAndWait()
                }

            }
        }

    }

    fun onMoveFile(actionEvent: ActionEvent) {
        val leftPC = leftPanel.properties["ctrl"] as PanelController?
        val rightPC = rightPanel.properties["ctrl"] as PanelController?

        if (leftPC?.getSelectedFilename() == null && rightPC?.getSelectedFilename() == null) {
            val alert: Alert = Alert(Alert.AlertType.ERROR, "Файл не выбран!", ButtonType.OK)
            alert.showAndWait()
            return
        }

        var srcPC: PanelController? = null
        var dstPC: PanelController? = null

        if (leftPC?.getSelectedFilename() != null) {
            srcPC = leftPC
            dstPC = rightPC
        }

        if (rightPC?.getSelectedFilename() != null) {
            srcPC = rightPC
            dstPC = leftPC
        }

        val srcPath = srcPC?.getCurrentPath()?.let { Paths.get(it, srcPC.getSelectedFilename()) }
        val dstPath = dstPC?.getCurrentPath()?.let { Paths.get(it).resolve(srcPath?.fileName.toString()) }

        try {
            if (srcPath != null && dstPath != null) {
                Files.move(srcPath, dstPath)
            }
            dstPC?.updateList(Paths.get(dstPC.getCurrentPath()))
            srcPC?.updateList(Paths.get(srcPC.getCurrentPath()))

        } catch (e: FileAlreadyExistsException) {
            var alert = Alert(Alert.AlertType.ERROR, "Файл ${e.message} уже сущетвует, перезапиать", ButtonType.YES, ButtonType.NO)
            alert.showAndWait()

            if (alert.result == ButtonType.YES) {

                try {
                    Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING)
                    dstPC?.updateList(Paths.get(dstPC.getCurrentPath()))
                    srcPC?.updateList(Paths.get(srcPC.getCurrentPath()))
                } catch (e: IOException) {
                    alert = Alert(Alert.AlertType.ERROR, "Ошибка перемещения файла", ButtonType.OK)
                    alert.showAndWait()
                }

            }
        }
    }

    fun onDeleteFile(actionEvent: ActionEvent) {
        val leftPC = leftPanel.properties["ctrl"] as PanelController?
        val rightPC = rightPanel.properties["ctrl"] as PanelController?

        if (leftPC?.getSelectedFilename() == null && rightPC?.getSelectedFilename() == null) {
            val alert: Alert = Alert(Alert.AlertType.ERROR, "Файл не выбран!", ButtonType.OK)
            alert.showAndWait()
            return
        }

        var srcPC: PanelController? = null

        if (leftPC?.getSelectedFilename() != null) {
            srcPC = leftPC
        }

        if (rightPC?.getSelectedFilename() != null) {
            srcPC = rightPC
        }

        val srcPath = srcPC?.getCurrentPath()?.let { Paths.get(it, srcPC.getSelectedFilename()) }

        try {
            if (srcPath != null ) {

                val alert = Alert(Alert.AlertType.CONFIRMATION, "Вы уверены что хотите удалить файл?", ButtonType.YES, ButtonType.NO)
                alert.showAndWait()

                if (alert.result == ButtonType.YES)  Files.delete(srcPath)
            }
            srcPC?.updateList(Paths.get(srcPC.getCurrentPath()))

        } catch (e: IOException) {
            val alert = Alert(Alert.AlertType.ERROR, "Ошибка уадления файла ${e.message} ", ButtonType.OK)
            alert.showAndWait()
        }

    }

}