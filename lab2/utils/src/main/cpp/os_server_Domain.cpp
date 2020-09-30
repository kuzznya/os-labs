#include "os_server_Domain.h"

#include <sys/socket.h>

JNIEXPORT jshort JNICALL Java_os_server_Domain_getNativeValue
  (JNIEnv* env, jobject obj, jint idx) {
    switch (idx) {
        case 1:
            return AF_UNIX;
        case 2:
            return AF_INET;
        case 3:
            return AF_INET6;
        default:
            return AF_UNSPEC;
    }
}