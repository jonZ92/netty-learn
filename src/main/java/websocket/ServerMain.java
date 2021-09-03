package websocket;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author jon 2021:09:03
 */


public class ServerMain {
    private final Logger log= LogManager.getLogger(this.getClass());

    public static void main(String[] args) {
        ServerMain s=new ServerMain();
        s.test();
    }


    private void test(){
        String str="xxxada";
        log.info("xxxxxxx:{}",str);
    }
}
