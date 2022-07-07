package com.scgts.sctrace.capture.composable.camera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.assets.confirmation.R
import com.scgts.sctrace.ui.components.SCTraceTextField
import theme.N100
import theme.SCGTSTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddTag(
    tags: List<String>,
    onSaveClicked: (String) -> Unit,
    onDeleteClicked: (String) -> Unit,
    onDoneClicked: () -> Unit,
) {
    val (tag, setTag) = remember { mutableStateOf("") }
    val (isEditing, setIsEditing) = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun clearTextField() {
        setTag("")
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    fun saveTag() {
        onSaveClicked(tag)
        clearTextField()
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 24.dp)
        ) {
            if (isEditing) {
                TextButton(
                    onClick = { clearTextField() },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.Start)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
            TextButton(
                onClick = { if (isEditing) saveTag() else onDoneClicked() },
                enabled = (isEditing && tag.isNotEmpty()) || !isEditing,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Text(
                    text = if (isEditing) stringResource(R.string.save) else stringResource(R.string.done),
                    color = if ((isEditing && tag.isNotEmpty()) || !isEditing) MaterialTheme.colors.primary else N100
                )
            }
        }
        SCTraceTextField(
            value = tag,
            onValueChange = setTag,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onFocusChanged { focusState -> setIsEditing(focusState.isFocused) },
            placeholder = { Text(stringResource(R.string.enter_tag_id)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (tag.isNotEmpty()) saveTag() }),
        )
        TagsList(
            tags = tags,
            onDeleteClicked = onDeleteClicked
        )
    }
}

@Preview
@Composable
private fun AddTagPreview() {
    SCGTSTheme {
        Surface {
            AddTag(
                tags = listOf("tag1", "tag2", "tag3"),
                onSaveClicked = { },
                onDeleteClicked = { },
                onDoneClicked = { }
            )
        }
    }
}