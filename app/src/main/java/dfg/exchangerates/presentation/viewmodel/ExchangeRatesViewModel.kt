package dfg.exchangerates.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dfg.exchangerates.data.model.ExchangeRates

class ExchangeRatesViewModel : ViewModel() {




    val exchangeRatesResponse = MutableLiveData<ExchangeRates.Rates>()
    val exchangePairsResponse = MutableLiveData<ExchangeRates.Pairs>()


}