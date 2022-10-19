package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
@Serializable
@SerialName("estadistica")
data class Estadistica(
    val id: String,
    val fecha: String,

    @XmlElement(true)
    val resumenes: List<Resumen> = listOf()
)
