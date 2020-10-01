#include "os_socket_Socket.h"

#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>
#include <memory>

#include <iostream>

JNIEXPORT jint JNICALL Java_os_socket_Socket_socket
        (JNIEnv* env, jobject obj, jshort domain, jint type, jint protocol) {
    std::cout << "socket(): Defining socket with domain " << domain << ", type " << type << std::endl;
    return socket(domain, type, protocol);
}

JNIEXPORT jint JNICALL Java_os_socket_Socket_bind
        (JNIEnv* env, jobject obj, jint socket, jshort domain, jcharArray address) {
    std::cout << "bind(): Binding socket " << socket << ", domain " << domain << std::endl;

    jboolean isCopy = true;
    char* addressData = reinterpret_cast<char*>(
            env->GetCharArrayElements(address, &isCopy)
            );
    int addressSize = env->GetArrayLength(address);

    sockaddr socketAddress{};
    socketAddress.sa_family = domain;
    for (int i = 0; i < 14 && i < addressSize; i++)
        socketAddress.sa_data[i] = addressData[i];
    delete[] addressData;

    return bind(socket, &socketAddress, sizeof(socketAddress));
}

JNIEXPORT jint JNICALL Java_os_socket_Socket_close
        (JNIEnv* env, jobject obj, jint socket) {
    std::cout << "close(): Closing socket " << socket << std::endl;

    return close(socket);
}
