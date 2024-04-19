val ex = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.i("CoroutineTest", "ExceptionHandler. $coroutineContext $throwable")
    }
val scope = CoroutineScope(Dispatchers.IO + Job() + ex)
val job1 = scope.launch(SupervisorJob()) {
    Log.i("CoroutineTest", "hello job1 start")
    delay(100)
    Log.i("CoroutineTest", "hello job1 end")
}
job1.invokeOnCompletion {
    Log.i("CoroutineTest", "job1 complete. $it")
    Log.i("CoroutineTest", "isScopeActive: ${scope.isActive}")
}
val job2 = scope.launch(SupervisorJob()) {
    Log.i("CoroutineTest", "hello job2 start")
    throw error("throwing IllegalStateException")
    delay(100)
    Log.i("CoroutineTest", "hello job2 end")
}
job2.invokeOnCompletion {
    Log.i("CoroutineTest", "job2 complete. $it")
    Log.i("CoroutineTest", "isScopeActive: ${scope.isActive}")
}
