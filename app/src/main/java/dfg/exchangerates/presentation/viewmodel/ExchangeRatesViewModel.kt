package dfg.exchangerates.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.domain.usecase.GetExchangePairsUseCase
import dfg.exchangerates.domain.usecase.GetExchangeRatesUseCase
import dfg.exchangerates.utils.awaitForServer
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.w
import java.io.IOException
import java.lang.NumberFormatException
import java.util.*
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


    private val exchangePairsResponse = MutableLiveData<ExchangeRates.Pairs>()
    val exchangePairsWithRate = MutableLiveData<List<ExchangeRates.Rate>>()

    fun getExchangeRates() {
        try {
            viewModelScope.launch(IO) {
                getExchangeRatesUseCase.execute()?.let {
                    viewModelScope.launch(Main) {
                        listRates = it.rates.toMutableList()
                        isListUpdated = true
                        generateAllPossibleRates()
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
                    }
                }
            }
        } catch (e: IOException) {
            e(e)
        }
    }

    /**
     *
     * Observe that some currencies have inverse transformation (received from API) :
     *      ex: AUD to USD is 1.37 (should be 0.73)
     *
     *  It will be treated as is and will not be highlighted
     */

    private var listRates: MutableList<ExchangeRates.Rate> = mutableListOf()
    private var newListRates = listRates
    var isListUpdated = true

    private suspend fun generateAllPossibleRates() {

        while (isListUpdated) {
            reiterateTroughList()
            w("ListSize = ${listRates.size}")
            w("isListUpdated = $isListUpdated")
            delay(500)
        }

        mapPairsWithNewRatesList(listRates)
    }

    private fun reiterateTroughList() {

        for (oldRates in listRates) {

            val oldTriple = Triple(oldRates.from, oldRates.to, oldRates.rate)

            for (newRates in listRates) {

                var newRate = ""
                val newTriple = Triple(newRates.from, newRates.to, newRates.rate)

                try {
                    if (oldTriple.first == newTriple.second && oldTriple.second != newTriple.first) {
                        newRate = String.format(
                            Locale.CANADA,
                            "%.2f",
                            oldTriple.third.toDouble() * newTriple.third.toDouble()
                        ).toString()
                        e("===>   |  new Rate = $newRate for ${oldTriple.second} to ${newTriple.first}")
                        newListRates.add(ExchangeRates.Rate(oldTriple.second, newTriple.first, newRate))
                    }
                    if (oldTriple.second == newTriple.first && oldTriple.first != newTriple.second) {
                        newRate = String.format(
                            Locale.CANADA,
                            "%.2f",
                            oldTriple.third.toDouble() * newTriple.third.toDouble()
                        )
                        e("===>    || new Rate = $newRate for ${newTriple.second} to ${oldTriple.first}")
                        newListRates.add(ExchangeRates.Rate(newTriple.second, oldTriple.first, newRate))
                    }

                } catch (e: NumberFormatException) {
                    e(e)
                }

            }
        }
        w("newListRates.size = ${newListRates.size} =|||= listRates.size = ${listRates.size}")
        if (newListRates.size == listRates.size) {
            isListUpdated = false
        }
        if (newListRates.size > 50) isListUpdated = false
        listRates = listRates.union(newListRates).toMutableList()
    }

    private suspend fun mapPairsWithNewRatesList(listRates: List<ExchangeRates.Rate>) {

        val exchangePairWithRatesList: MutableList<ExchangeRates.Rate> = mutableListOf()

        if (exchangePairsResponse.value != null) {
            for (pair in exchangePairsResponse.value!!.pairs) {
                for (rate in listRates) {
                    if ( pair.from == rate.from) {
                        if (pair.to == rate.to){
                            exchangePairWithRatesList
                                .add(ExchangeRates.Rate(pair.from, pair.to, rate.rate))
                        }
                    }
                }
            }
        } else {
            awaitForServer(exchangePairsResponse.value == null)
        }
        exchangePairsWithRate.value = exchangePairWithRatesList.distinct()
        e("exchangePairsWithRate size = ${exchangePairsWithRate.value?.size}")
    }
}