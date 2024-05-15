package ui.compose.Setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import kotlin.jvm.Transient

class AccountSettingsVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) :Screen{

    @Composable
    override fun Content() {
        val username = remember{
            mutableStateOf("")
        }
        val account = remember{
            mutableStateOf("")
        }
        val issUndergraduate = remember{
            mutableStateOf(true)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                issUndergraduate.value = true
                            }
                    ){
                        RadioButton(
                            selected = issUndergraduate.value,
                            onClick = {
                                issUndergraduate.value = true
                            }
                        )
                        Text("本科生")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                issUndergraduate.value = false
                            }
                    ){
                        RadioButton(
                            selected = !issUndergraduate.value,
                            onClick = {
                                issUndergraduate.value = false
                            }
                        )
                        Text("研究生")
                    }
                }
                TextField(
                    value = username.value,
                    onValueChange = {
                        username.value = it
                    },
                    label = {
                        Text("账号")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TextField(
                    value = account.value,
                    onValueChange = {
                        account.value = it
                    },
                    label = {
                        Text("密码")
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation('*')
                )
                Button(
                    onClick = {

                    }
                ){
                    Text("登录")
                }
            }
        }
    }

}