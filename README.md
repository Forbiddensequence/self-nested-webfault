# self-nested-webfault

Проблема вложенного самого в себя WebfaultException возникает при выбрасывании данного исключения в корутине, когда механизм [Stacktrace recovery](https://github.com/Guolianxing/kotlinx.coroutines-cn/blob/master/docs/debugging.md#stacktrace-recovery) пытается дописать в stacktrace исключения часть синхронного вызова внутри корутины. 
На примере метода [webFaultExceptionInKotlin](org.example.KWebFaultExampleKt.webFaultExceptionInKotlin) это следующая часть стека вызова

```kotlin
org.taymyr.lagom.soap.WebFaultException: org.taymyr.lagom.soap.WebFaultException: java.lang.IllegalStateException: test error
	....
    at _COROUTINE._BOUNDARY._(CoroutineDebugging.kt:42)
	at org.example.KWebFaultExampleKt$webFaultExceptionInKotlin$1.invokeSuspend(KWebFaultExample.kt:21)
```
И таким образом WebFaultException, который должен в себе содержать целевое SOAP исключение содержит в себе исходный WebFaultException с нужным исключением, генерируемым SOAP сервисом