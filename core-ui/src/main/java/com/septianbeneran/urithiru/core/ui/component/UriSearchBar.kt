package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

/**
 * Single-line search field. The keyboard's search key calls [onSearch] and hides the keyboard.
 * The clear button shows while there's text; it empties the field and then calls [onClear].
 */
@Composable
fun UriSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String,
    onClear: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = UrithiruTheme.typography.bodyM
            )
        },
        textStyle = UrithiruTheme.typography.bodyM,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.search),
                contentDescription = stringResource(R.string.search_icon_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        onValueChange("")
                        onClear?.invoke()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.search_clear_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearch()
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun UriSearchBarPrev() {
    UrithiruTheme {
        var text by remember { mutableStateOf("Hand Axe") }
        Box(modifier = Modifier.padding(16.dp)) {
            UriSearchBar(
                value = text,
                onValueChange = { text = it },
                onSearch = {},
                placeholder = "Search weapon..."
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriSearchBarEmptyPrev() {
    UrithiruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            UriSearchBar(
                value = "",
                onValueChange = {},
                onSearch = {},
                placeholder = "Search weapon..."
            )
        }
    }
}
