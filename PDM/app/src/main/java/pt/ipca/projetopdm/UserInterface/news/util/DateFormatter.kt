package pt.ipca.projetopdm.UserInterface.news.util

import android.os.Build
import androidx.compose.runtime.Composable
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


@Composable
fun DateFormatter(

    inputDateTime:String?
):String {

    val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val outputFormatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.getDefault())

    val dateString = try {

        val DateTime = OffsetDateTime.parse(inputDateTime, inputFormatter)
        DateTime.format(outputFormatter)
    } catch(e: Exception) {

        ""
    }

    return dateString
}