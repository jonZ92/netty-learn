package websocket.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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
import websocket.handler.WebSocketHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jon 2021:09:03
 */


public class WebServerSocket {
    private final Logger log = LogManager.getLogger(this.getClass());


    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), Executors.defaultThreadFactory());

    private String host;

    private Integer port;

    private String wsPath;

    public WebServerSocket(String host, Integer port) {

        this.host = StringUtil.isNullOrEmpty(host) ? "127.0.0.1" : host;

        this.port = StringUtil.isNullOrEmpty(String.valueOf(port)) ? 8800 : port;
        //ws://localhost:8818/websocket
        this.wsPath="ws://"+this.host+":"+this.port+"/websocket";
    }

    public void init() {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channels) throws Exception {
                    channels.pipeline().addLast("logging", new LoggingHandler("INFO"));//设置log监听器，并且日志级别为debug，方便观察运行流程
                    channels.pipeline().addLast("http-codec", new HttpServerCodec());//设置解码器
                    channels.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));//聚合器，使用websocket会用到
                    channels.pipeline().addLast("http-chunked", new ChunkedWriteHandler());//用于大数据的分区传输
                    channels.pipeline().addLast("handler", new WebSocketHandler(wsPath));//自定义的业务handler
                }
            });
            Channel channel = bootstrap.bind(host, port).sync().channel();
            log.info("webSocket服务器启动成功 HOST=>:{},PORT=>:{}", host,port);
            log.info("webSocket服务器地址:{}", wsPath);
            channel.closeFuture().sync();


        } catch (Exception e) {
            log.error("运行出错:{}", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            log.info("websocket服务器已关闭");
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                log.error("休眠失败:{}", e.getMessage());
            }
            executor.execute(() -> {
                init();
            });
        }
    }
}
