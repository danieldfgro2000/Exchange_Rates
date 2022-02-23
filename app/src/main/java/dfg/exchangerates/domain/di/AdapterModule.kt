package dfg.exchangerates.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dfg.exchangerates.presentation.adapter.ExchangeRatesAdapter

@Module
@InstallIn(SingletonComponent::class)
class AdapterModule {

    @Provides
    fun provideExchangeRatesAdapter() : ExchangeRatesAdapter {
        return ExchangeRatesAdapter()
    }
}