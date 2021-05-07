package JavaXEPSample;
import java.nio.file.Paths;
 
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
 
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// 追加
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Timestamp用
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// データ確認時使用
//import java.util.Iterator;


public class GPXInput extends DefaultHandler {
  Double lat;
  Double lon;
  Double speed;
  Double heading;
  //String description;
  //Timestamp drivetime;

  //XMLから読み取った情報をListに格納
  public static List<GPXInput> datalist = new ArrayList<GPXInput>();
  //XMLから読み取ってオブジェクトに入れる為に準備
  public static GPXInput obj = new GPXInput();
  public static String text;
  //TimeStamp処理用
  public static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  public static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    // 5. 何かの要素が始まった

    // trkptエレメントが始まった
    if ("trkpt".equals(qName)) {
      Double lat = Double.parseDouble(attributes.getValue("lat"));
      Double lon = Double.parseDouble(attributes.getValue("lon")); 
      // XEP経由で登録するデータ用
      obj.lat=lat;
      obj.lon=lon;
      datalist.add(obj);
      //System.out.println("lat = " + obj.lat);
      //System.out.println("lon = " + obj.lon);
    }
  }
 
  public void characters(char[] ch, int start, int length) throws SAXException {
    // 6. テキストが出現したなら、char配列をStringにしてフィールドへ保存する
    text = new String(ch, start, length);
  }
 
  public void endElement(String uri, String localName, String qName) throws SAXException {
    // 7. 何かの要素が終わった   

    /*
    // descエレメントの終わりが来たらテキストを取得
    if ("desc".equals(qName)) {
      //System.out.println("description = " + text);
      obj.description=text;
    }
    */

    // mytracks:sppedの終わりが来たらテキストを取得
    if ("mytracks:speed".equals(qName)) {
      //System.out.println("speed = " + Double.parseDouble(text));
      obj.speed=Double.parseDouble(text);
    }
    /*
    // timeエレメントの終わりが来たらタイムスタンプ取得
    if ("time".equals(qName)) {
      try {
        Date date = format1.parse(text);
        obj.drivetime=Timestamp.valueOf(format2.format(date));
        //System.out.println("time = " + text);
        //System.out.println("変換後time = " +obj.drivetime);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    */

    // trkptの終わりが来たらオブジェクト初期化
    if ("trkpt".equals(qName)) {
      obj=new GPXInput();
    }
  }



  //GPXファイルからList<GPXInput>を作成
  public static List<GPXInput> GPXToList(String filename) throws Exception {
    // 1. SAXParserFactoryを取得
    SAXParserFactory factory = SAXParserFactory.newInstance();
    // 2. SAXParserを取得
    SAXParser parser = factory.newSAXParser();
    // 3. SAXのイベントハンドラを生成(このクラスのインスタンス)
    GPXInput handler = new GPXInput();
    // 4. SAXParserにXMLを読み込ませて、SAXのイベントハンドラに処理を行わせる
    parser.parse(filename, handler);
    
    /* 
    //datalist確認用
    for(Iterator<GPXInput>itr = datalist.iterator(); itr.hasNext();) {
      GPXInput obj = itr.next();
      System.out.println(obj.lon + "-" + obj.lat);
    }
    */
    
    return datalist;
  }

  
  //List<GPXInput>クラスが格納された配列に変更　→　FlowableでList<GPXInput>で読めるので不要
  public static GPXInput[] generateSampleData(int objectCount ,List<GPXInput> datalist) {
    GPXInput[] xepdata = new GPXInput[objectCount];
    try{
      for (int i=0; i<datalist.size(); i++) {
        datalist.get(i);
        xepdata[i]= new GPXInput();
        xepdata[i]=datalist.get(i);
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return xepdata;
  }
}