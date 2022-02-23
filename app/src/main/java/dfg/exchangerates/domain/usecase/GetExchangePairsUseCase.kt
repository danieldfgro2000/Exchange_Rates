package dfg.exchangerates.domain.usecase

import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.domain.repo.ExchangeRateRepository

class GetExchangePairsUseCase (private val exchangeRateRepository: ExchangeRateRepository) {
    suspend fun execute() : ExchangeRates.Pairs? {
        return exchangeRateRepository.getExchangePairs()
    }
}