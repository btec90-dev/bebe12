package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PregnancyWeek
import com.example.data.model.SizeTheme
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.CaramelSecondary
import com.example.ui.theme.CrispWhiteSurface
import com.example.ui.theme.TextEspresso
import com.example.ui.theme.TextWarmTaupe

@Composable
fun PregnancyCountdownHeroCard(
    currentWeek: Int,
    weekData: PregnancyWeek,
    diffDays: Int,
    sizeTheme: SizeTheme,
    onEditDueDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val comparison = weekData.getComparison(sizeTheme)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp), spotColor = CaramelPrimary.copy(alpha = 0.08f))
            .clickable(onClick = onEditDueDate)
            .testTag("pregnancy_countdown_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CrispWhiteSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Baby Illustration in Womb / Botanical Frame & Size text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Baby illustration frame
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    CaramelSecondary.copy(alpha = 0.25f),
                                    CaramelPrimary.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(1.5.dp, CaramelPrimary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = comparison.emoji,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Wk $currentWeek",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaramelPrimary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Your baby is the size\nof a ${comparison.title.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextEspresso,
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit due date",
                            tint = CaramelPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Tap to edit due date",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWarmTaupe,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Side: Circular Countdown Progress Dial
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular arc progress
                Canvas(modifier = Modifier.size(90.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                    val arcSize = Size(diameter, diameter)

                    // Background track
                    drawArc(
                        color = CaramelSecondary.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Progress arc
                    val progressFraction = (280f - diffDays.toFloat()).coerceIn(0f, 280f) / 280f
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(CaramelSecondary, CaramelPrimary)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Center countdown text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$diffDays",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextEspresso,
                            fontSize = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Days to Go",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = TextWarmTaupe,
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
