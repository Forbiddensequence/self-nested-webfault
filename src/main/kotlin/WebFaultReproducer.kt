@file:JvmName("WebFaultReproducer")

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    try {
        throwEx()
    } catch (e: Exception) {
        processException(e)
    }
    try {
        throwExFromNewScope()
    } catch (e: Exception) {
        processException(e)
    }
}

private fun processException(e: Throwable) {
    if (e is IllegalStateException && e.cause is IllegalArgumentException) {
        println("Got correctly exception: IllegalStateException <- IllegalArgumentException")
    } else {
        e.printStackTrace()
    }
}

private suspend fun throwExFromNewScope() = coroutineScope {
    throwEx()
}

private suspend fun throwEx() {
    throw IllegalStateException(IllegalArgumentException("error"))
}
