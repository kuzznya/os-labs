#include "os_socket_ServerSocket.h"

#include <sys/socket.h>

#include <iostream>

JNIEXPORT jint JNICALL Java_os_socket_ServerSocket_listen
        (JNIEnv* env, jclass c, jint socket, jint backlog) {
    std::cout << "Listening socket " << socket << std::endl;
    return listen(socket, backlog);
}

JNIEXPORT jint JNICALL Java_os_socket_ServerSocket_accept
        (JNIEnv* env, jclass c, jint socket, jbyteArray client_addr_buf) {
    std::cout << "accept(): Accepting incoming connection" << std::endl;

    sockaddr client_addr{};
    socklen_t len = sizeof(client_addr);
    int code = accept(socket, &client_addr, &len);
    env->SetByteArrayRegion(client_addr_buf, 0, len, (jbyte *) (&(client_addr.sa_family)));
    return code;
}