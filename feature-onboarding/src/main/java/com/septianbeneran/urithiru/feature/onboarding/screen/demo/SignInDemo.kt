package com.septianbeneran.urithiru.feature.onboarding.screen.demo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.component.UriListItem
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun SignInDemo(
    modifier: Modifier = Modifier,
    signedInName: String?
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (signedInName != null) "You're signed in" else "Demo account",
                style = UrithiruTheme.typography.h4,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (signedInName != null) {
                UriListItem(title = signedInName, description = "Session saved on this device")
            } else {
                UriListItem(title = "emilys", description = "Username")
                UriListItem(title = "emilyspass", description = "Password")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInDemoPrev() {
    UrithiruTheme {
        SignInDemo(signedInName = null)
    }
}
