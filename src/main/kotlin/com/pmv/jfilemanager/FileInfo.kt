package com.pmv.jfilemanager

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneOffset

class FileInfo(path: Path) {

    enum class FileType(name: String) {
        FILE("F"), DIRECTORY("D");
    }


    var filename: String
    var type: FileType
    var size: Long
    var lastModified: LocalDateTime

    init {
        this.filename = path.fileName.toString()
        this.size = Files.size(path)
        this.type =  if (Files.isDirectory(path)) FileType.DIRECTORY  else FileType.FILE
        if (this.type == FileType.DIRECTORY) this.size = 1L
        this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0))
    }


}