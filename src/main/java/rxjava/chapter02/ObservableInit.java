package rxjava.chapter02;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableCompletableObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class Launcher_Chapter2 {

    private static int start = 1;
    private static int count = 5;

    public static void main(String[] args) {

        /*
        Cold Observable:
        Observable that doesn’t emit items until a subscriber subscribes.
        If we have more than one subscriber, then observable will emit sequence of items to all subscribers one
        by one.*/

        //observableCreate();
        //observableJust();
        //observableIterable();
        //observerSubscription();
        //observableRange();

        //observableInterval();

        //ObservableDefer(false);

        //connectableObservable();

        //fromCallable(true);

        //singleObservable();

        //maybeObservable();

        //completablefromRunnable();
        //completableComplete();

        disposable();
    }

    static void observableCreate() {

        Observable<String> source;

        System.out.println("Initializing the Observable");
        source = Observable.create(
                emitter -> {
                    try {
                        System.out.println("emitting the onNext1");
                        emitter.onNext("Alpha");
                        System.out.println("emitting the onNext2");
                        emitter.onNext("Beta");
                        System.out.println("emitting the onNext3");
                        emitter.onNext("Gamma");
                        System.out.println("emitting the onNext4");
                        emitter.onNext("Delta");
                        System.out.println("emitting the onNext5");
                        emitter.onNext("Epsilon");
                        System.out.println("emitting the onComplete");
                        emitter.onComplete();

                    } catch (Throwable e) {
                        System.out.println("emitting the onNext");
                        emitter.onError(e);
                    }
                });

        System.out.println("subscribing to Observable");
        //Subscribe to the observable directly
        source.subscribe(s -> System.out.println("RECEIVED: " + s));

        System.out.println("subscribing to Observable with operators");
        //Adding an operator to the Observable and then subscribe
        source.map(s -> s.length())// returns an observable
                .filter(length -> length >= 5)//applies filtering on the data of the returned observable
                .subscribe(s -> System.out.println("RECEIVED: " + s));//trigger the observable OnNext
    }

    static void observableJust() {

        Observable<String> myStrings =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");

        //#2: U can use other operators between observable and observer which in-turn returns observables.
        myStrings.map(s -> s.length()).subscribe(System.out::println);

        //#1: Observable<String> pushed each string object one at a time to our Observer, which we
        //shorthanded using the lambda expression s -> System.out.println(s).
        myStrings.subscribe(System.out::println);
    }

    static void observableIterable() {

        List<String> names = new ArrayList<String>();
        names.add("Alpha");
        names.add("Beta");
        names.add("Gamma");
        names.add("Delta");
        names.add("Epsilon");

        Observable.fromIterable(names)
                .subscribe(System.out::println);
    }

    static void observerSubscription() {

        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");

        System.out.println("Subscribing to observable");
        source.subscribe(System.out::println);

        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe");

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext:: Received " + integer.toString());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        System.out.println("Subscribing along with operators");
     /*   source.map(s -> s.length())
                .filter(l -> l >=5 )
                .subscribe(observer);*/

        /*subscribe(Consumer<? super T> onNext,
        Consumer<? super Throwable> onError,
        Action onComplete)* */
        Consumer<Integer> onNext = i -> System.out.println("RECEIVED: " + i);
        Action onComplete = () -> System.out.println("Done!");
        Consumer<Throwable> onError = Throwable::printStackTrace;

        source.map(s -> s.length())
                .filter(l -> l >= 5)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        Throwable::printStackTrace,
                        () -> System.out.println("Done!"));

    }

    static void observableRange() {
        Observable<Integer> range = Observable.range(1, 20);

        range.subscribe(i -> System.out.println("RECEIVED: " + i),
                Throwable::printStackTrace,
                () -> System.out.println("Done!"));
    }

    /*Look what happened after five seconds elapsed, when Observer 2 came in. Note that it is on its own separate
    timer and starting at 0!
    These two observers are actually getting their own emissions, each starting at 0.
    So this Observable is actually cold.
    */
    static void observableInterval() {

        Observable<Long> intervals =
                Observable.interval(1, TimeUnit.SECONDS);

        //Observer 1
        intervals.subscribe(l -> System.out.println("Observer 1: " + l));

        /* Hold main thread for 5 seconds
        so Observable above has chance to fire */
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            System.out.println(e);
        }

        //Observer 2
        intervals.subscribe(l -> System.out.println("  Observer 2: " + l));

        /*
         * The main thread used to launch our application is not going to wait on this Observable since it fires on a
         * computation thread, not the main thread. Therefore, we use sleep() to pause the main thread for 5000 milliseconds
         * and then allow it to reach the end of the main() method (which will cause the application to terminate).
         * This gives Observable.interval() a chance to fire for a five second window before the application quits.
         */
        //sleep 5 seconds
        try {
            Thread.sleep(8000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /*  Hot Observables:
    They don’t emit the sequence of items again for a new subscriber.
    When an item is emitted by hot observable, all the subscribers that are subscribed will get the emitted item at once.
    If an Observer subscribes to a hot Observable, receives some emissions, and then another Observer comes in
    afterwards, that second Observer will have missed those emissions.*/
    static void connectableObservable() {

        System.out.println("Publishing the Observable ");
        ConnectableObservable<Long> intervals =
                Observable.interval(1, TimeUnit.SECONDS)
                        .publish();//This converts a coldP to hotP.

        //Observer 1
        intervals.subscribe(l -> System.out.println("Observer 1: " + l));

        intervals.connect();

        /* Hold main thread for 5 seconds
        so Observable above has chance to fire */
        try {
            Thread.sleep(6000);
        } catch (Exception e) {
            System.out.println(e);
        }

        //All the values emitted till this stage will be lost for the second observer and it will pick the values
        //emitted from this stage.

        //Observer 2
        intervals.subscribe(l -> System.out.println("  Observer 2: " + l));

        //sleep 5 seconds
        try {
            Thread.sleep(8000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /* When using certain Observable factories, you may run into some nuances if your source is stateful and
    you want to create a separate state for each Observer. Your source Observable may not capture something
    that has changed about its parameters and send emissions that are obsolete.
    public static <T> Observable<T> defer(Func0<Observable<T>> observableFactory)*/
    static void ObservableDefer(boolean viewProblem) {

        Observable<Integer> source;

        if(viewProblem) {
            source = Observable.range(start, count);
        }else {
            //do not create the Observable until the observer subscribes, and create a fresh Observable
            //for each observer
            source = Observable.defer(() -> Observable.range(start,count));
       }

        source.subscribe(i -> System.out.println("Observer 1: " + i));

        //modify count
        count = 10;
        source.subscribe(i -> System.out.println("Observer 2: " + i));

    }

    /*If initializing your emission has a likelihood of throwing an error, you should use
    Observable.fromCallable() instead of Observable.just();
    static <T> Observable<T> fromCallable(java.util.concurrent.Callable<? extends T> func)*/
    static void fromCallable(boolean viewSolution){

        if(!viewSolution) {
            Observable.just(1 / 0)
                    .subscribe(i -> System.out.println("RECEIVED: " + i),
                            e -> System.out.println("Error Captured: " + e));
        }else{
            /*Returns an Observable that, when an observer subscribes to it, invokes a function you specify
            and then emits the value returned from that function.*/
            Observable.fromCallable(() -> 1 / 0)
                    .subscribe(i -> System.out.println("Received: " + i),
                            e -> System.out.println("Error Captured: " + e));

            Callable<Integer> func = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return (2+2*3);
                }
            };

            Observable.fromCallable(func)
                    .subscribe(i -> System.out.println("Received: " + i),
                            e -> System.out.println("Error Captured: " + e));

        }
    }

    /*
    The Single class implements the Reactive Pattern for a single value response.
    Single behaves similarly to Observable except that it can only emit either a single successful value or an error
    (there is no "onComplete" notification as there is for an Observable).
    The Single class implements the SingleSource base interface and the default consumer type it interacts with
    is the SingleObserver via the subscribe(SingleObserver) method.
    The Single operates with the following sequential protocol:
    onSubscribe (onSuccess | onError)?
    Note that onSuccess and onError are mutually exclusive events; unlike Observable, onSuccess is never followed by onError.
    */
    static void singleObservable(){

        int[] arry = new int[]{1,2,3,4};
        String[] name = new String[]{ "Alpha","Beta","Gamma","Delta","Epsilon"};
  /*      String[][] name = new String[][]{
                {"a","b","e","f"},
                {"c","d","g","h"}
        };*/
        // Single operator Demo
        Single.just(name)
                .subscribe(
                s -> System.out.println("Value recevied -> " + Arrays.toString(s)),
                Throwable::printStackTrace);

        /*Single Demo via observable operators:
        Certain RxJava Observable operators will yield a Single, as we will see in the next chapter.
        For instance, the first()*/
        Observable <String> nameArry = Observable.just("Alpha","Beta","Gamma","Delta","Epsilon");
        nameArry.first("No Element Found")// Returns a Single that emits only the very first item emitted by
                // the source ObservableSource or a default item if the source ObservableSource completes without emitting any items.
        .subscribe(s -> System.out.println("Value recevied -> " + s.toString()),
                Throwable::printStackTrace);
    }

    /*
    The Maybe operates with the following sequential protocol: onSubscribe (onSuccess | onError | onComplete)?
    Note that onSuccess, onError and onComplete are mutually exclusive events; unlike Observable, onSuccess is
    never followed by onError or onComplete.
    * */
    static void maybeObservable(){

        // has emission
        Maybe<Integer> presentSource = Maybe.just(100);
        presentSource.subscribe(s -> System.out.println("Process 1 received: " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 1 done!"));

        //no emission
        Maybe<Integer> emptySource = Maybe.empty();
        emptySource.subscribe(s -> System.out.println("Process 2 received: " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 2 done!"));

        //Maybe via Observable operator. Certain Observable operators that we will learn about later yield a Maybe.
        // One example is the firstElement() operator, which is similar to first(), but it returns an empty result
        // if no elements are emitted:
        Observable<String> source =
                Observable.just("Alpha","Beta","Gamma","Delta","Epsilon");
        source.firstElement().subscribe(
                s -> System.out.println("RECEIVED " + s),Throwable::printStackTrace,
                () -> System.out.println("Done!"));
    }

    /*Completable is simply concerned with an action being executed, but it does not receive any emissions.
    Logically, it does not have  onNext() or onSuccess() to receive emissions, but it does have onError() and onComplete():

    You can construct one quickly by calling Completable.complete() or Completable.fromRunnable().
    The former will immediately call onComplete() without doing anything, while fromRunnable() will execute the
    specified action before calling onComplete():*/
    static void completablefromRunnable(){
        Completable completable = Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                System.out.println("Processing the data!");
            }
        });

        System.out.println("Subscribing to completable");
        completable.subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                System.out.println("Completed the processing!!");
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    static void completableComplete(){
        Completable completable = Completable.complete();

        System.out.println("Subscribing to completable");
        completable.subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                System.out.println("Completed the processing!!");
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    static void disposable(){

        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);

        //Approach#1 to subscribe
        System.out.println("--observer Approach#1 ");
        seconds.subscribe(new Observer<Long>() {
            Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;
                System.out.println("onSubscribe(Disposable d) ");
            }

            @Override
            public void onNext(Long aLong) {
                System.out.println("onNext(Long aLong) " + aLong);
                if(aLong == 4)
                    disposable.dispose();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError(Throwable e) " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete() ");
            }
        });

        //Approach#2 to subscribe
        System.out.println("--observer Approach#2 ");
        Disposable disposable =
                seconds.subscribe(
                        new io.reactivex.functions.Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                System.out.println("Consumer:Received:onNext " + aLong);
                            }
                        },
                        new io.reactivex.functions.Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                System.out.println("Throwable:onError " + throwable);

                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                System.out.println("Action:onComplete ");
                            }
                        },
                        new io.reactivex.functions.Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                System.out.println("Consumer<Disposable>():onSubscribe ");
                            }
                        }
                    );

        //sleep 5 seconds
        /* Hold main thread for 5 seconds
        so Observable above has chance to fire */
        try {
            System.out.println("Thread.sleep#1 ");
            Thread.sleep(6000);
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("+++ disposable.dispose() +++");
        //dispose and stop emissions
        disposable.dispose();//Will trigger Consumer<Disposable>() in approach#2

        //sleep 5 seconds to prove
        //there are no more emissions
        /* Hold main thread for 5 seconds
        so Observable above has chance to fire */
        try {
            System.out.println("Thread.sleep#2 ");
            Thread.sleep(8000);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("+++ Exiting disposable:method +++");
    }
}