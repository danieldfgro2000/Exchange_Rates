package dfg.exchangerates.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import java.lang.Exception
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

    val exchangeRatesResponse = MutableLiveData<ExchangeRates.Rates>()
    val exchangePairsResponse = MutableLiveData<ExchangeRates.Pairs>()

    private fun getExchangeRates() {
        try {
            viewModelScope.launch(IO) {
                getExchangeRatesUseCase.execute()?.let {
                    viewModelScope.launch(Main) {
                        exchangeRatesResponse.value = it
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
}