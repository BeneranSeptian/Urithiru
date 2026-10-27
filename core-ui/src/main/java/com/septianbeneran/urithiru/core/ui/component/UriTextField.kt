package com.septianbeneran.urithiru.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.septianbeneran.urithiru.core.ui.R
import com.septianbeneran.urithiru.core.ui.theme.UrithiruTheme

/**
 * Labelled single-line text field for forms. Shows [errorText] below the field and marks it as an error.
 * With [isPassword], the text is hidden and an eye button toggles it.
 */
@Composable
fun UriTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    errorText: String? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = UrithiruTheme.typography.bodyM
                )
            },
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = UrithiruTheme.typography.bodyM
                    )
                }
            },
            textStyle = UrithiruTheme.typography.bodyM,
            singleLine = true,
            enabled = enabled,
            isError = errorText != null,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(
                                if (isPasswordVisible) R.drawable.visibility_off else R.drawable.visibility
                            ),
                            contentDescription = stringResource(
                                if (isPasswordVisible) R.string.text_field_hide_password
                                else R.string.text_field_show_password
                            ),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onAny = { onImeAction() })
        )
        errorText?.let {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = it,
                style = UrithiruTheme.typography.bodyS,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UriTextFieldPrev() {
    UrithiruTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UriTextField(value = "emilys", onValueChange = {}, label = "Username")
            UriTextField(value = "emilyspass", onValueChange = {}, label = "Password", isPassword = true)
            UriTextField(
                value = "wrong",
                onValueChange = {},
                label = "Password",
                isPassword = true,
                errorText = "Invalid username or password"
            )
        }
    }
}
