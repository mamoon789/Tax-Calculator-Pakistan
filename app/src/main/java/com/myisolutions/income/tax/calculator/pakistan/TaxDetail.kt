package com.myisolutions.income.tax.calculator.pakistan

data class TaxDetail(
    var tax: Double, val taxBracket: String, val amountExempted: Double, val taxAmountExempted: Double,
    val amountLeft: Double, val taxPercentageAmountLeft: Double
): java.io.Serializable
{
    val taxAmountLeft: Double = amountLeft * taxPercentageAmountLeft

    constructor(info: String, amountExempted: Double) :
            this(0.0, info, amountExempted, 0.0, 0.0, 0.0)

    constructor() :
            this(0.0, "", 0.0, 0.0, 0.0, 0.0)
}
