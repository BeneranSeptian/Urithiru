package com.septianbeneran.urithiru.feature.catalog.screen.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

@Composable
fun ComponentDemo(id: String) {
    when (id) {
        "button" -> ButtonDemo()
        "dialog" -> DialogDemo()
        "toast" -> ToastDemo()
        "banner" -> BannerDemo()
        "text_field" -> TextFieldDemo()
        "search_bar" -> SearchBarDemo()
        "check_box" -> CheckBoxDemo()
        "radio_button" -> RadioButtonDemo()
        "theme_picker" -> ThemePickerDemo()
        "loader" -> LoaderDemo()
        "progress_bar" -> ProgressBarDemo()
        "skeleton" -> SkeletonDemo()
        "error_state" -> ErrorStateDemo()
        "empty_state" -> EmptyStateDemo()
        "list_footer" -> ListFooterDemo()
        "list_item" -> ListItemDemo()
        "image" -> ImageDemo()
        "media_placeholder" -> MediaPlaceHolderDemo()
        "avatar" -> AvatarDemo()
        "badge" -> BadgeDemo()
        "dot" -> DotDemo()
        "stepper" -> StepperDemo()
        "divider" -> DividerDemo()
        "top_app_bar" -> TopAppBarDemo()
    }
}

@Composable
fun DemoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = UrithiruTheme.typography.h5,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}
