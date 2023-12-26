package com.myisolutions.income.tax.calculator.pakistan

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.myisolutions.income.tax.calculator.pakistan.ui.theme.TaxCalculatorPakistanTheme

@Composable
fun TaxDetailScreen(viewModel: MainViewModel, onClick: () -> Unit)
{
    TaxCalculatorPakistanTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                val taxDetail = viewModel.taxDetail.collectAsState()
                Text(
                    text = "Your income fall under the following tax slab:",
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = taxDetail.value.taxBracket)
                Spacer(modifier = Modifier.height(20.dp))
                DrawCard(taxDetail = taxDetail.value)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val density = LocalDensity.current.density
                    val animateTransition = rememberInfiniteTransition()
                    val yAnimation = animateTransition.animateFloat(
                        initialValue = 0F,
                        targetValue = -70F,
                        animationSpec = InfiniteRepeatableSpec(
                            animation = tween(
                                durationMillis = 400,
                                delayMillis = 200,
                                easing = FastOutSlowInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    Column(
                        modifier = Modifier
                            .clickable { onClick() }
                            .wrapContentSize()
                            .offset(0.dp, (yAnimation.value / density).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Click to go back")
                        Image(
                            painter = painterResource(id = R.drawable.arrow_down),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawCard(taxDetail: TaxDetail)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            Text(
                text = "Calculation Details",
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Exempted amount")
                Text(
                    text = String.format("%.1f", taxDetail.amountExempted),
                    textAlign = TextAlign.Right
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tax on exempted amount")
                Text(
                    text = String.format("%.1f", taxDetail.taxExempted),
                    textAlign = TextAlign.Right
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tax amount after exemption")
                Text(
                    text = String.format("%.1f", taxDetail.amountLeft),
                    textAlign = TextAlign.Right
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "% on tax amount")
                Text(
                    text = "${
                        String.format(
                            "%.1f",
                            taxDetail.amountLeft
                        )
                    } * ${
                        taxDetail.taxPercentageAmountLeft
                    } = ${
                        String.format(
                            "%.1f",
                            taxDetail.amountLeft * taxDetail.taxPercentageAmountLeft
                        )
                    }",
                    textAlign = TextAlign.Right
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Tax")
                Text(
                    text = String.format(
                        "%.1f",
                        taxDetail.taxExempted + (taxDetail.amountLeft * taxDetail.taxPercentageAmountLeft)
                    ),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}