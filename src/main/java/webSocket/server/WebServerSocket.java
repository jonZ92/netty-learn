package webSocket.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import webSocket.handler.WebSocketHandler;

import java.util.concurrent.*;

/**
 * @author jon 2021:09:03
 */


public class WebServerSocket {
    private final Logger log = LogManager.getLogger(this.getClass());


    private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 10,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true), Executors.defaultThreadFactory());

    private final String host;

    private final Integer port;

    private final String wsPath;

    public WebServerSocket(String host, Integer port) {

        this.host = StringUtil.isNullOrEmpty(host) ? "127.0.0.1" : host;

        this.port = StringUtil.isNullOrEmpty(String.valueOf(port)) ? 8800 : port;

        this.wsPath = "ws://" + this.host + ":" + this.port + "/webSocket";
    }

    public void init() {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(boss, work);
            boot.channel(NioServerSocketChannel.class);
            boot.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channels) throws Exception {
                    ChannelPipeline pipeline = channels.pipeline();
                    pipeline.addLast("logging", new LoggingHandler("INFO"));//设置log监听器，并且日志级别为info
                    pipeline.addLast("http-codec", new HttpServerCodec());//设置 编、解 码器
                    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));//聚合器，使用websocket会用到
                    pipeline.addLast("http-chunked", new ChunkedWriteHandler());//用于大数据的分区传输
                    pipeline.addLast("handler", new WebSocketHandler(wsPath));//自定义的业务handler
                }
            });
            Channel channel = boot.bind(host, port).sync().channel();

            log.info("webSocket服务器启动成功 HOST=>:{},PORT=>:{}", host, port);

            log.info("webSocket服务器地址:{}", wsPath);

            channel.closeFuture().sync();


        } catch (Exception e) {
            log.error("运行出错:{}", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();

            log.info("websocket服务器已关闭");

            try {

                TimeUnit.SECONDS.sleep(10);//休眠10s

            } catch (InterruptedException e) {

                log.error("当前线程休眠异常:{}", e.getMessage());
            }

            log.info("this thread is name  =>:{}", Thread.currentThread().getName());

            executor.execute(() -> {
                init();
            });
        }
    }
}
