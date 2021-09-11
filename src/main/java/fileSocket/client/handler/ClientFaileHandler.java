package fileSocket.client.handler;


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


public class ClientFaileHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LogManager.getLogger(this.getClass());

    private int byteRead;
    private volatile Long start = 0l;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;
    private FilePojo filePacket;
    File files = null;
    String path;

    public ClientFaileHandler(FilePojo filePacket) {
        this.path = filePacket.getFilePath() + filePacket.getFileName();
        this.files = new File(path);
        this.filePacket = filePacket;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!files.exists()) {
            log.info("file path error :{}", filePacket.getFilePath());
            throw new RuntimeException("file path error");
        }
        if (!files.isFile()) {
            log.info("file does not exist :{}", this.path);
            throw new RuntimeException("file does not exist");
        }
        randomAccessFile = new RandomAccessFile(files, "r");
        randomAccessFile.seek(filePacket.getBeging());
        lastLength = Integer.MAX_VALUE / 4;
        byte[] bytes = new byte[lastLength];
        if ((byteRead = randomAccessFile.read(bytes)) != -1) {
            filePacket.setEndl(byteRead);
            filePacket.setBytes(bytes);
            ctx.writeAndFlush(filePacket);
        } else {
            log.info("file finished reading");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Long) {
            start = (Long) msg;
            if (start != -1) {
                randomAccessFile = new RandomAccessFile(files, "r");
                randomAccessFile.seek(start);

                Long nowLength = randomAccessFile.length() - start;
                lastLength = Integer.MAX_VALUE / 4;
                if (nowLength < lastLength) {
                    log.info("file last length :{}", nowLength);
                    lastLength = nowLength.intValue();
                }
                byte[] bytes = new byte[lastLength];
                //这个判断关闭判断调整存在漏洞
                if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                    log.info("byte长度 :{}", bytes.length);
                    filePacket.setEndl(byteRead);
                    filePacket.setBytes(bytes);
                    randomAccessFile.close();
                    ctx.writeAndFlush(filePacket);
                } else {
                    ctx.close();
                    log.info("文件已读完------");
                }
            } else {
                ctx.close();
                log.info("文件已读完------");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("错误============>:{}", cause.getMessage());
        ctx.close();
    }
}
