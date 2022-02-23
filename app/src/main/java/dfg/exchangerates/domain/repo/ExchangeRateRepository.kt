package dfg.exchangerates.domain.repo

import dfg.exchangerates.data.model.ExchangeRates

interface ExchangeRateRepository {

    fun getExchangeRates() : ExchangeRates.Rates

    fun getExchangePairs() : ExchangeRates.Pairs
}