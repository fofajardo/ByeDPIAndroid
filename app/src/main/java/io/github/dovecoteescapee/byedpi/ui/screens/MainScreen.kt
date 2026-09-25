package io.github.dovecoteescapee.byedpi.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.ui.theme.ByeDpiTheme

@Composable
fun MainScreen(
    buttonText: String,
    statusText: String,
    proxyAddress: String,
    buttonEnabled: Boolean,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onButtonClick,
                enabled = buttonEnabled,
                contentPadding = PaddingValues(horizontal = 26.dp, vertical = 13.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 24.sp
                )
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = proxyAddress,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    ByeDpiTheme {
        Surface {
            MainScreen(
                buttonText = stringResource(R.string.vpn_connect),
                statusText = stringResource(R.string.vpn_disconnected),
                proxyAddress = "127.0.0.1:1080",
                buttonEnabled = true,
                onButtonClick = {}
            )
        }
    }
}
