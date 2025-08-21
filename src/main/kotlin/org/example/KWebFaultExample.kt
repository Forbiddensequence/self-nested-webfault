package org.example

import org.example.JWebFaultExample.webFaultExceptionInJava
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import org.taymyr.lagom.soap.WebFaultException
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

fun main() {
    webFaultExceptionInKotlin()
    webFaultExceptionInJava()
}

private fun webFaultExceptionInKotlin() {
    runBlocking {
        try {
            val res = testFunction(false).await()
            println(res)
        } catch (e: Exception) {
            if (!(e is WebFaultException && e.cause is IllegalStateException)) {
                println("Expected exception hierarchy in kotlin is broken, caught exception cause type is ${e.cause!!::class.java}")
                e.printStackTrace()
            }
        }
    }
}

@Throws(WebFaultException::class)
fun CoroutineScope.testFunction(success: Boolean): CompletableFuture<Int> = future {
    println("entered in test kotlin function")
    delay(5000)
    println("have waited in test kotlin function")
    1337
}.handle { result, throwable ->
    if (success)
        result
    else
        throw WebFaultException(IllegalStateException("test error"))
}