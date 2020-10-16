#include "os_process_Process.h"
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <pwd.h>
#include <vector>
#include <iostream>

 char* as_unsigned_char_array(JNIEnv * env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    char* buf = new char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}
jbyteArray as_byte_array(JNIEnv * env, char* buf, int len) {
    jbyteArray array = env->NewByteArray (len);
    env->SetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return array;
}

JNIEXPORT jint JNICALL Java_os_process_Process_run
  (JNIEnv * env, jclass c, jbyteArray byteArray){
  std::cout<<"run"<<std::endl;
       char * wholeLine = nullptr;
       char * name = nullptr;
       std::vector <char*> arguements;
       wholeLine = as_unsigned_char_array(env, byteArray);
       name = strtok(wholeLine, " ");
       char * arguement = strtok(NULL, " ");
       //std::cout<<name<<" "<<arguement<<std::endl;
       arguements.push_back(name);
       while(arguement!=NULL){
       arguements.push_back(arguement);
       arguement = strtok(NULL, " ");
       }
       //arguements = strtok(NULL, " ");
      // std::cout<<arguements[0]<<std::endl;
       return execv((const char *)name, (char * const *)&arguements[0]);
  }
  JNIEXPORT jbyteArray JNICALL Java_os_process_Process_user
    (JNIEnv * env, jclass c){
    char * username = getpwuid(getuid())->pw_name;
    if(username==NULL){
    std::cout<<"Was zum Teufel?!"<<std::endl;
    }
    //std::cout<<username<<" username"<<std::endl;
   // char * userrname = "user";
    jbyteArray result = as_byte_array(env, username, strlen(username));
    return result;
    }