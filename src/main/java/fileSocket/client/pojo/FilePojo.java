package fileSocket.client.pojo;

import java.io.Serializable;

/**
 * @author jon 2021:09:11
 */


public class FilePojo implements Serializable {


    private String fileName;


    private int starPos;

    private byte[] bytes;

    private int endPos;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStarPos() {
        return starPos;
    }

    public void setStarPos(int starPos) {
        this.starPos = starPos;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }
}
