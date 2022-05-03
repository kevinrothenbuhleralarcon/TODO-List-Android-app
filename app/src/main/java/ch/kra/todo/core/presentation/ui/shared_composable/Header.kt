package ch.kra.todo.core.presentation.ui.shared_composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.kra.todo.R

@Composable
fun Header(
    modifier: Modifier = Modifier,
    username: String = "",
    hasBackArrow: Boolean = false,
    title: String = "",
    navigateBack: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .background(MaterialTheme.colors.primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
        ) {
            if (hasBackArrow) {
                IconButton(
                    onClick = { navigateBack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))

            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        }
        if (username.isNotEmpty()) {
            Text(
                text = username,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 20.dp)
            )
        }
    }
}