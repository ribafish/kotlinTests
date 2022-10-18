import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.Integer.min
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


fun main() {

//    timerPublishSubject()
//    errorHandling()
//    errorHandling2()
//    errorHandling3()
//    parallelList()
    testPages()
}

fun parallelList() {
    val subject = PublishSubject.create<Int>()

    val filterValues = listOf(1,2,3)
    Completable.merge(filterValues.map { filterValue ->
        subject
            .filter { value ->
                filterValue == value
            }
            .firstOrError()
            .ignoreElement()
    })
        .timeout(1, TimeUnit.SECONDS)
        .subscribeBy(
            onError = {
                it.printStackTrace()
            },
            onComplete = {
                println("All completed")
            }
        )
    for(i in 2..10) {
        subject.onNext(i)
    }
    runBlocking {
        delay(2000)
    }
}

fun checkStuff() = Single.create<Long> { emitter ->
    val time = System.currentTimeMillis()
    runBlocking {
        delay(50)
        if (Random(time).nextBoolean()) {
            emitter.onSuccess(time)
        } else {
            emitter.onError(IllegalStateException("wrong time1: $time -> time2: ${java.lang.System.currentTimeMillis()}"))
        }
    }
}

fun observeStuff(): Disposable {
    return checkStuff()
        .doOnSuccess {
            println(it)
        }
        .ignoreElement()
//        .subscribeOn(scheduler)
//        .observeOn(Schedulers.single())
        .subscribeBy(
        onComplete = {
            println("complete")
        },
        onError = {
            println(it.message)
        }
    )
}

fun errorHandling3() {
    val cd = CompositeDisposable()
    runBlocking {
        for (i in 0..10) {
            cd.add(observeStuff())
            delay(100)
        }
        cd.dispose()
        println("dispose immediately")
        observeStuff().dispose()
        observeStuff().dispose()
        observeStuff().dispose()
    }

}

interface Listn{
    fun onNext(value: Boolean)
    fun onError()
}

var scheduler = Schedulers.from(Executors.newCachedThreadPool { r -> //result.setDaemon(true);
    Thread(r)
})

fun errorHandling2() {
    var errorInject: Listn? = null

    val singl = Single.create<Boolean> { emitter ->
        println("Single.create run")
        errorInject = object : Listn {
            override fun onNext(value: Boolean) {
                emitter.onSuccess(value)
            }

            override fun onError() {
                println("emitter.tryOnError returned ${emitter.tryOnError(IllegalStateException("Error from single.create"))}")
            }
        }
//        emitter.onError(IllegalStateException("Error from single.create"))
    }
        .retry(5)

    val behSubject = BehaviorSubject.create<Boolean>()

    val d = Single.zip(singl, Single.just(true)) { i, j -> i and j }
//        .subscribeOn(scheduler)
        .toObservable()
        .subscribeBy(onNext = { behSubject.onNext(it) },
            onError = {
                behSubject.onError(it)
            })

//    val d = singl
////        .subscribeOn(Schedulers.io())
//        .subscribeBy(
//            onSuccess = {
//                println("singl.onNext $it")
//                behSubject.onNext(it)
//            },
////            onNext = {
////                println("singl.onNext $it")
////                behSubject.onNext(it)
////            },
////            onComplete = {
////                println("singl.onComplete")
////            },
//            onError = {
//                println("singl.onError $it")
//                behSubject.onError(it)
//            })

    val disposable = behSubject.doOnDispose { d.dispose() }.subscribeBy(onError = {
        println("BehSubject onError: $it")
//        it.printStackTrace()
    }, onNext = {
        println("BehSubject OnNext: $it")
    }, onComplete = {
        println("BehSubject onComplete")
    })

    errorInject?.onError()
    errorInject?.onError()
    errorInject?.onError()
    errorInject?.onError()
    errorInject?.onError()
    errorInject?.onNext(true)
    disposable.dispose()

    println("Disposed!")
    errorInject?.onError()
    errorInject?.onError()
    errorInject?.onError()

    runBlocking {
        delay(1000L)
    }
}

