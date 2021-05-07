package JavaXEPSample;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intersystems.xep.*;

import java.util.Iterator;

public class Start {
    protected   static  String              namespace = "USER";
    protected   static  String              host = "gps-iris";
    protected   static  int                 port = 1972; 
    protected   static  String              username = "SuperUser";
    protected   static  String              password = "SYS";

    public static void main (String [] args) throws Exception {
    try {
        if (args.length==0) {
            System.out.println("★★ 引数に入力するGPXファイルを指定してください ★★");
            return;
        }

        String env1 = System.getenv("IRISHOSTNAME");
        if (env1 !=null ) {
            //System.out.println(env1);
            host=env1;
        }

        System.out.println("読むファイル : " + Paths.get(args[0]).toFile().toString());
        // XMLパース呼出
        List<GPXInput> datalist=GPXInput.GPXToList(Paths.get(args[0]).toFile().toString());
        System.out.println("リストの個数＝" + datalist.size());                

        // EventPersisterを利用してIRISへ接続し、スキーマ作成
        EventPersister xepPersister = PersisterFactory.createPersister();
        xepPersister.connect(host,port,namespace,username,password);
        // 既存にクラスがある場合、テストデータのため全消去
        String classname="JavaXEPSample.GPXInput";
        xepPersister.deleteExtent(classname);

        // フラットスキーマとしてインポート
        //https://docs.intersystems.com/irislatestj/csp/docbook/Doc.View.cls?KEY=BJAVXEP_xep#BJAVXEP_xep_import        
        xepPersister.importSchema(classname);

        System.out.println("EmitData 開始！"); 
        // EmitData呼出
        EmitData.Start(xepPersister,datalist,classname);

        // EventPersister終了
        xepPersister.close();

        }
        catch (Exception e) { 
            System.out.println("Interactive prompt failed:\n" + e); 
        }
    }
}