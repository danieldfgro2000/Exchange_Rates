package dfg.exchangerates.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.exchangerates.domain.repo.ExchangeRateRepository
import dfg.exchangerates.domain.usecase.GetExchangePairsUseCase
import dfg.exchangerates.domain.usecase.GetExchangeRatesUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideGetExchangeRatesUseCase( exchangeRateRepository: ExchangeRateRepository) : GetExchangeRatesUseCase {
        return GetExchangeRatesUseCase(exchangeRateRepository)
    }

    @Singleton
    @Provides
    fun provideGetExchangePairsUseCase( exchangeRateRepository: ExchangeRateRepository) : GetExchangePairsUseCase {
        return GetExchangePairsUseCase(exchangeRateRepository)
    }

}