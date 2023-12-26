package com.myisolutions.income.tax.calculator.pakistan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel()
{
    val income = savedStateHandle.getStateFlow("income", "")
    val timePeriodSelected = savedStateHandle.getStateFlow("timePeriodSelected", "Monthly")
    val taxDetail = savedStateHandle.getStateFlow("taxDetail", TaxDetail())
    val showDetailBtn = savedStateHandle.getStateFlow("showDetailBtn", false)

    fun updateIncome(income: String)
    {
        savedStateHandle["income"] = income
    }

    fun updateTimePeriodSelected(timePeriodSelected: String)
    {
        savedStateHandle["timePeriodSelected"] = timePeriodSelected
    }

    fun updateTaxDetail(taxDetail: TaxDetail)
    {
        savedStateHandle["taxDetail"] = taxDetail
    }

    fun updateShowDetailBtn(showDetailBtn: Boolean)
    {
        savedStateHandle["showDetailBtn"] = showDetailBtn
    }
}