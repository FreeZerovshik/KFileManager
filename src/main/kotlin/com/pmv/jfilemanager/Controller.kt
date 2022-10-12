package com.pmv.jfilemanager

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.VBox
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths



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

        } catch (e: IOException) {
            println(e.message)
            val alert = Alert(Alert.AlertType.ERROR, "Ошибка копирования файла!", ButtonType.OK)
            alert.showAndWait()
        }

    }

}