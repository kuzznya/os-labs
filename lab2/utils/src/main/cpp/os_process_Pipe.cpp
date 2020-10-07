#include "os_process_Pipe.h"

#include <unistd.h>

JNIEXPORT void JNICALL Java_os_process_Pipe_init
        (JNIEnv* env, jclass c, jintArray descriptors) {
    int* pipe_fd = reinterpret_cast<int*>(env->GetIntArrayElements(descriptors, JNI_FALSE));
    pipe(pipe_fd);
    env->ReleaseIntArrayElements(descriptors, pipe_fd, 0);
}

JNIEXPORT void JNICALL Java_os_process_Pipe_closeReadNative
        (JNIEnv* env, jclass c, jint descriptor) {
    close(descriptor);
}

JNIEXPORT void JNICALL Java_os_process_Pipe_closeWriteNative
        (JNIEnv* env, jclass c, jint descriptor) {
    close(descriptor);
}

JNIEXPORT jint JNICALL Java_os_process_Pipe_read
        (JNIEnv* env, jclass c, jint descriptor, jbyteArray buffer) {
    jbyte* data = env->GetByteArrayElements(buffer, JNI_FALSE);
    int len = env->GetArrayLength(buffer);
    int result = read(descriptor, data, len);
    env->ReleaseByteArrayElements(buffer, data, 0);
    return result;
}

JNIEXPORT jint JNICALL Java_os_process_Pipe_write
        (JNIEnv* env, jclass c, jint descriptor, jbyteArray buffer) {
    jbyte* data = env->GetByteArrayElements(buffer, JNI_FALSE);
    int len = env->GetArrayLength(buffer);
    int result = write(descriptor, data, len);
    env->ReleaseByteArrayElements(buffer, data, 0);
    return result;
}
