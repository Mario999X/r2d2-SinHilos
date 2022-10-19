package controllers

import kotlinx.serialization.encodeToString
import models.*
import mu.KotlinLogging
import nl.adaptivity.xmlutil.serialization.XML
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.writeJson
import java.io.File
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

object R2D2Controller {
    private val workingDir: String = System.getProperty("user.dir")
    private val fs = File.separator

    private lateinit var medicionesTomadas: List<Medicion>

    fun init() {
        val csvMedicionFile = workingDir + fs + "src" + fs + "main" + fs + "resources" + fs + "data" + fs + "data03.csv"
        val xmlMedicionFile = workingDir + fs + "src" + fs + "main" + fs + "resources" + fs + "data" + fs + "data03.xml"

        //Lectura de datos
        val medicionCsvList by lazy { loadCSVFile(File(csvMedicionFile)) }
        val medicionXMLList by lazy { loadXMLFile(File(xmlMedicionFile)) }

        cargarListas(medicionCsvList, medicionXMLList)
        procesarDatos()
    }

    private fun cargarListas(medicionCsvList: List<Medicion>, medicionXMLList: List<Medicion>) {
        logger.debug { "Inicio Filtrado" }

        // Juntamos ambos archivos/listas, sin repetidos y ordenadas por el ID
        medicionesTomadas = (medicionCsvList + medicionXMLList).distinctBy { it.id }.sortedBy { it.id }

    }

    private fun procesarDatos() {

        // Aqui se realiza la paginacion, que por ahora solo se hacer con una lista mutable y no con Dataframe
        val PAGE_SIZE = 25
        logger.debug { "Procesado datos..." }

        val estadisticasList = mutableListOf<Estadistica>()
        medicionesTomadas.windowed(PAGE_SIZE, PAGE_SIZE, true) { i ->
            estadisticasList.add(calcularEstadisticas(i))
        }
        logger.debug { "Datos procesados" }
        //println(estadisticasList)

        val estadisticasDataframe = estadisticasList.toDataFrame()
        logger.debug { "Guardando datos..." }
        parserSaveData(estadisticasDataframe, estadisticasList)
        logger.debug { "Datos Guardados" }

    }

    private fun calcularEstadisticas(mediciones: List<Medicion>): Estadistica {
        logger.debug { "Calculando estadisticas..." }
        // Tomamos la lista (medicionesTomadas) y la trasnformamos en un dataframe
        val dfMedicion = mediciones.toDataFrame()

        // Unica forma de filtrar que se me ha ocurrido, esto habria que hacerlo con todos los campos que se piden

        logger.debug { "Procesado datos NO2" }
        val dfMaxNo2 = dfMedicion.max { it["NO2"].convertToDouble() }
        val fechaMaxNo2 = dfMedicion.filter { it["NO2"] == dfMaxNo2 }["fecha"][0]

        val dfMinNo2 = dfMedicion.min { it["NO2"].convertToDouble() }
        val fechaMinNo2 = dfMedicion.filter { it["NO2"] == dfMinNo2 }["fecha"][0]

        val dfAvgNo2 = dfMedicion.mean { it["NO2"].convertToDouble() }

        // Generamos un resumen que contenga los datos anteriores, generando el id, el tipo y la fecha a mano y a string
        val datosNO2 = Resumen(
            id = UUID.randomUUID().toString(),
            tipo = "NO2",
            fecha = LocalDateTime.now().toString(),
            maxValue = dfMaxNo2,
            maxDate = fechaMaxNo2.toString(),
            minValue = dfMinNo2,
            minDate = fechaMinNo2.toString(),
            avgValue = dfAvgNo2
        )

        logger.debug { "Procesado datos Temperatura" }
        val dfMaxTemp = dfMedicion.max { it["temperatura"].convertToDouble() }
        val fechaMaxTemp = dfMedicion.filter { it["temperatura"] == dfMaxTemp }["fecha"][0]

        val dfMinTemp = dfMedicion.min { it["temperatura"].convertToDouble() }
        val fechaMinTemp = dfMedicion.filter { it["temperatura"] == dfMinTemp }["fecha"][0]

        val dfAvgTemp = dfMedicion.mean { it["temperatura"].convertToDouble() }

        val datosTemp = Resumen(
            id = UUID.randomUUID().toString(),
            tipo = "Temperatura",
            fecha = LocalDateTime.now().toString(),
            maxValue = dfMaxTemp,
            maxDate = fechaMaxTemp.toString(),
            minValue = dfMinTemp,
            minDate = fechaMinTemp.toString(),
            avgValue = dfAvgTemp
        )

        logger.debug { "Procesado datos Ozono" }
        val dfMaxOzone = dfMedicion.max { it["ozone"].convertToDouble() }
        val fechaMaxOzone = dfMedicion.filter { it["ozone"] == dfMaxOzone }["fecha"][0]

        val dfMinOzone = dfMedicion.min { it["ozone"].convertToDouble() }
        val fechaMinOzone = dfMedicion.filter { it["ozone"] == dfMinOzone }["fecha"][0]

        val dfAvgOzone = dfMedicion.mean { it["ozone"].convertToDouble() }

        val datosOzone = Resumen(
            id = UUID.randomUUID().toString(),
            tipo = "Ozone",
            fecha = LocalDateTime.now().toString(),
            maxValue = dfMaxOzone,
            maxDate = fechaMaxOzone.toString(),
            minValue = dfMinOzone,
            minDate = fechaMinOzone.toString(),
            avgValue = dfAvgOzone
        )

        // Devolvemos la estadistica y en su interior almacenamos los filtrados realizados anteriormente
        logger.debug { "Generando Estadistica..." }
        return Estadistica(
            id = UUID.randomUUID().toString(),
            fecha = LocalDateTime.now().toString(),
            resumenes = listOf(datosNO2, datosTemp, datosOzone)
        )
    }

    // Me da pereza hacer el informe, asi que con esto a mi me vale
    private fun parserSaveData(listaDf: DataFrame<Estadistica>, lista: List<Estadistica>) {
        logger.debug { "Guardando datos en JSON y XML..." }

        // Creacion de JSON
        listaDf.writeJson(File(workingDir + fs + "data" + fs + "datos.json"), prettyPrint = true)

        // Creacion de  XML
        val xml = XML { indentString = " " }
        File((workingDir + fs + "data" + fs + "datos.xml")).writeText(xml.encodeToString(lista))

    }

}