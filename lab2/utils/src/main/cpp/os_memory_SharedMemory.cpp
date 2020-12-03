#include "os_memory_SharedMemory.h"

#include <unistd.h>
#include <string>
#include <sys/shm.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <iostream>

short& shm_pos(void* ptr) {
    return *(static_cast<short*>(ptr));
}

JNIEXPORT jint JNICALL Java_os_memory_SharedMemory_createNative
        (JNIEnv* env, jclass, jstring java_name, jint size) {
    const char* name = env->GetStringUTFChars(java_name, nullptr);
    shm_unlink(name);

    int shm_fd = shm_open(name, O_CREAT | O_RDWR | O_TRUNC, 0666);
    ftruncate(shm_fd, size);

    env->ReleaseStringUTFChars(java_name, name);

    return shm_fd;
}

JNIEXPORT jint JNICALL Java_os_memory_SharedMemory_openNative
        (JNIEnv* env, jclass, jstring java_name) {
    const char* name = env->GetStringUTFChars(java_name, nullptr);

    int shm_fd = shm_open(name, O_RDWR, 0666);

    env->ReleaseStringUTFChars(java_name, name);

    return shm_fd;
}

JNIEXPORT jlong JNICALL Java_os_memory_SharedMemory_mapNative
        (JNIEnv *, jclass, jint shm_fd, jint size) {
    void* ptr = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_SHARED, shm_fd, 0);
    shm_pos(ptr) = 0;
    return reinterpret_cast<jlong>(ptr);
}

JNIEXPORT void JNICALL Java_os_memory_SharedMemory_unmapNative
        (JNIEnv *, jclass, jlong java_ptr, jint size) {
    char* ptr = reinterpret_cast<char *>(java_ptr);
    munmap(ptr, size);
}

JNIEXPORT void JNICALL Java_os_memory_SharedMemory_unlinkNative
        (JNIEnv* env, jclass, jstring java_name) {
    const char* name = env->GetStringUTFChars(java_name, nullptr);
    shm_unlink(name);
    env->ReleaseStringUTFChars(java_name, name);
}

JNIEXPORT jbyteArray JNICALL Java_os_memory_SharedMemory_readNative
        (JNIEnv* env, jclass, jlong java_ptr) {
    char* ptr = reinterpret_cast<char *>(java_ptr);

    std::cout << "DATA IS BEING READ; SHM LEN: " << shm_pos(ptr) << std::endl;
    for (int i = 2; i < shm_pos(ptr) + 2; i++)
        std::cout << ptr[i];
    std::cout << std::endl;

    short len = shm_pos(ptr);
    jbyteArray data = env->NewByteArray(len);
    env->SetByteArrayRegion(data, 2, len, reinterpret_cast<const jbyte *>(ptr));
    shm_pos(ptr) = 0;
    return data;
}

JNIEXPORT void JNICALL Java_os_memory_SharedMemory_writeNative
        (JNIEnv* env, jclass, jlong java_ptr, jbyteArray java_data, jint mem_size) {
    char* ptr = reinterpret_cast<char *>(java_ptr);

    char* data = reinterpret_cast<char *>(env->GetByteArrayElements(java_data, nullptr));
    int data_size = env->GetArrayLength(java_data);

    short write_len = std::min(data_size, mem_size - shm_pos(ptr) - 2);
    memcpy(ptr + 2 + shm_pos(ptr), data, write_len);
    env->ReleaseByteArrayElements(java_data, reinterpret_cast<jbyte *>(data), 0);

    shm_pos(ptr) += write_len;

    std::cout << "DATA WRITTEN. SHM LEN: " << shm_pos(ptr) << std::endl;
    for (int i = 2; i < shm_pos(ptr) + 2; i++)
        std::cout << ptr[i];
    std::cout << std::endl;
}
