package fileSocket.pojo;

import java.io.Serializable;

/**
 * @author jon 2021:09:11
 */


public class FilePojo implements Serializable {


    private String fileName;

    private long beging;

    private byte[] bytes;

    private long endl;

    private String filePath;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getBeging() {
        return beging;
    }

    public void setBeging(long beging) {
        this.beging = beging;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public long getEndl() {
        return endl;
    }

    public void setEndl(long endl) {
        this.endl = endl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
