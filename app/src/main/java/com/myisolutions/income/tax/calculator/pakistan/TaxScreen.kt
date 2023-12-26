package com.myisolutions.income.tax.calculator.pakistan

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.util.rangeTo
import com.myisolutions.income.tax.calculator.pakistan.ui.theme.TaxCalculatorPakistanTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaxScreen(viewModel: MainViewModel, onClick: () -> Unit)
{
    TaxCalculatorPakistanTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                val context = LocalContext.current
                val timePeriod = listOf("Monthly", "Yearly")

                val income = viewModel.income.collectAsState()
                val timePeriodSelected = viewModel.timePeriodSelected.collectAsState()
                val tax = viewModel.taxDetail.collectAsState().value.tax
                val showDetailBtn = viewModel.showDetailBtn.collectAsState()

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                ) {
                    TextField(
                        value = income.value,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            viewModel.updateIncome(it)
                            viewModel.updateTaxDetail(TaxDetail())
                            viewModel.updateShowDetailBtn(false)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        placeholder = { Text(text = "Your Income") },
                        singleLine = true,
                        maxLines = 1,
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
//                        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.05f))

                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    val expanded = remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = !expanded.value },
                        modifier = Modifier.width(150.dp),
                        content = {
                            TextField(
                                value = timePeriodSelected.value,
                                onValueChange = {},
                                readOnly = true,
                                singleLine = true,
                                maxLines = 1,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded.value
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
//                                colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
                            )
                            ExposedDropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false },
                                content = {
                                    timePeriod.map {
                                        DropdownMenuItem(
                                            onClick = {
                                                viewModel.updateTimePeriodSelected(it)
                                                viewModel.updateTaxDetail(TaxDetail())
                                                expanded.value = false
                                                viewModel.updateShowDetailBtn(false)
                                            },
                                            content = { Text(text = it, maxLines = 1) }
                                        )
                                    }
                                })
                        })
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .padding(vertical = 10.dp),
                    onClick = {
                        calculateTax(context, income.value, timePeriodSelected.value).let {
                            viewModel.updateTaxDetail(it)
                        }
                        viewModel.updateShowDetailBtn(true)
                    },
//                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(context.getColor(R.color.purple_dark))),
                    content = {
                        Text(
                            text = "Calculate",
                            color = Color.White,
                            style = MaterialTheme.typography.button
                        )
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                DrawCard(
                    card = "By Month",
                    incomeLambda = {
                        if (income.value == "") 0.0
                        else if (timePeriodSelected.value == "Monthly") income.value.toDouble()
                        else income.value.toDouble() / 12
                    },
                    taxLambda = {
                        if (tax == 0.0) 0.0
                        else if (timePeriodSelected.value == "Monthly") tax
                        else tax / 12
                    },
                    incomeAfterTaxLambda = {
                        if (income.value == "" || tax == 0.0) 0.0
                        else if (timePeriodSelected.value == "Monthly") income.value.toDouble() - tax
                        else (income.value.toDouble() / 12) - (tax / 12)
                    })
                Spacer(modifier = Modifier.height(10.dp))
                DrawCard(
                    card = "By Year",
                    incomeLambda = {
                        if (income.value == "") 0.0
                        else if (timePeriodSelected.value == "Monthly") income.value.toDouble() * 12
                        else income.value.toDouble()
                    },
                    taxLambda = {
                        if (tax == 0.0) 0.0
                        else if (timePeriodSelected.value == "Monthly") tax * 12
                        else tax
                    },
                    incomeAfterTaxLambda = {
                        if (income.value == "" || tax == 0.0) 0.0
                        else if (timePeriodSelected.value == "Monthly") (income.value.toDouble() * 12) - (tax * 12)
                        else income.value.toDouble() - tax
                    })
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = showDetailBtn.value,
                        enter = fadeIn(),
                        exit = fadeOut()
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
                            Text(text = "Click for details")
                            Image(
                                painter = painterResource(id = R.drawable.arrow_up),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DrawCard(
    card: String,
    incomeLambda: () -> Double,
    taxLambda: () -> Double,
    incomeAfterTaxLambda: () -> Double
)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            Text(
                text = card,
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
                Text(text = "Income including Tax")
                Text(text = String.format("%.1f", incomeLambda()), textAlign = TextAlign.Right)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tax deduction")
                Text(text = String.format("%.1f", taxLambda()), textAlign = TextAlign.Right)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Income after Tax")
                Text(
                    text = String.format("%.1f", incomeAfterTaxLambda()),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

fun calculateTax(context: Context, income: String, timePeriod: String): TaxDetail
{
    val taxDetail: TaxDetail = when (val yearlyIncome =
        if (timePeriod == "Monthly") income.toDouble() * 12 else income.toDouble())
    {
        in 1.0 rangeTo 600000.0 -> TaxDetail(context.getString(R.string.tax_bracket_1))
        in 600000.0 rangeTo 1200000.0 -> TaxDetail(
            (yearlyIncome - 600000) / 100 * 2.5,
            context.getString(R.string.tax_bracket_2),
            600000.0,
            0.0,
            yearlyIncome - 600000,
            0.025
        )
        in 1200000.0 rangeTo 2400000.0 -> TaxDetail(
            15000 + ((yearlyIncome - 1200000) / 100 * 12.5),
            context.getString(R.string.tax_bracket_3),
            1200000.0,
            15000.0,
            yearlyIncome - 1200000,
            0.125
        )
        in 2400000.0 rangeTo 3600000.0 -> TaxDetail(
            165000 + ((yearlyIncome - 2400000) / 100 * 22.5),
            context.getString(R.string.tax_bracket_4),
            2400000.0,
            165000.0,
            yearlyIncome - 2400000,
            0.225
        )
        in 3600000.0 rangeTo 6000000.0 -> TaxDetail(
            435000 + ((yearlyIncome - 3600000) / 100 * 27.5),
            context.getString(R.string.tax_bracket_5),
            3600000.0,
            435000.0,
            yearlyIncome - 3600000,
            0.275
        )
        else -> TaxDetail(
            1095000 + ((yearlyIncome - 6000000) / 100 * 35.0),
            context.getString(R.string.tax_bracket_6),
            6000000.0,
            1095000.0,
            yearlyIncome - 6000000,
            0.35
        )
    }
    taxDetail.tax = if (timePeriod == "Monthly") taxDetail.tax / 12 else taxDetail.tax
    return taxDetail
}
