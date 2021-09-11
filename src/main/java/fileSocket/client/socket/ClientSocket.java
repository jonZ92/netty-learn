package fileSocket.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jon 2021:09:11
 */


public class ClientSocket {

    private final Logger log = LogManager.getLogger(this.getClass());
    //线程池 实例化
    private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 10,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true), Executors.defaultThreadFactory());

    //ip
    private final String host;

    // 端口
    private final Integer port;

    private String filePath;


    public ClientSocket(String host, Integer port,String filePath) {
        this.host = host;
        this.port = port;
        this.filePath=filePath;
    }

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bt=new Bootstrap();
            bt.group(group)
                    .channel(NioSctpChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                        }
                    });
            ChannelFuture sync = bt.connect(host, port).sync();
            log.info("客户端启动成功 HOST=>:{},PORT=>:{}", host, port);
            sync.channel().closeFuture().sync();

        }catch (Exception e){

            log.error("运行出错:{}", e.getMessage());

        }finally {

            group.shutdownGracefully();

            log.info("客户端已关闭");

            try {
                TimeUnit.SECONDS.sleep(10);//休眠10s
                log.info("this thread is name run socket =>:{}", Thread.currentThread().getName());

                executor.execute(()->connect());
            }catch (Exception ex){

            }

        }

    }
}
