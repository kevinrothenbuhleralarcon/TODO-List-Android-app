package ch.kra.todo.core.presentation.ui.shared_composable


import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.kra.todo.R
import ch.kra.todo.core.DateFormatUtil
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DatePicker(
    date: String?,
    onUpdateDate: (date: LocalDateTime?) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .padding(top = 10.dp)
            .border(
                0.5.dp,
                Color(0f, 0f, 0f, TextFieldDefaults.UnfocusedIndicatorLineOpacity),
                RoundedCornerShape(5.dp)
            )
            .padding(10.dp)
            .clickable { showDatePicker(context, onUpdateDate) }
    ) {
        Text(
            text = date ?: stringResource(R.string.deadline),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = stringResource(R.string.deadline),
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .size(20.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private fun showDatePicker(
    context: Context,
    updatedDate: (LocalDateTime?) -> Unit)
{
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            updatedDate(
                DateFormatUtil.fromDateTimeValues(
                    year,
                    month+1,
                    day,
                    0,
                    0,
                    0
                )
            )
        }, currentYear, currentMonth, currentDay
    ).show()
}