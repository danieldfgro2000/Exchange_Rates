package dfg.exchangerates.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dfg.exchangerates.R
import dfg.exchangerates.databinding.ActivityMainBinding
import dfg.exchangerates.presentation.adapter.ExchangeRatesAdapter
import dfg.exchangerates.presentation.viewmodel.ExchangeRatesViewModel
import timber.log.Timber
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

        hideRecyclerView()

        mExchangeRatesViewModel.exchangePairsWithRate.observe(this){
            initRecyclerView()
            showRecyclerView()
            exchangeRatesAdapter.differ.submitList(it)
        }

        mBinding.button.setOnClickListener {
            mExchangeRatesViewModel.getExchangeRates()
        }
    }

    private fun initRecyclerView() {
        exchangeRatesAdapter = ExchangeRatesAdapter()
        mBinding.rvItemsList.apply {
            adapter = exchangeRatesAdapter
            setHasFixedSize(false)
        }
    }

    private fun showRecyclerView() {
        with(mBinding){
            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
        }
    }

    private fun hideRecyclerView() {
        with(mBinding) {
            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }

    }
}