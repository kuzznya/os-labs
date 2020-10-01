#include "os_socket_SocketType.h"

#include <sys/socket.h>

JNIEXPORT jint JNICALL Java_os_socket_SocketType_getNativeValue
        (JNIEnv* env, jobject obj, jint idx) {
    switch (idx) {
        case 0:
            return SOCK_STREAM;
        case 1:
            return SOCK_DGRAM;
        case 2:
            return SOCK_RAW;
        case 3:
            return SOCK_SEQPACKET;
        default:
            return -1;
    }
}