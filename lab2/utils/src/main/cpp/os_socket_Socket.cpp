#include "os_socket_Socket.h"

#include <sys/socket.h>
#include <unistd.h>

#include <iostream>

JNIEXPORT jint JNICALL Java_os_socket_Socket_socket
        (JNIEnv* env, jclass c, jbyte domain, jint type, jint protocol) {
    std::cout << "socket(): Defining socket with domain " << (short) domain << ", type " << type << std::endl;
    return socket(domain, type, protocol);
}

JNIEXPORT jint JNICALL Java_os_socket_Socket_bind
        (JNIEnv* env, jclass c, jint socket, jbyte domain, jbyteArray java_address_data) {
    std::cout << "bind(): Binding socket " << socket << ", domain " << (short) domain << std::endl;

    jbyte* address_data = env->GetByteArrayElements(java_address_data, nullptr);
    int data_size = env->GetArrayLength(java_address_data);

    sockaddr address{17, static_cast<sa_family_t>(domain)};

    for (int i = 0; i < sizeof(sockaddr::sa_data) && i < data_size; i++)
        address.sa_data[i] = static_cast<char>(address_data[i]);
    env->ReleaseByteArrayElements(java_address_data, reinterpret_cast<jbyte*>(address_data), 0);

    return bind(socket, &address, sizeof(address));
}

JNIEXPORT jint JNICALL Java_os_socket_Socket_close
        (JNIEnv* env, jclass c, jint socket) {
    std::cout << "close(): Closing socket " << socket << std::endl;

    return close(socket);
}
