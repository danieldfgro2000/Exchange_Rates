package dfg.exchangerates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import dfg.exchangerates.data.model.ExchangeRates
import dfg.exchangerates.databinding.ActivityMainBinding
import dfg.exchangerates.presentation.adapter.ExchangeRatesAdapter
import dfg.exchangerates.presentation.viewmodel.ExchangeRatesViewModel
import org.json.JSONObject
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.Forest.e
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var  mExchangeRatesViewModel: ExchangeRatesViewModel

    @Inject
    lateinit var exchangeRatesAdapter: ExchangeRatesAdapter

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        mExchangeRatesViewModel = ViewModelProvider(this)
                .get(ExchangeRatesViewModel::class.java)


        mExchangeRatesViewModel.exchangeRatesResponse.observe(this){
            initRecyclerView()
            exchangeRatesAdapter.differ.submitList(it.rates)
        }

        mExchangeRatesViewModel.exchangePairsResponse.observe(this) {
            e("Pairs = $it")
        }
    }

    private fun initRecyclerView() {
        exchangeRatesAdapter = ExchangeRatesAdapter()
        mBinding.rvItemsList.apply {
            adapter = exchangeRatesAdapter
            setHasFixedSize(false)
        }
    }
}