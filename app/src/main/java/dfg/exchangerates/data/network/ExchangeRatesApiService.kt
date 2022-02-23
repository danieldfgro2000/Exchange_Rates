package dfg.exchangerates.data.network

import dfg.exchangerates.data.model.ExchangeRates
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ExchangeRatesApiService @Inject constructor(
    exchangeRatesAPI: ExchangeRatesAPI
) {
    private var mExchangeRatesAPI = exchangeRatesAPI

    fun getExchangeRates() : Single<ExchangeRates.Rates> {
        return mExchangeRatesAPI.getExchangeRates()
    }

    fun getExchangePairs() : Single<ExchangeRates.Pairs> {
        return mExchangeRatesAPI.getExchangePairs()
    }
}