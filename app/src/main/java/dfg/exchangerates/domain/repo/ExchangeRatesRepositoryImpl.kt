package dfg.exchangerates.domain.repo

import dfg.exchangerates.data.model.ExchangeRates
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class ExchangeRatesRepositoryImpl(
) : ExchangeRateRepository {

    private val compositeDisposable = CompositeDisposable()

    fun getExchangeRates() {
        compositeDisposable.add(
            exchangeRatesApiService.getExchangeRates()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ExchangeRates.Rates>() {
                    override fun onSuccess(t: ExchangeRates.Rates) {
                        exchangeRatesResponse.value = t
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )
    }

    fun getExchangePairs() {
        compositeDisposable.add(
            exchangeRatesApiService.getExchangePairs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ExchangeRates.Pairs>() {
                    override fun onSuccess(t: ExchangeRates.Pairs) {
                        exchangePairsResponse.value = t
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )
    }
}