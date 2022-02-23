package dfg.exchangerates.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.domain.usecase.GetExchangePairsUseCase
import dfg.exchangerates.domain.usecase.GetExchangeRatesUseCase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.e
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    app: Application,
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val getExchangePairsUseCase: GetExchangePairsUseCase
) : AndroidViewModel(app) {

    init {
        getExchangeRates()
        getExchangePairs()
    }

    private val exchangeRatesResponse = MutableLiveData<ExchangeRates.Rates>()
    private val exchangePairsResponse = MutableLiveData<ExchangeRates.Pairs>()
    val exchangePairsWithRate = MutableLiveData<List<ExchangeRates.Rate>>()

    private fun getExchangeRates() {
        try {
            viewModelScope.launch(IO) {
                getExchangeRatesUseCase.execute()?.let {
                    viewModelScope.launch(Main) {
                        exchangeRatesResponse.value = it
                        calculateRates()
                    }
                }
            }
        } catch (e: IOException) {
            e(e)
        }
    }

    private fun getExchangePairs() {
        try {
            viewModelScope.launch(IO) {
                getExchangePairsUseCase.execute()?.let {
                    viewModelScope.launch(Main) {
                        exchangePairsResponse.value = it
                        calculateRates()
                    }
                }
            }
        } catch (e: IOException) {
            e(e)
        }
    }



    private fun calculateRates() {

        val exchangePairWithRatesList: MutableList<ExchangeRates.Rate> = mutableListOf()

        if (exchangePairsResponse.value != null && exchangeRatesResponse.value != null) {
            for (pair in exchangePairsResponse.value!!.pairs) {
                for (rate in exchangeRatesResponse.value!!.rates) {
                    e("pair = $pair, rate = $rate")
                    var newRate = ""
                    var fromRate = ""
                    var toRate = ""
                    if (pair.to == rate.to) {
                        if (pair.from == rate.from) {
                            e("Adding :: pair = $pair, rate = $rate")
                            newRate = rate.rate
                            e("New Rate = $newRate")
                            exchangePairWithRatesList.add(ExchangeRates.Rate(pair.from, pair.to, newRate))
                            exchangePairsWithRate.value = exchangePairWithRatesList
                            e("List size to display: ${exchangePairsWithRate.value?.size}")
                        }
                    }

                    when {
                        pair.to == rate.to -> {
                            if (pair.from == rate.from) {
                                newRate = rate.rate
                                exchangePairWithRatesList.add(ExchangeRates.Rate(pair.from, pair.to, newRate))
                            }
                        }
                        pair.from == rate.from -> {
                            if (pair.to == rate.to) {
                                newRate = rate.rate
                                exchangePairWithRatesList.add(ExchangeRates.Rate(pair.from, pair.to, newRate))
                            }
                        }
                        pair.to != rate.to -> {
                            if (pair.from == rate.from)  fromRate = rate.rate
                        }
                        pair.from != rate.from -> {
                            if (pair.to == rate.to) toRate = rate.rate
                        }
                    }
                }
            }
        }

    }
}