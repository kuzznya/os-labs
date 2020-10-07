#include "os_process_CurrentProcess.h"

#include <sys/types.h>
#include <unistd.h>
#include <csignal>

JNIEXPORT void JNICALL Java_os_process_CurrentProcess_kill__I
        (JNIEnv* env, jclass c, jint process) {
    kill(process, SIGKILL);
}

JNIEXPORT void JNICALL Java_os_process_CurrentProcess_kill__II
        (JNIEnv* env, jclass c, jint process, jint signal) {
    kill(process, signal);
}

JNIEXPORT jint JNICALL Java_os_process_CurrentProcess_callFork
        (JNIEnv *, jclass) {
    signal(SIGCHLD, SIG_IGN);
    return fork();
}
