package dfg.exchangerates.data.network

import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRatesAPI {

    @GET(Constants.RATES_ENDPOINT)
    fun getExchangeRates() : Single<ExchangeRates.Rates>

    @GET(Constants.RATES_ENDPOINT)
    fun getExchangePairs() : Single<ExchangeRates.Pairs>
}