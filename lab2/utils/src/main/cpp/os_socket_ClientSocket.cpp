#include "os_socket_ClientSocket.h"

#include <sys/socket.h>

JNIEXPORT jint JNICALL Java_os_socket_ClientSocket_connect
        (JNIEnv* env, jclass c, jint socket, jbyte domain, jbyteArray java_address_data) {
    jbyte* address_data = env->GetByteArrayElements(java_address_data, nullptr);
    int data_size = env->GetArrayLength(java_address_data);

    sockaddr address{17, static_cast<sa_family_t>(domain)};

    for (int i = 0; i < sizeof(sockaddr::sa_data) && i < data_size; i++)
        address.sa_data[i] = static_cast<char>(address_data[i]);
    env->ReleaseByteArrayElements(java_address_data, reinterpret_cast<jbyte*>(address_data), 0);

    return connect(socket, &address, sizeof(address));
}
