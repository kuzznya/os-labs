#include "os_processProcess.h"
#include <process.h>
#include <string.h>

unsigned char* as_unsigned_char_array(JNIEnv * env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

JNIEXPORT jint JNICALL Java_os_process_Process_run
  (JNIEnv * env, jclass c, jbyteArray byteArray){
       char * const wholeLine[] = as_unsigned_char_array(env, byteArray);
       char * const name[] = strtok(wholeLine, " ");
       char * const arguements[] = strtok(NULL, " ");
       return execv(name, arguements);
  }