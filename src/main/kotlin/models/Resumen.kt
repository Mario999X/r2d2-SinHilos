package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
@Serializable
@SerialName("resumen")
data class Resumen(
    val id: String,
    val tipo: String,
    val fecha: String,

    val maxValue: Double,
    val maxDate: String,
    val minValue: Double,
    val minDate: String,
    val avgValue: Double
)
