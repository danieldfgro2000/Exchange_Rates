package dfg.exchangerates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        mExchangeRatesViewModel = ViewModelProvider(this)
                .get(ExchangeRatesViewModel::class.java)


        mExchangeRatesViewModel.exchangeRatesResponse.observe(this){
            e("Rates = $it")
            initRecyclerView()
//            val json = JSONObject(it)
//            try {
//                val resultArray = json.getJSONArray("rates")
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            exchangeRatesAdapter.differ.submitList()

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