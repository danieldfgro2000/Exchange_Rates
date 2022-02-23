package dfg.exchangerates.data.model

import android.provider.Telephony
import com.google.gson.annotations.SerializedName

object ExchangeRates {

    data class  Rates (
        val rates: List<Rate>
    )

    data class Rate (
        val from: String,
        val to: String,
        val rate: String
    )

    data class Pairs (
        val pairs: List<Pair>
    )

    data class Pair (
        val from: String,
        val to: String
    )
}