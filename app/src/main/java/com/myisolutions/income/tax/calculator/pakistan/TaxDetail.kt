package com.myisolutions.income.tax.calculator.pakistan

data class TaxDetail(
    var tax: Double, val taxBracket: String, val amountExempted: Double, val taxExempted: Double,
    val amountLeft: Double, val taxPercentageAmountLeft: Double
): java.io.Serializable
{
    val taxLeft: Double = amountLeft * taxPercentageAmountLeft

    constructor(info: String) :
            this(0.0, info, 0.0, 0.0, 0.0, 0.0)

    constructor() :
            this(0.0, "", 0.0, 0.0, 0.0, 0.0)
}
