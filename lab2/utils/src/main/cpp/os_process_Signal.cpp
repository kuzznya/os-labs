#include "os_process_Signal.h"

#include <csignal>
#include <iostream>

JNIEXPORT jint JNICALL Java_os_process_Signal_getNativeValue
        (JNIEnv* env, jclass c, jint idx) {
    switch (idx) {
        case 0:
            return SIGHUP;
        case 1:
            return SIGINT;
        case 2:
            return SIGQUIT;
        case 3:
            return SIGABRT;
        case 4:
            return SIGPIPE;
        case 5:
            return SIGTERM;
        case 6:
            return SIGSTOP;
        case 7:
            SIGTSTP;
        case 8:
            return SIGCONT;
        case 9:
            return SIGCHLD;
        case 10:
            return SIGIO;
        case 11:
            return SIGINFO;
        case 12:
            return SIGUSR1;
        case 13:
            return SIGUSR2;
        default:
            return 0;
    }
}

// Global vars are used to pass lambda as a signal handler
// (only non-capturing lambdas can be used)
JavaVM* vm = nullptr;

JNIEXPORT void JNICALL Java_os_process_Signal_bindSignalHandler
        (JNIEnv* env, jclass c, jint signum) {
    env->GetJavaVM(&vm);

    std::signal(signum, [](int signum) -> void {
        std::cout.flush();
        if (vm != nullptr) {
            JNIEnv* env;
            vm->AttachCurrentThread(reinterpret_cast<void **>(&env), nullptr);

            jclass cls = env->FindClass("os/process/Signal");
            jmethodID method = env->GetStaticMethodID(cls, "handle", "(I)V");
            env->CallVoidMethod(cls, method, signum);

            vm->DetachCurrentThread();
        }
    });
}

JNIEXPORT void JNICALL Java_os_process_Signal_ignore
        (JNIEnv* env, jclass c, jint signum) {
    signal(signum, SIG_IGN);
}

JNIEXPORT void JNICALL Java_os_process_Signal_raise
        (JNIEnv* env, jclass c, jint signum) {
    raise(signum);
}