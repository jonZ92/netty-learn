package tcpSocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcpSocket.handler.TcpSocketHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jon 2021:09:04
 */


public class TcpServerSocket {
    //log 日志
    private final Logger log = LogManager.getLogger(this.getClass());
    //线程池 实例化
    private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 10,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true), Executors.defaultThreadFactory());

    //ip
    private final String host;

    // 端口
    private final Integer port;

    //构造函数初始化服务端配置信息
    public TcpServerSocket(String host, Integer port) {

        this.host = StringUtil.isNullOrEmpty(host) ? "127.0.0.1" : host;

        this.port = StringUtil.isNullOrEmpty(String.valueOf(port)) ? 8800 : port;

    }

    //
    public void init() {

        EventLoopGroup boss = new NioEventLoopGroup(1);//监听链接线程数量设为1
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("the one", new TcpSocketHandler());
                        }
                    });
            Channel channel = boot.bind(host, port).sync().channel();

            log.info("webSocket服务器启动成功 HOST=>:{},PORT=>:{}", host, port);

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
