package dfg.exchangerates.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.exchangerates.data.network.ExchangeRatesAPI
import dfg.exchangerates.domain.repo.ExchangeRateRepository
import dfg.exchangerates.domain.repo.ExchangeRatesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepoModule {

    @Singleton
    @Provides
    fun provideExchangeRatesRepository(
        exchangeRatesAPI: ExchangeRatesAPI
    ) : ExchangeRateRepository {
        return ExchangeRatesRepositoryImpl(exchangeRatesAPI)
    }
}