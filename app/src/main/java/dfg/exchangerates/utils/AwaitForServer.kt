package dfg.exchangerates.utils

import kotlinx.coroutines.delay

suspend fun awaitForServer(isWaitingForResponse: Boolean) {
    var countSeconds = 0
    val timeOut = 20 // 2 sec

    while (isWaitingForResponse) {
        countSeconds++
        if (countSeconds > timeOut) break
        delay(100)
    }
}