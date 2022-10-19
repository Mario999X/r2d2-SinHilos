import controllers.DirController
import controllers.R2D2Controller
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

fun main() {

    logger.debug { "Empezando APP" }

    measureTimeMillis {
        DirController.init()
        R2D2Controller.init()
    }.also {
        logger.debug { "Tiempo: ${it}ms" }
    }

    logger.debug { "Finalizando APP" }
}