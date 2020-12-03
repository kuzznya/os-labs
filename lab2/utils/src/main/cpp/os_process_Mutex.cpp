#include "os_process_Mutex.h"

#include <pthread.h>

JNIEXPORT jlong JNICALL Java_os_process_Mutex_init
        (JNIEnv *, jclass) {
    auto* mutex = new pthread_mutex_t();
    pthread_mutex_init(mutex, nullptr);
    return reinterpret_cast<jlong>(mutex);
}

JNIEXPORT jint JNICALL Java_os_process_Mutex_lock
        (JNIEnv *, jclass, jlong mutex) {
    auto* mutex_p = reinterpret_cast<pthread_mutex_t *>(mutex);
    return pthread_mutex_lock(mutex_p);
}

JNIEXPORT jint JNICALL Java_os_process_Mutex_unlock
        (JNIEnv *, jclass, jlong mutex) {
    auto* mutex_p = reinterpret_cast<pthread_mutex_t *>(mutex);
    return pthread_mutex_unlock(mutex_p);
}

JNIEXPORT void JNICALL Java_os_process_Mutex_destroy
        (JNIEnv *, jclass, jlong mutex) {
    auto* mutex_p = reinterpret_cast<pthread_mutex_t *>(mutex);
    pthread_mutex_destroy(mutex_p);
}
