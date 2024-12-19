package pt.ipca.projetopdm

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp


val componentShapes = Shapes(

    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    //large = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(
        topStart = 28.0.dp,
        topEnd = 28.0.dp,
        bottomEnd = 28.0.dp,
        bottomStart = 28.0.dp
    )
)