#include "os_server_Socket.h"

#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>
#include <memory>

JNIEXPORT jint JNICALL Java_os_server_Socket_socket
        (JNIEnv* env, jobject obj, jshort domain, jint type, jint protocol) {
    return socket(domain, type, protocol);
}

JNIEXPORT jint JNICALL Java_os_server_Socket_bind
        (JNIEnv* env, jobject obj, jint socket, jshort domain, jcharArray address) {
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

JNIEXPORT jint JNICALL Java_os_server_Socket_close
        (JNIEnv* env, jobject obj, jint socket) {
    return close(socket);
}
