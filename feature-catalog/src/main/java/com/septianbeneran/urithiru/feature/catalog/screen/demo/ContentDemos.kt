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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R as CoreUiR
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
import com.septianbeneran.urithiru.feature.catalog.R

@Composable
fun ListItemDemo() {
    DemoSection(stringResource(R.string.catalog_demo_variants)) {
        UriListItem(title = stringResource(R.string.catalog_demo_title_only))
        UriListItem(title = stringResource(R.string.catalog_demo_with_description), description = stringResource(R.string.catalog_demo_second_line))
        UriListItem(
            title = stringResource(R.string.catalog_demo_with_leading),
            description = stringResource(R.string.catalog_demo_avatar_left),
            leadingContent = { UriAvatar() }
        )
        UriListItem(
            title = stringResource(R.string.catalog_demo_with_trailing),
            description = stringResource(R.string.catalog_demo_chevron_right),
            trailingContent = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(CoreUiR.drawable.chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
fun ImageDemo() {
    DemoSection(stringResource(R.string.catalog_demo_loaded)) {
        UriImage(
            modifier = Modifier.size(120.dp),
            model = DEMO_IMAGE_URL,
            contentDescription = stringResource(R.string.catalog_demo_product_photo),
            shape = RoundedCornerShape(12.dp)
        )
    }
    DemoSection(stringResource(R.string.catalog_demo_failed_to_load)) {
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
    DemoSection(stringResource(R.string.catalog_demo_image_and_video)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            UriMediaPlaceHolder()
            UriMediaPlaceHolder(icon = CoreUiR.drawable.play)
        }
    }
}

@Composable
fun AvatarDemo() {
    DemoSection(stringResource(R.string.catalog_demo_sizes)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            UriAvatar(size = UriAvatarProperties.S)
            UriAvatar(size = UriAvatarProperties.M)
            UriAvatar(size = UriAvatarProperties.L)
        }
    }
}

@Composable
fun BadgeDemo() {
    DemoSection(stringResource(R.string.catalog_demo_types)) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            UriBadge(type = UriBadgeProperties.Type.Number("9"))
            UriBadge(type = UriBadgeProperties.Type.Icon)
            UriBadge(type = UriBadgeProperties.Type.Dot)
        }
    }
}

@Composable
fun DotDemo() {
    DemoSection(stringResource(R.string.catalog_demo_pager_indicator)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UriDot(isFilled = true)
            UriDot()
            UriDot()
        }
    }
}

@Composable
fun StepperDemo() {
    DemoSection(stringResource(R.string.catalog_demo_states)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            UriStepper(type = UriStepperProperties.Type.Done, stepText = "1", titleText = stringResource(R.string.catalog_demo_step_account))
            UriStepper(type = UriStepperProperties.Type.Filled, stepText = "2", titleText = stringResource(R.string.catalog_demo_step_address))
            UriStepper(type = UriStepperProperties.Type.Unfilled, stepText = "3", titleText = stringResource(R.string.catalog_demo_step_payment))
        }
    }
}

@Composable
fun DividerDemo() {
    DemoSection(stringResource(R.string.catalog_demo_between_rows)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.catalog_demo_above), style = UrithiruTheme.typography.bodyM, color = MaterialTheme.colorScheme.onSurface)
            UriDivider()
            Text(text = stringResource(R.string.catalog_demo_below), style = UrithiruTheme.typography.bodyM, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private const val DEMO_IMAGE_URL =
    "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp"
