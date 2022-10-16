package com.pmv.jfilemanager

import javafx.collections.ObservableList
import java.nio.file.Path

class Source {

    var leftPC: PanelController? = null
    var rightPC: PanelController? = null
    var srcPC: PanelController? = null
    var dstPC: PanelController? = null
    var srcPath: Path? = null
    var dstPath: Path? = null
    var selectedFiles: ObservableList<FileInfo>? = null

    constructor()
    constructor (leftPC: PanelController, rightPC: PanelController ) {
        this.leftPC = leftPC
        this.rightPC = rightPC
    }
}