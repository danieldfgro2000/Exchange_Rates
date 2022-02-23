package dfg.exchangerates.data.model

object ExchangeRates {

    data class  Rates (
        var rates: List<Rate>
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