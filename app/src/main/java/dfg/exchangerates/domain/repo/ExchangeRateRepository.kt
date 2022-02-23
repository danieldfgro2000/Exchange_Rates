package dfg.exchangerates.domain.repo

import dfg.exchangerates.data.model.ExchangeRates

interface ExchangeRateRepository {

    suspend fun getExchangeRates() : ExchangeRates.Rates?

    suspend fun getExchangePairs() : ExchangeRates.Pairs?
}