fun errorHandling() {
    val responseObservable = Observable.fromCallable {
        1
    }

    val observable = responseObservable
        .flatMap { r ->
            Observable.just(r)
                .map { handleErrorResponse(r) }
                .onErrorResumeNext(Function { throwable ->
                    handleError<Int>(throwable)
                })
        }
        .subscribeOn(Schedulers.io())
//        .toFlowable(BackpressureStrategy.LATEST)
        .singleElement()
//        .singleOrError()
//        .ignoreElements()
//    try {
        RxJavaPlugins.onAssembly(observable)
            .observeOn(Schedulers.single())
                /*
            .subscribeBy(
//            onSuccess = {
                onComplete = {
                    println("onComplete")
                },
//            onError = {
//                println("onError: $it")
////                it.printStackTrace()
//            },
                onNext = {
                    println("onNext: $it")
                }
            )*/
            .blockingGet().also { println("blockingGet done: $it") }
//    } catch (e: Exception) {
//        println("Caught exception: $e")
//    }

    runBlocking {
        delay(1000L)
    }
}

private fun handleErrorResponse(a: Any): Any {
    println("handleErrorResponse")
    throw ApiException("test")
}

private fun <T> handleError(error: Throwable): Observable<T> {
    println("handleError")
//    error.printStackTrace()
    return Observable.error(error)
}

data class ApiException(val customCause: String): RuntimeException() {

}

fun timerPublishSubject() {
    val _timer = Observable.interval(1, TimeUnit.SECONDS).publish()
    val timer = BehaviorSubject.createDefault(-1L)
    val _disp = _timer.subscribe(timer)
    val state = Observable.just("state")

    runBlocking {
        delay(200L)
        println("connect")
        _timer.connect()

        delay(2200L)
        println("subscribeBy")
        val disp = Observable.combineLatest(state, timer) { state, timer ->
            state + timer
        }.subscribeBy(
            onNext = {
                println(it)
            }
        )

        delay(3000L)
        disp.dispose()
    }
}

fun testPages() {
    data class PagedData<T>(val page: Int, val pageSize: Int, val totalCount: Int, val data: List<T>)

    fun generatePagesData(size: Int) = mutableListOf<Int>().apply{
        for (i in 0 until size) {
            add(i)
        }
    }

    fun getPagesSize(pagesData: List<Int>): Single<Int>{
        println("getPagesSize")
        return Single.just(pagesData.size)
    }

    fun getData(pagesData: List<Int>, fromPosition: Int, count: Int): Single<List<Int>> {
        println("getData fromPosition $fromPosition count $count")
        val endPosition = min(fromPosition + count, pagesData.size)
        return Single.just(pagesData.subList(fromPosition, endPosition)).delay(50, TimeUnit.MILLISECONDS)
    }

    fun getPages(pagesData: List<Int>, pageSize: Int = 10): Flowable<PagedData<Int>> {
        println("getPages")
        return getPagesSize(pagesData)
            .flatMapObservable{ totalCount ->
                val additionalPage = if (totalCount % pageSize != 0) 1 else 0
                val pagesCount = (totalCount / pageSize) + additionalPage
                Observable.range(0, pagesCount)
                    .map { it to totalCount }
            }
            .toFlowable(BackpressureStrategy.BUFFER)
            .concatMap { (page, totalCount) ->
                val fromPosition = page * pageSize
                getData(pagesData, fromPosition, pageSize)
                    .map { PagedData(page, pageSize, totalCount, it) }
                    .toFlowable()
            }
    }

    fun doTest(size: Int) = runBlocking {
        val list = generatePagesData(size)
        println("\n\ndoTest size $size listSize ${list.size}")
        getPages(list)
            .subscribeBy(
                onNext = {
                    println("onNext: ${it.page}, ${it.pageSize}, ${it.totalCount}, size: ${it.data.size}, [${it.data.first()}->${it.data.last()}]")
                },
                onComplete = {
                    println("onComplete")
                },
                onError = {
                    it.printStackTrace()
                }
            )
        delay(2000)
    }

    doTest(5)
    doTest(10)
    doTest(52)
}
