package os.server;

import os.socket.*;

public class Main {

    public static void main(String[] args) {
        Socket socket = new Socket(Domain.AF_INET, SocketType.SOCK_STREAM);
        socket.create();
        socket.close();
    }
}
