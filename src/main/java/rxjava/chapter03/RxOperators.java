package rxjava.chapter03;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/* https://github.com/ReactiveX/RxJava/wiki/Mathematical-and-Aggregate-Operators#collectinto */
public class RxOperators {

    public static void main (String ... args){
        Observable observable = Observable.just("Alpha", "Beta","Alpha","Alpha", "Gamma", "Delta", "Epsilon","Alludu");
        //suppressingOperators(observable);

        collectorOperators(observable);
    }


    /*There are a number of operators that will suppress emissions that fail to meet a specified criterion.
    These operators work by simply not calling the onNext() function downstream for a disqualified emission,
    and therefore does not go down the chain to Observer.
    All the operators in-turn return observable which can be then subscribed.
    */
    static void suppressingOperators(Observable o){

        //filters(o);
        //take(o);
        //skip(o);
        //distinct(o);
        //elementAt();
    }

    /*Collection operators will accumulate all emissions into a collection such as a list or map and then emit that
    entire collection as a single emission. Collection operators are another form of reducing operators since they
    consolidate emissions into a single one.*/
    static void collectorOperators(Observable o){
        //toList(o);
        //toSortedList();
        //toMap(o);
        toCollect();
    }

    /*The  filter() operator accepts Predicate<T> for a given Observable<T>. This means that you provide it a lambda
    that qualifies each emission by mapping it to a Boolean value, and emissions with false will not go forward.*/
    static void filters(Observable o){

        System.out.println("Initializing filter operator ");
        System.out.println("Subscribe to observable ");
        subscribeToObservable(o.filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                System.out.println("Value filter: " + s);
                return (s.length() < 3);
            }
        }));
    }

    static void take(Observable o){
        System.out.println("Initializing take operator ");
        System.out.println("Subscribe to observable ");

        //Option#1
        //subscribeToObservable(o.take(3));

        try{
            //Option#2
            subscribeToObservable(Observable.interval(300, TimeUnit.MILLISECONDS)
                    .take(3,TimeUnit.SECONDS));
            Thread.sleep(5000);
        }catch(Exception e){

        }
    }

    static void skip(){

        System.out.println("Initializing skip operator ");
        System.out.println("Subscribe to observable ");

        /*subscribeToObservable(Observable.range(1,20)
                .skip(10));*/

        /*will skip the last specified number of items (or time duration) before the onComplete() event is called.
        Just keep in mind that the skipLast() operator will queue and delay emissions until it confirms the last
        emissions in that scope.*/
        subscribeToObservable(Observable.range(1,20)
                .skipLast(5));
    }

    static void distinct(Observable o){
        System.out.println("Initializing distinct operator ");
        System.out.println("Subscribe to observable ");
        subscribeToObservable(Observable.just(1, 1, 1, 2, 2, 3, 3, 2, 1, 1)
                .distinct());

        /*subscribeToObservable(o.map(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o.toString().length();
            }
        }).distinct());*/

        /*subscribeToObservable(o.distinct(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o.toString().length();
            }
        }));*/

       /*distinctUntilChanged() function will ignore duplicate consecutive emissions.
       It is a helpful way to ignore repetitions until they change.
       If the same value is being emitted repeatedly, all the duplicates will be ignored
       until a new value is emitted.
       Duplicates of the next value will be ignored until it changes again, and so on. */

       /* subscribeToObservable(Observable.just(1, 1, 1, 2, 2, 3, 3, 2, 1, 1)
                .distinctUntilChanged());*/
        //subscribeToObservable(o.distinctUntilChanged());
    /*    subscribeToObservable(o.distinctUntilChanged(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o.toString().length();
            }
        }));*/


    }

    static void elementAt(){

        System.out.println("Initializing distinct operator ");
        System.out.println("Subscribe to observable ");
        subscribeToMabyBe(Observable.just(1,2,3,4,5,6).elementAt(3));
        subscribeToSingle(Observable.just(1,2,3,4,5,6).elementAtOrError(7));

    }
    /* A given Observable<T>, it will collect incoming emissions into a List<T> and then push that entire List<T> as
    a single emission (through Single<List<T>>).*/
    static void toList(Observable o){
        subscribeToSingle(o.toList());
    }

    static void toSortedList(){
        System.out.println("Initializing sorting operator ");
        System.out.println("Subscribe to observable ");
        subscribeToSingle(Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3).toSortedList());
    }

    static void toMap(Observable o){
        System.out.println("Initializing Map operator ");
        System.out.println("Subscribe to observable ");

        subscribeToSingle(o.toMap(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o.toString().charAt(0);
            }
        }));

        subscribeToSingle(o.toMap(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o.toString().charAt(0);
            }
        }, new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o.toString().toUpperCase();
            }
        }));
    }

    /*When none of the collection operators have what you need, you can always use the collect() operator to specify a
    different type to collect items into. For instance, there is no toSet() operator to collect emissions into a Set<T>,
     but you can quickly use collect() to effectively do this.*/
    static void toCollect(){
        System.out.println("Initializing toCollect operator ");
        System.out.println("Subscribe to observable ");
        subscribeToSingle(Observable.just(6, 2, 5, 7, 5, 1, 4, 9, 8, 3).collect(HashSet::new, HashSet::add));

    }

    static void subscribeToObservable(Observable observable){

        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe ");
            }

            @Override
            public void onNext(Object o) {
                System.out.println("onNext " + o.toString());

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError ");

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete ");

            }
        });
    }

    static void subscribeToMabyBe(Maybe maybeobsr){

        maybeobsr.subscribe(new MaybeObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe ");
            }

            @Override
            public void onSuccess(Object o) {
                System.out.println("onSuccess " + o.toString());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete ");
            }
        });
    }

    static void subscribeToSingle(Single s){

        s.subscribe(new SingleObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe ");
            }

            @Override
            public void onSuccess(Object o) {
                System.out.println("onSuccess " + o.toString());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError " + e.getMessage());
            }
        });
    }
}