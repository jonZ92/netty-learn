package fileSocket.server.socket;

import fileSocket.server.handler.ServerFileHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
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


    public ServerSocket(String host, Integer port) {
        this.host = host;
        this.port = port;
    }


    public void bind() {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));
                            pipeline.addLast("serverHandler",new ServerFileHandler());
                        }
                    });

            ChannelFuture sync = sb.bind(host, port).sync();

            log.info("文件服务器启动成功 HOST=>:{},PORT=>:{}", host, port);

            sync.channel().closeFuture().sync();

        } catch (Exception e) {

            log.error("运行出错:{}", e.getMessage());

        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            log.info("服务器已关闭");
            try {
                TimeUnit.SECONDS.sleep(10);//休眠10s
                log.info("this thread is name run socket =>:{}", Thread.currentThread().getName());
                executor.execute(()-> bind());
            }catch (Exception ex){
                log.error("当前线程休眠异常:{}", ex.getMessage());
            }
        }

    }
}
