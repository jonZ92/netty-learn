package fileSocket.server.handler;


import fileSocket.pojo.FilePojo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author jon 2021:09:11
 */


public class ServerFileHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LogManager.getLogger(this.getClass());

    private Long byteRead;
    private volatile Long start = 0L;
    private String file_dir = "/home/jon/soft/";
    //private String file_dir = "D:\\JZRJ\\";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("client is ============>:{}", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FilePojo) {
            FilePojo pojo = (FilePojo) msg;
            byte[] bytes = pojo.getBytes();
            byteRead = pojo.getEndl();
            String fileName = pojo.getFileName();
            String filePath = file_dir + fileName;
            File file = new File(filePath);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + byteRead;
            randomAccessFile.close();
            if (byteRead > 0) {
                log.info("文件接收完成");
                ctx.writeAndFlush(start);
            }else{
                log.info("文件接收完毕");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("错误============>:{}", cause.getMessage());
        ctx.close();
    }
}
