package os.server;

import os.socket.*;

public class Main {

    public static void main(String[] args) {
        ServerSocket socket = new ServerSocket();
        socket.create();
        socket.bind(new InetSocketAddress((short) 8080));
        socket.listen();
        socket.accept();
        socket.close();
    }
}
