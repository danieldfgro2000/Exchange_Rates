package dfg.exchangerates.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dfg.exchangerates.data.model.ExchangeRates

@HiltViewModel
class ExchangeRatesViewModel : ViewModel() {

    val exchangeRatesResponse = MutableLiveData<ExchangeRates.Rates>()
    val exchangePairsResponse = MutableLiveData<ExchangeRates.Pairs>()


}