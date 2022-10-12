package com.pmv.jfilemanager

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class MainApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainApplication::class.java.getResource("main.fxml"))
        val scene = Scene(fxmlLoader.load(), 1024.0, 768.0)
        stage.title = "FileManager"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(MainApplication::class.java)
}