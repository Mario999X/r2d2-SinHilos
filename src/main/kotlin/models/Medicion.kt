package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.io.File
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
@Serializable
@SerialName("Medicion")
data class Medicion(
    val id: Int,
    val fecha: String,
    val tipo: String,
    val NO2: Double,
    val temperatura: Double,
    val CO: Double,
    val ozone: Double,
) {
    companion object
}

fun loadCSVFile(csvFile: File): List<Medicion> {
    val mediciones: List<Medicion> = csvFile.readLines().drop(1)
        .map { it.split(",") }
        .map {
            it.map { campo -> campo.trim() }
            Medicion(
                id = it[0].toInt(),
                fecha = it[1],
                tipo = it[2],
                NO2 = it[5].toDouble(),
                temperatura = it[9].toDouble(),
                CO = it[10].toDouble(),
                ozone = it[6].toDouble(),
            )
        }
    return mediciones
}

fun loadXMLFile(xmlFile: File): List<Medicion> {
    val sax = SAXBuilder()
    val doc: Document = sax.build(xmlFile)
    return doc.rootElement.getChild("resources")
        .getChildren("resource")
        .map { it.toDto() }
}

private fun Element.toDto(): Medicion {
    /*        <resource>
    <str name="ayto:particles"/>
    <str name="ayto:NO2">116.0</str>
    <str name="ayto:type">AirQualityObserved</str>
    <str name="ayto:latitude">43.449</str>
    <str name="ayto:temperature">22.9</str>
    <str name="ayto:altitude"/>
    <str name="ayto:speed"/>
    <str name="ayto:CO">0.1</str>
    <date name="dc:modified">2021-08-27T07:47:39Z</date>
    <str name="dc:identifier">3047</str>
    <str name="ayto:longitude">-3.8677</str>
    <str name="ayto:odometer"/>
    <str name="ayto:course"/>
    <str name="ayto:ozone">120.0</str>
    <str name="uri">http://datos.santander.es/api/datos/sensores_smart_mobile/3047.xml</str>
    </resource>*/
    val strs = this.getChildren("str")
    return Medicion(
        NO2 = strs[1].text.toDouble(),
        tipo = strs[2].text,
        temperatura = strs[4].text.toDouble(),
        CO = strs[7].text.toDouble(),
        id = strs[8].text.toInt(),
        ozone = strs[12].text.toDouble(),
        fecha = getChild("date").text
    )
}
