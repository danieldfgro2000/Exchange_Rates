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
import timber.log.Timber.Forest.d
import timber.log.Timber.Forest.e
import timber.log.Timber.Forest.i
import timber.log.Timber.Forest.w
import java.io.IOException
import java.lang.NumberFormatException
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.roundToLong

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
                        getCurrencyListSize()
                        generateAllPossibleRates()

                    }
                }
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
        for (currency in listRates) {
            if (!list.contains(currency.from)) list.add(currency.from)
            if (!list.contains(currency.to)) list.add(currency.to)
        }
        i("Currency list size = ${list.size}")
        return list.size - 3
    }

    /**
     *
     * Observe that some currencies have inverse transformation (received from API) :
     *      ex: AUD to USD is 1.37 (should be 0.73)
     *
     *  It will be treated as is and will not be highlighted
     */

    private var listRates: MutableList<ExchangeRates.Rate> = mutableListOf()

    private suspend fun generateAllPossibleRates() {
        var count = 0
        while (count < getCurrencyListSize()) {
            count ++
            reiterateTroughList()
            w("ListSize = ${listRates.size}")
            delay(500)
        }
        mapPairsWithNewRatesList(listRates)
    }

    private fun reiterateTroughList() {

        val newListRates = mutableListOf<ExchangeRates.Rate>()

        for (oldRates in listRates) {

            val oldTriple = Triple(oldRates.from, oldRates.to, oldRates.rate)

            for (newRates in listRates) {

                var newRate: String
                var newTransformation: ExchangeRates.Rate
                val newTriple = Triple(newRates.from, newRates.to, newRates.rate)

                try {
                    if (oldTriple.second == newTriple.first
                        && oldTriple.first != newTriple.second) {

                        newRate = ( oldTriple.third.toBigDecimal() * newTriple.third.toBigDecimal())
                            .setScale(2, RoundingMode.HALF_EVEN).toString()

                        w("newRate = $newRate")
                        newTransformation = ExchangeRates.Rate(oldTriple.first, newTriple.second,  newRate )
                        if (!newListRates.contains(newTransformation)) newListRates.add(newTransformation)
                    }
                } catch (e: NumberFormatException) {
                    e(e)
                }
            }
        }
        w("newListRates.size = ${newListRates.size} =|||= listRates.size = ${listRates.size}")
        listRates = listRates.union(newListRates.distinct()).toMutableList()
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