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

## Шаги воспроизведения
Для начала нужно собрать запускаемый jar файл
```bash
./mvnw clean package
```
После этого запускаем файл без флага -ea
```bash 
cmd> java -jar .\target\web-fault-problem-1.0-SNAPSHOT.jar
entered in test kotlin function
have waited in test kotlin function
Entered in test java function
Have waited in test java function

```
Запускаем тот же jar файл с флагом -ea
После этого запускаем файл без флага -ea
```bash 
cmd> java -jar .\target\web-fault-problem-1.0-SNAPSHOT.jar
entered in test kotlin function
have waited in test kotlin function
Expected exception hierarchy in kotlin is broken, caught exception cause type is class org.taymyr.lagom.soap.WebFaultException
org.taymyr.lagom.soap.WebFaultException: org.taymyr.lagom.soap.WebFaultException: java.lang.IllegalStateException: test error
        at org.example.KWebFaultExampleKt.testFunction$lambda$0(KWebFaultExample.kt:42)
        at org.example.KWebFaultExampleKt.testFunction$lambda$1(KWebFaultExample.kt:38)
        at java.base/java.util.concurrent.CompletableFuture.uniHandle(CompletableFuture.java:930)
        at java.base/java.util.concurrent.CompletableFuture$UniHandle.tryFire(CompletableFuture.java:907)
        at java.base/java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:506)
        at java.base/java.util.concurrent.CompletableFuture.complete(CompletableFuture.java:2073)
        at kotlinx.coroutines.future.CompletableFutureCoroutine.onCompleted(Future.kt:53)
        at kotlinx.coroutines.AbstractCoroutine.onCompletionInternal(AbstractCoroutine.kt:91)
        at kotlinx.coroutines.JobSupport.tryFinalizeSimpleState(JobSupport.kt:287)
        at kotlinx.coroutines.JobSupport.tryMakeCompleting(JobSupport.kt:887)
        at kotlinx.coroutines.JobSupport.makeCompletingOnce$kotlinx_coroutines_core(JobSupport.kt:859)
        at kotlinx.coroutines.AbstractCoroutine.resumeWith(AbstractCoroutine.kt:98)
        at _COROUTINE._BOUNDARY._(CoroutineDebugging.kt:42)
        at org.example.KWebFaultExampleKt$webFaultExceptionInKotlin$1.invokeSuspend(KWebFaultExample.kt:21)
Caused by: org.taymyr.lagom.soap.WebFaultException: java.lang.IllegalStateException: test error
        at org.example.KWebFaultExampleKt.testFunction$lambda$0(KWebFaultExample.kt:42)
        at org.example.KWebFaultExampleKt.testFunction$lambda$1(KWebFaultExample.kt:38)
        at java.base/java.util.concurrent.CompletableFuture.uniHandle(CompletableFuture.java:930)
        at java.base/java.util.concurrent.CompletableFuture$UniHandle.tryFire(CompletableFuture.java:907)
        at java.base/java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:506)
        at java.base/java.util.concurrent.CompletableFuture.complete(CompletableFuture.java:2073)
        at kotlinx.coroutines.future.CompletableFutureCoroutine.onCompleted(Future.kt:53)
        at kotlinx.coroutines.AbstractCoroutine.onCompletionInternal(AbstractCoroutine.kt:91)
        at kotlinx.coroutines.JobSupport.tryFinalizeSimpleState(JobSupport.kt:287)
        at kotlinx.coroutines.JobSupport.tryMakeCompleting(JobSupport.kt:887)
        at kotlinx.coroutines.JobSupport.makeCompletingOnce$kotlinx_coroutines_core(JobSupport.kt:859)
        at kotlinx.coroutines.AbstractCoroutine.resumeWith(AbstractCoroutine.kt:98)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:46)
        at kotlinx.coroutines.DispatchedTaskKt.resume(DispatchedTask.kt:221)
        at kotlinx.coroutines.DispatchedTaskKt.dispatch(DispatchedTask.kt:154)
        at kotlinx.coroutines.CancellableContinuationImpl.dispatchResume(CancellableContinuationImpl.kt:470)
        at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl$kotlinx_coroutines_core(CancellableContinuationImpl.kt:504)
        at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl$kotlinx_coroutines_core$default(CancellableContinuationImpl.kt:493)
        at kotlinx.coroutines.EventLoopImplBase$DelayedResumeTask.run(EventLoop.common.kt:497)
        at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:263)
        at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:95)
        at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source)
        at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:47)
        at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source)
        at org.example.KWebFaultExampleKt.webFaultExceptionInKotlin(KWebFaultExample.kt:19)
        at org.example.KWebFaultExampleKt.main(KWebFaultExample.kt:14)
        at org.example.KWebFaultExampleKt.main(KWebFaultExample.kt)
Caused by: java.lang.IllegalStateException: test error
Entered in test java function
Have waited in test java function

```