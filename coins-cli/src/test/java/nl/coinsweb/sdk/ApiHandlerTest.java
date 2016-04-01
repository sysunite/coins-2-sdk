package nl.coinsweb.sdk;

import nl.coinsweb.sdk.cli.Run;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ApiHandlerTest {

  private static final Logger log = Logger.getLogger(ApiHandlerTest.class);

  private class ViewerThread extends Thread {

    public void run() {

      File file = new File(getClass().getClassLoader().getResource("zhejiangdayuan.ccr").getFile());

      String[] args = {
          "viewer",
          "-q",
          "-i",        file.getAbsolutePath(),
      };
      Run viewer = new Run();
      viewer.go(args);
    }

  }

  @Test
  public void runJetty() {



    ViewerThread server = new ViewerThread();
    server.start();


    try {

      Thread.sleep(6000);  // give jetty time to start


      log.info(getJson("http://localhost:8080/api/classes?sort={on:'URI',direction:'asc'}&offset=0&limit=100&filter=[{on:'URI',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/classes?sort={on:'LABEL',direction:'desc'}&offset=0&limit=100&filter=[{on:'LABEL',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/classes?sort={on:'COMMENT',direction:'asc'}&offset=0&limit=100&filter=[{on:'COMMENT',keyword:'ba'},{on:'URI',keyword:'ba'}]"));

      log.info(getJson("http://localhost:8080/api/class?uri='http://zhejiangdayuan.com/Motor'"));

      log.info(getJson("http://localhost:8080/api/individuals?sort={on:'URI',direction:'asc'}&offset=0&limit=100&filter=[{on:'URI',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/individuals?sort={on:'NAME',direction:'desc'}&offset=0&limit=100&filter=[{on:'NAME',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/individuals?sort={on:'USER',direction:'asc'}&offset=0&limit=100&filter=[{on:'USER',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/individuals?sort={on:'VERSION',direction:'asc'}&offset=0&limit=100&filter=[{on:'VERSION',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/individuals?sort={on:'MODIFIER',direction:'asc'}&offset=0&limit=100&filter=[{on:'MODIFIER',keyword:'ba'}]"));
      log.info(getJson("http://localhost:8080/api/individuals?sort={on:'MODIFICATION_DATE',direction:'asc'}&offset=0&limit=100&filter=[{on:'MODIFICATION_DATE',keyword:'ba'},{on:'URI',keyword:'ba'}]"));

      log.info(getJson("http://localhost:8080/api/individual?uri='http://zhejiangdayuan.com/378c4abc-39ef-44e3-9e0d-8437c1618bd4'"));

      log.info(getJson("http://localhost:8080/api/attachments?search=''"));

      log.info(touch("http://localhost:8080/attachment/zhejiangdayuan.jpg"));


//      Thread.sleep(6000000);  // give jetty time to start


    } catch (Exception e) {
      log.error(e.getMessage(), e);

    }




  }



  public static String getJson(String urlToRead) throws Exception {

    StringBuilder result = new StringBuilder();
    URL url = new URL(urlToRead);

    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");

    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    rd.close();
    return result.toString();
  }



  public static boolean touch(String urlToRead) throws Exception {

    StringBuilder result = new StringBuilder();
    URL url = new URL(urlToRead);

    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");

    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
    }
    rd.close();
    return true;
  }

}
