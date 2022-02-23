package dfg.exchangerates.domain.repo

import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.data.network.ExchangeRatesAPI
import dfg.exchangerates.utils.awaitForServer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber.Forest.e

class ExchangeRatesRepositoryImpl(
    private val exchangeRatesAPI: ExchangeRatesAPI
) : ExchangeRateRepository {

    private val compositeDisposable = CompositeDisposable()

    override suspend fun getExchangeRates() : ExchangeRates.Rates? {
        var list: ExchangeRates.Rates? = null
        compositeDisposable.add(
            exchangeRatesAPI.getExchangeRates()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ExchangeRates.Rates>() {
                    override fun onSuccess(t: ExchangeRates.Rates) {
                        list = t
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )
        awaitForServer(list == null)
        return list
    }

    override suspend fun getExchangePairs() : ExchangeRates.Pairs? {
        var list: ExchangeRates.Pairs? = null
        compositeDisposable.add(
            exchangeRatesAPI.getExchangePairs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ExchangeRates.Pairs>() {
                    override fun onSuccess(t: ExchangeRates.Pairs) {
                        list = t
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        )
        awaitForServer(list == null)
        return list
    }
}