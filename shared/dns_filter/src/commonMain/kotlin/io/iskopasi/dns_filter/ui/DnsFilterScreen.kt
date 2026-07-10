package io.iskopasi.dns_filter.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.iskopasi.dns_filter.decompose.DnsFilterComponent
import io.iskopasi.dns_filter.generated.resources.Res
import io.iskopasi.dns_filter.generated.resources.enter_domain
import io.iskopasi.dns_filter.generated.resources.nothing_blocked
import io.iskopasi.kmpvpntest.utils.theme.cGray
import io.iskopasi.kmpvpntest.utils.theme.cWhite
import org.jetbrains.compose.resources.stringResource

@Composable
fun DnsFilterScreen(
    modifier: Modifier = Modifier, component: DnsFilterComponent, padding: PaddingValues
) {
    val items by component.filterListFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.padding(top = padding.calculateTopPadding() + 32.dp)
            .padding(bottom = padding.calculateBottomPadding() + 16.dp).padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ).weight(1f), contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = items,
                contentAlignment = Alignment.Center
            ) {
                when {
                    it.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(Res.string.nothing_blocked), style = TextStyle(
                                color = cWhite.copy(alpha = 0.5f), fontSize = 13.sp

                            ), fontWeight = FontWeight.Light
                        )
                    }

                    else -> ListBox(items = it, onDeleteDomain = component::onDeleteDomain)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        InputField(
            onEnter = component::addDomain
        )
    }
}

@Composable
fun ListBox(modifier: Modifier = Modifier, items: Set<String>, onDeleteDomain: (String) -> Unit) {
    val style = remember {
        TextStyle(
            color = cWhite,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Light
        )
    }
    val itemMod = remember {
        Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
    }

    LazyColumn(
    ) {
        items(items = items.toList(), key = { domain -> domain }) { domain ->
            DomainItem(
                modifier = itemMod.animateItem(),
                item = domain,
                onDelete = onDeleteDomain,
                style = style
            )
            HorizontalDivider(
                thickness = 0.5.dp, color = cWhite
            )
        }
    }
}

@Composable
fun DomainItem(
    modifier: Modifier = Modifier, item: String, style: TextStyle, onDelete: (String) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item, style = style
        )
        IconButton(onClick = { onDelete(item) }) {
            Icon(LucideCircleMinus, contentDescription = null)
        }
    }
}

@Composable
fun InputField(modifier: Modifier = Modifier, onEnter: (String) -> Unit) {
    var state by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun onSave() {
        onEnter(state)
        state = ""
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 12.dp),
        border = BorderStroke(width = 0.5.dp, color = Color.White.copy(alpha = 0.3f)),
        color = Color(0xFFD7D7D7),
        contentColor = Color.Black,
        shape = CircleShape,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state, onValueChange = {
                    state = it
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    errorBorderColor = Color.Red,
                    errorLabelColor = Color.Red,
                    errorTextColor = cGray,
                    errorCursorColor = Color.Red,
                    focusedTextColor = cGray,
                    unfocusedTextColor = cGray.copy(alpha = 0.7f),
                    disabledTextColor = cGray.copy(alpha = 0.4f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedLabelColor = Color.Unspecified,
                    unfocusedLabelColor = cGray.copy(alpha = 0.7f),
                    disabledLabelColor = cGray.copy(alpha = 0.4f),
                    cursorColor = Color.Black,
                ), modifier = Modifier.fillMaxWidth().onFocusChanged {
                    isFocused = it.isFocused
                }.weight(1f), placeholder = {
                    Text(
                        text = stringResource(Res.string.enter_domain), style = TextStyle(
                            color = cGray.copy(alpha = 0.4f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraLight
                        )
                    )
                }, singleLine = true, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Uri
                ), keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }, onDone = {
                    onSave()
                    focusManager.clearFocus()
                })
            )
            IconButton(onClick = ::onSave) {
                Icon(VscodeCodiconsAdd, contentDescription = null)
            }
        }
    }
}