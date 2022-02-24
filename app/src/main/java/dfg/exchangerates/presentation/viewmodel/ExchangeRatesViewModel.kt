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
import timber.log.Timber.Forest.i
import timber.log.Timber.Forest.w
import java.io.IOException
import java.lang.NumberFormatException
import java.math.RoundingMode
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
    val isLoading = MutableLiveData<Boolean>()

    private fun getExchangeRates() {

        try {
            viewModelScope.launch(Main) {
                isLoading.value = true

                viewModelScope.launch(IO) {  getExchangeRatesUseCase.execute()?.let {
                    listOfExchangeRates = it.rates.toMutableList()
                }}.join()

                getCurrencyListSize()
                generateAllPossibleRates()
                isLoading.value = false

            }
        } catch (e: IOException) {
            e(e)
        }
    }

    /**
     *  1st pair is identical with 4th pair (retrieved from API)
     */

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

    private fun getCurrencyListSize() : Int {
        val list: MutableList<String> = mutableListOf()
        for (currency in listOfExchangeRates) {
            if (!list.contains(currency.from)) list.add(currency.from)
            if (!list.contains(currency.to)) list.add(currency.to)
        }
        i("Currency list size = ${list.size}")
        return list.size
    }

    /**
     *
     * Observe that some currencies have inverse transformation (received from API) :
     *      ex: AUD to USD is 1.37 (should be 0.73)
     *
     *  It will be treated as is and will not be highlighted
     */

    private var listOfExchangeRates: MutableList<ExchangeRates.Rate> = mutableListOf()
    private val temporaryListOfExchangeRates = mutableListOf<ExchangeRates.Rate>()

    private suspend fun generateAllPossibleRates() {
        var count = 0
        while (count < getCurrencyListSize()) {
            count ++
            generateNewRates()
        }
        mapPairsWithNewRatesList(listOfExchangeRates)
    }

    private fun generateNewRates() {

        listOfExchangeRates.forEach { oldRates ->

            val oldTriple = Triple(oldRates.from, oldRates.to, oldRates.rate)
            i("oldTriple = $oldTriple")

            listOfExchangeRates.forEach { newRates ->

                val newTriple = Triple(newRates.from, newRates.to, newRates.rate)

                try {
                    if (oldTriple.second == newTriple.first
                        && oldTriple.first != newTriple.second) {
                        i("newTriple = $newTriple")
                        val newRate = ( oldTriple.third.toBigDecimal() * newTriple.third.toBigDecimal())
                            .setScale(2, RoundingMode.HALF_UP).toString()

                        if(!isAlreadyInserted(oldTriple.first, newTriple.second)) {
                            w("Inserting :: ${ExchangeRates.Rate(oldTriple.first, newTriple.second,  newRate )}")
                            temporaryListOfExchangeRates
                                .add(ExchangeRates.Rate(oldTriple.first, newTriple.second,  newRate ))
                        } else e("Skipping")
                    }
                } catch (e: NumberFormatException) {
                    e(e)
                }
            }
        }

        listOfExchangeRates = listOfExchangeRates
            .union(temporaryListOfExchangeRates.distinct()).toMutableList()
    }

    private fun isAlreadyInserted (fromCurrency: String, toCurrency: String) : Boolean {
        var isAlreadyInserted = false

        listOfExchangeRates.forEach { rate ->
            if (rate.from == fromCurrency && rate.to == toCurrency) {
                isAlreadyInserted = true
            }
        }

        if (!isAlreadyInserted) {
            temporaryListOfExchangeRates.forEach { rate ->
                if (rate.from == fromCurrency && rate.to == toCurrency) {
                    isAlreadyInserted = true
                }
            }
        }

        return isAlreadyInserted
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