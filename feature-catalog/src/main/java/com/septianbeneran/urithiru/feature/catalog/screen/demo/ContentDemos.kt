package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriAvatar
import com.septianbeneran.urithiru.core.ui.component.UriAvatarProperties
import com.septianbeneran.urithiru.core.ui.component.UriBadge
import com.septianbeneran.urithiru.core.ui.component.UriBadgeProperties
import com.septianbeneran.urithiru.core.ui.component.UriDivider
import com.septianbeneran.urithiru.core.ui.component.UriDot
import com.septianbeneran.urithiru.core.ui.component.UriImage
import com.septianbeneran.urithiru.core.ui.component.UriListItem
import com.septianbeneran.urithiru.core.ui.component.UriMediaPlaceHolder
import com.septianbeneran.urithiru.core.ui.component.UriStepper
import com.septianbeneran.urithiru.core.ui.component.UriStepperProperties
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun ListItemDemo() {
    DemoSection("Variants") {
        UriListItem(title = "Title only")
        UriListItem(title = "With description", description = "A second line of supporting text.")
        UriListItem(
            title = "With leading content",
            description = "An avatar on the left.",
            leadingContent = { UriAvatar() }
        )
        UriListItem(
            title = "With trailing content",
            description = "A chevron on the right.",
            trailingContent = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
fun ImageDemo() {
    DemoSection("Loaded") {
        UriImage(
            modifier = Modifier.size(120.dp),
            model = DEMO_IMAGE_URL,
            contentDescription = "Product photo",
            shape = RoundedCornerShape(12.dp)
        )
    }
    DemoSection("Failed to load") {
        UriImage(
            modifier = Modifier.size(120.dp),
            model = "https://dummyjson.com/not-an-image.png",
            contentDescription = null,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun MediaPlaceHolderDemo() {
    DemoSection("Image and video") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            UriMediaPlaceHolder()
            UriMediaPlaceHolder(icon = R.drawable.play)
        }
    }
}

@Composable
fun AvatarDemo() {
    DemoSection("Sizes") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            UriAvatar(size = UriAvatarProperties.S)
            UriAvatar(size = UriAvatarProperties.M)
            UriAvatar(size = UriAvatarProperties.L)
        }
    }
}

@Composable
fun BadgeDemo() {
    DemoSection("Types") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UriBadge(type = UriBadgeProperties.Type.Number("9"))
            UriBadge(type = UriBadgeProperties.Type.Icon)
            UriBadge(type = UriBadgeProperties.Type.Dot)
        }
    }
}

@Composable
fun DotDemo() {
    DemoSection("Pager indicator") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UriDot(isFilled = true)
            UriDot()
            UriDot()
        }
    }
}

@Composable
fun StepperDemo() {
    DemoSection("States") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            UriStepper(type = UriStepperProperties.Type.Done, stepText = "1", titleText = "Account")
            UriStepper(type = UriStepperProperties.Type.Filled, stepText = "2", titleText = "Address")
            UriStepper(type = UriStepperProperties.Type.Unfilled, stepText = "3", titleText = "Payment")
        }
    }
}

@Composable
fun DividerDemo() {
    DemoSection("Between rows") {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Above", style = UrithiruTheme.typography.bodyM, color = MaterialTheme.colorScheme.onSurface)
            UriDivider()
            Text(text = "Below", style = UrithiruTheme.typography.bodyM, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private const val DEMO_IMAGE_URL =
    "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp"
