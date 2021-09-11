package fileSocket.client;


import fileSocket.client.socket.ClientSocket;
import fileSocket.pojo.FilePojo;

/**
 * @author jon 2021:09:11
 */


public class FileClientMain {

    public static void main(String[] args) {
        FilePojo file=new FilePojo();
        file.setFilePath("C:\\Users\\Administrator\\Desktop\\");
        file.setBeging(0L);
        file.setFileName("netty_learn.jar");
        ClientSocket socket = new ClientSocket("192.168.247.129", 7777,file);
        socket.connect();
    }
}

