package com.septianbeneran.urithiru.core.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.component.UriTopAppBarProperties.defaults
import com.septianbeneran.urithiru.core.ui.theme.Highlight
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UriTopAppBar(
    modifier: Modifier = Modifier,
    topAppBarArgs: UriTopAppBarProperties.TopAppBarArgs = defaults(),
    onLeadingComposableClick: () -> Unit = {}
) {
    with(topAppBarArgs) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                AppBarTitle(
                    title = title
                )
            },
            navigationIcon = {
                if (leadingComposable != null) {
                    leadingComposable(onLeadingComposableClick)
                } else {
                    backIcon?.let {
                        AppBarBackButton(
                            backIcon = backIcon,
                            onClick = onLeadingComposableClick
                        )
                    }
                }
            },
            actions = {
                trailingComposable?.invoke(onTrailingComposableClick)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
fun AppBarTitle(
    modifier: Modifier = Modifier,
    title: String?
) {
    Text(
        text = title.orEmpty(),
        style = UrithiruTypography.titleLarge,
        modifier = modifier
            .padding(8.dp, end = 8.dp),
        maxLines = 1,
        overflow = Ellipsis
    )
}

@Composable
fun AppBarBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes backIcon: Int?
) {
    backIcon?.let {
        Icon(
            painter = painterResource(it),
            contentDescription = null,
            modifier = modifier
                .size(20.dp)
                .clickable(onClick = onClick),
            tint = Highlight.Highlight500
        )
    }
}

object UriTopAppBarProperties {

    @Composable
    fun defaults(
        title: String = "",
        backIcon: Int? = R.drawable.arrow_left,
        onTrailingComposableClick: () -> Unit = {},
        leadingComposable: (@Composable (onClick: () -> Unit) -> Unit)? = null,
        trailingComposable: (@Composable (onClick: () -> Unit) -> Unit)? = null
    ) = TopAppBarArgs(
        title = title,
        backIcon = backIcon,
        onTrailingComposableClick = onTrailingComposableClick,
        leadingComposable = leadingComposable,
        trailingComposable = trailingComposable
    )

    data class TopAppBarArgs(
        val title: String,
        val backIcon: Int?,
        val onTrailingComposableClick: () -> Unit,
        val leadingComposable: (@Composable (onClick: () -> Unit) -> Unit)? = null,
        val trailingComposable: (@Composable (onClick: () -> Unit) -> Unit)? = null
    )
}

@Preview(showBackground = true)
@Composable
private fun UriTopAppBarPreview() {
    UrithiruTheme() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UriTopAppBar(
                    topAppBarArgs = defaults(
                        title = "Page Title",
                        trailingComposable = {
                            Text(
                                text = "Edit",
                                style = UrithiruTypography.labelMedium,
                                color = Highlight.Highlight500
                            )
                        }
                    ),
                )
                UriTopAppBar(
                    topAppBarArgs = defaults(
                        title = "Page Title",
                        trailingComposable = {
                            UriAvatar()
                        }
                    ),
                )

                UriTopAppBar(
                    topAppBarArgs = defaults(
                        title = "Page Title",
                        trailingComposable = {
                            Icon(
                                painter = painterResource(id = R.drawable.heart_outlined),
                                contentDescription = null,
                                tint = Highlight.Highlight500
                            )
                        }
                    ),
                )

                UriTopAppBar(
                    topAppBarArgs = defaults(
                        title = "Page Title",
                        leadingComposable = {
                            Text(
                                text = "Cancel",
                                style = UrithiruTypography.labelMedium,
                                color = Highlight.Highlight500
                            )
                        },
                        trailingComposable = {
                            Text(
                                text = "Edit",
                                style = UrithiruTypography.labelMedium,
                                color = Highlight.Highlight500
                            )
                        }
                    ),
                )

                UriTopAppBar(
                    topAppBarArgs = defaults(
                        title = "Page Title",
                        leadingComposable = {
                            Text(
                                text = "Cancel",
                                style = UrithiruTypography.labelMedium,
                                color = Highlight.Highlight500
                            )
                        },
                        trailingComposable = {
                            UriAvatar()
                        }
                    )
                )
            }
        }
    }
}