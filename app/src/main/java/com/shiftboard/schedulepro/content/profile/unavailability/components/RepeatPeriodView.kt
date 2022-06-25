package com.shiftboard.schedulepro.content.profile.unavailability.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.theme.Primary
import com.shiftboard.schedulepro.core.network.model.profile.PatternType
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*

@Composable
fun RepeatPeriodView(
    patternType: PatternType,
    patternInterval: Int = 0,
    dateOfTheYear: LocalDate? = null,
    dateOfTheMonth: Int? = null
) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        val dayOfMonth =
            if (patternType == PatternType.Monthly) dateOfTheMonth else if (patternType == PatternType.Yearly) dateOfTheYear?.dayOfMonth else null
        val month = if (patternType == PatternType.Yearly) dateOfTheYear?.month else null
        dayOfMonth?.let {
            Text(text = "$it${ordinalSuffix(it, context)}", fontSize = 12.sp)
        }
        month?.let {
            Text(
                text = " ${it.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH)}",
                fontSize = 12.sp
            )
        }
        if (dayOfMonth != null) {
            Text(text = stringResource(R.string.space_of_space), fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Primary.copy(alpha = 0.15f)),
        ) {
            val period = if (patternType == PatternType.Daily) if (dateOfTheMonth != 1) "$dateOfTheMonth${ordinalSuffix(dateOfTheMonth?:1, context)}" else "" else {
                if (patternInterval != 1) "$patternInterval${ordinalSuffix(patternInterval, context)}" else ""
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(3.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.swap),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "",
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(2.dp))
                val stringPatternType = when (patternType) {
                    PatternType.Daily -> stringResource(id = R.string.day)
                    PatternType.Weekly -> stringResource(id = R.string.week)
                    PatternType.Monthly -> stringResource(id = R.string.month)
                    else -> stringResource(id = R.string.month)
                }
                Text(
                    text = "${stringResource(id = R.string.every)} $period ${stringPatternType.lowercase(Locale.getDefault())}",
                    color = Primary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}

fun ordinalSuffix(number: Int, context: Context): String {
    return if (number % 100 in 11..13) context.getString(R.string.th)
    else when (number % 10) {
        1 -> context.getString(R.string.st)
        2 -> context.getString(R.string.nd)
        3 -> context.getString(R.string.rd)
        else -> context.getString(R.string.th)
    }
}