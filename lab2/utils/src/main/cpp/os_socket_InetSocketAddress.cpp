#include "os_socket_InetSocketAddress.h"

#include <netinet/in.h>

JNIEXPORT jbyteArray JNICALL Java_os_socket_InetSocketAddress_getNativeData
        (JNIEnv* env, jclass c, jshort port, jint ip) {
    sockaddr_in address {17, AF_INET, static_cast<in_port_t>(port), {static_cast<in_addr_t>(ip)}};

    jbyteArray result = env->NewByteArray(14);
    env->SetByteArrayRegion(result, 0, 14, reinterpret_cast<jbyte*>(&address + 2));
    return result;
}
