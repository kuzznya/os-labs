#include "os_process_Process.h"

#include <sys/types.h>
#include <unistd.h>
#include <csignal>

JNIEXPORT jint JNICALL Java_os_process_Process_callFork
        (JNIEnv *, jclass) {
    signal(SIGCHLD, SIG_IGN);
    return fork();
}
