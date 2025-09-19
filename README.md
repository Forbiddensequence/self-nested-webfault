# self-nested-webfault

Проблема вложенного самого в себя WebFaultException возникает при выбрасывании данного исключения в корутине, когда механизм [Stacktrace recovery](https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/topics/debugging.md#stacktrace-recovery) пытается дописать в stacktrace исключения часть синхронного вызова внутри корутины. 
На примере метода [handleByTry]([WebFaultReproducer.kt](src/main/kotlin/WebFaultReproducer.kt)) это следующая часть стека вызова

```kotlin
WebFaultException: WebFaultException: java.lang.IllegalStateException: error
    at _COROUTINE._BOUNDARY._(CoroutineDebugging.kt:42)
    at WebFaultReproducer.handleByTry(WebFaultReproducer.kt:16)
    at WebFaultReproducer.main(WebFaultReproducer.kt:11)
```
И таким образом `WebFaultException`, который должен в себе содержать целевое `IllegalStateException` содержит в себе исходный `WebFaultException` с нужным исключением

## Шаги воспроизведения
```bash
./mvnw clean package
# Expected behavior
java -jar ./target/web-fault-problem-1.0-SNAPSHOT.jar
# Unexpected behavior
java -ea -jar ./target/web-fault-problem-1.0-SNAPSHOT.jar
```
