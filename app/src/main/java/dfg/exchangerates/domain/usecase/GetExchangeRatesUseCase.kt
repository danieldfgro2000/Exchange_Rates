package dfg.exchangerates.domain.usecase

import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.domain.repo.ExchangeRateRepository

class GetExchangeRatesUseCase (private val exchangeRatesRepository: ExchangeRateRepository) {
    suspend fun execute() : ExchangeRates.Rates? {
        return exchangeRatesRepository.getExchangeRates()
    }
}