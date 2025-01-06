package pt.ipca.projetopdm.Components

import android.util.Log
import android.widget.CheckBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.w3c.dom.Text
import pt.ipca.projetopdm.GrayColor
import pt.ipca.projetopdm.Primary
import pt.ipca.projetopdm.R
import pt.ipca.projetopdm.Secondary
import pt.ipca.projetopdm.TextColor
import pt.ipca.projetopdm.componentShapes
//import pt.ipca.projetopdm.navigation.Screen
//import pt.ipca.shopping_cart_app.R.color.Primary
//import pt.ipca.projetopdm.ui.theme.Primary



@Composable
fun SignUpText(value:String) {

    Text(text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),


        style = TextStyle(

            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        )

        , color = colorResource(id = R.color.black),
        textAlign = TextAlign.Center

    )
}

@Composable
fun SignUpToContinueText(value:String) {

    Text(text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),


        style = TextStyle(

            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )

        , color = colorResource(id = R.color.black),
        textAlign = TextAlign.Center

    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextFieldComponent(labelValue: String, painterResource: Painter) {


    val textValue = remember {


        mutableStateOf("")
    }


    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        label = {Text(text = labelValue)},
        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Primary, focusedLabelColor = Primary, cursorColor = Primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), value = textValue.value, onValueChange = {

            textValue.value = it
        },

        leadingIcon = {

            Icon(painter = painterResource, contentDescription = "")
        }

    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextFieldComponent(labelValue: String, painterResource: Painter) {


    val localFocusManager = LocalFocusManager.current

    val password = remember {


        mutableStateOf("")
    }



    val passwordVisible = remember {

        mutableStateOf(false)
    }



    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        label = {Text(text = labelValue)},
        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Primary, focusedLabelColor = Primary, cursorColor = Primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), value = password.value, onValueChange = {

            password.value = it
        },

        leadingIcon = {

            Icon(painter = painterResource, contentDescription = "")
        },

        trailingIcon = {

            val iconImage = if(passwordVisible.value) {

                Icons.Filled.Visibility
            }else {

                Icons.Filled.VisibilityOff
            }



            var description = if(passwordVisible.value) {

                stringResource(id = R.string.hide_password)
            }else {

                stringResource(id = R.string.show_password)
            }


            IconButton(onClick = { passwordVisible.value = !passwordVisible.value}) {

                Icon(imageVector = iconImage, contentDescription = description)

            }

        },

        visualTransformation = if(passwordVisible.value) VisualTransformation.None else

            PasswordVisualTransformation()


    )

}

@Composable
fun ClickableTextComponent(value: String, onTextSelected: (String) -> Unit) {
    val initialText = "By continuing you accept our "
    val privacyPolicyText = "Privacy Policy"
    val andText = " and "
    val termsAndConditionsText = "Terms"

    val annotatedString = buildAnnotatedString {
        append(initialText)

        // Adiciona a anotação para "Privacy Policy"
        pushStringAnnotation(tag = privacyPolicyText, annotation = privacyPolicyText)
        withStyle(style = SpanStyle(color = Primary)) {
            append(privacyPolicyText)
        }
        pop()

        append(andText)

        // Adiciona a anotação para "Terms"
        pushStringAnnotation(tag = termsAndConditionsText, annotation = termsAndConditionsText)
        withStyle(style = SpanStyle(color = Primary)) {
            append(termsAndConditionsText)
        }
        pop() // Finaliza a anotação
    }

    // Renderiza o texto clicável
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            // Verifica se o offset está dentro de uma anotação
            annotatedString.getStringAnnotations(start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    Log.d("ClickableTextComponent", "Clicked on: ${annotation.item}")
                    if (annotation.item == privacyPolicyText || annotation.item == termsAndConditionsText) {
                        onTextSelected(annotation.item) // Chama o método de navegação
                    }
                }
        }
    )
}





@Composable
fun CheckBoxComponent(value: String, onTextSelected: (String) -> Unit) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {





        val CheckedState = remember {


            mutableStateOf(false)
        }

        Checkbox(checked= CheckedState.value, onCheckedChange= {CheckedState.value != CheckedState.value})
        ClickableTextComponent(value = value, onTextSelected)
    }
}





@Composable
fun ButtonComponent(value: String) {


    Button(onClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent)


    ) {


        Box(

            modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp).background(

                    brush = Brush.horizontalGradient(listOf(Secondary, Primary)),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {


            Text(text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




@Composable
fun DividerTextComponent() {

    Row(modifier = Modifier
        .fillMaxWidth()
    ) {

        Divider(

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = GrayColor,
            thickness = 1.dp
        )

        Text(text = "or", fontSize = 14.sp, color = TextColor)

        Divider(

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = GrayColor,
            thickness = 1.dp
        )


    }
}

@Composable
fun ClickableLoginTextComponent(tryingToLogin:Boolean = false,onTextSelected: (String) -> Unit) {

    val initialText = if(tryingToLogin) "Already have an account? " else "Don´t have an account yet? "
    val loginText = if(tryingToLogin)"Login" else "Register"

    val annotatedString = buildAnnotatedString {

        append(initialText)
        withStyle(style = SpanStyle(color = Primary)) {

            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }





    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        text = annotatedString, onClick = {


                offset ->


            annotatedString.getStringAnnotations(offset,offset)
                .firstOrNull()?.also {span->


                    Log.d("ClickableTextComponent", "{${span.item}}")


                    if((span.item == loginText)) {


                        onTextSelected(span.item)
                    }
                }
        })
}


@Composable
fun UnderlineSignUpText(value:String) {

    Text(text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),


        style = TextStyle(

            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        )

        , color = colorResource(id = R.color.GreyColor),
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline

    )
}