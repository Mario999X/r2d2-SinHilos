package controllers

import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

object DirController {
    private val workingDir: String = System.getProperty("user.dir")
    private val fs = File.separator

    fun init() {

        val pathData = Paths.get(workingDir + fs + "data")

        if (Files.isDirectory(pathData) && Files.exists(pathData)) {
            logger.debug { "Carpeta de data comprobada... OK" }
        } else {
            logger.debug { "Carpeta de data no existe. Creando..." }
            Files.createDirectory(pathData)
            logger.debug { "Carpeta de data creada..." }
        }
    }
}