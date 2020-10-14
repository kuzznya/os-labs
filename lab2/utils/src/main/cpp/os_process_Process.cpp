#include "os_process_Process.h"
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <vector>
#include <iostream>

 char* as_unsigned_char_array(JNIEnv * env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    char* buf = new char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

JNIEXPORT jint JNICALL Java_os_process_Process_run
  (JNIEnv * env, jclass c, jbyteArray byteArray){
       char * wholeLine = new char[10];
       char * name = new char[10];
       std::vector <char*> arguements;
       wholeLine = as_unsigned_char_array(env, byteArray);
       name = strtok(wholeLine, " ");
       char * arguement = strtok(NULL, " ");
       std::cout<<name<<" "<<arguement<<std::endl;
       arguements.push_back(name);
       while(arguement!=NULL){
       arguements.push_back(arguement);
       arguement = strtok(NULL, " ");
       }
       //arguements = strtok(NULL, " ");
       std::cout<<arguements[0]<<std::endl;
       return execv((const char *)name, (char * const *)&arguements[0]);
  }