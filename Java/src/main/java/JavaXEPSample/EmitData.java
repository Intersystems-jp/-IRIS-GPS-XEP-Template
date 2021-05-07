package JavaXEPSample;
//http://reactivex.io/RxJava/javadoc/io/reactivex/Flowable.html
import io.reactivex.Flowable;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.intersystems.xep.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmitData {
    public static void Start(EventPersister xepPersister,List<GPXInput> datalist,String classname) throws InterruptedException{
        // EventPersisterからEventを作成
        Event xepEvent = xepPersister.getEvent(classname);

        //フィルタされた値をためておくList
        List<GPXInput> storelist = new ArrayList<GPXInput>();

        Flowable.fromIterable(datalist)
        .subscribeOn(Schedulers.io())
        .filter(obj->(obj.speed == null) || ((obj.speed !=null)&&(obj.speed>0)))
        .subscribe(
            // データ通知時
            // ** 1件ずつIRISへ登録することもできますが、対象全件一括登録を行うため一旦リストに格納しています **
            obj -> {
                storelist.add(obj);
                System.out.println("onNext：" + obj.lat + "-" + obj.lon + "-" + obj.speed);
        },

        // 第2引数：エラー通知時
        error -> System.out.println("エラー=" + error),
        
        //第3引数：完了通知
        () -> {
            System.out.println("フィルタ後の個数： " + storelist.size());

            GPXInput[] storedata = storelist.toArray(new GPXInput[storelist.size()]);
            xepEvent.store(storedata);
            System.out.println("データ登録完了!");
        });
        Thread.sleep(2000);
 
    }
}