package dfg.exchangerates.data.model

import android.provider.Telephony
import com.google.gson.annotations.SerializedName

object ExchangeRates {

    data class  Rates (
        @SerializedName("Rates") val rates: List<Rate>
    )

    data class Rate (
        @SerializedName("from") val from: String,
        @SerializedName("to") val to: String,
        @SerializedName("rate") val rate: String
    )

    data class Pairs (
        @SerializedName("Pairs") val pairs: List<Pair>
    )

    data class Pair (
        @SerializedName("from") val from: String,
        @SerializedName("to") val to: String
    )
}