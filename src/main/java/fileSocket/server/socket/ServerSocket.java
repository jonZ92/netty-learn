package fileSocket.server.socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jon 2021:09:11
 */


public class ServerSocket {

    private final Logger log = LogManager.getLogger(this.getClass());
    //线程池 实例化
    private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 10,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true), Executors.defaultThreadFactory());

    //ip
    private final String host;

    // 端口
    private final Integer port;


    public ServerSocket(String host,Integer port){
        this.host=host;
        this.port=port;
    }


    public void bind(){


    }
}
