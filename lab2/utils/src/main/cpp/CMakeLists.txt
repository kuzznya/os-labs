cmake_minimum_required(VERSION 3.15)
project(server)

find_package(JNI REQUIRED)

set(CMAKE_CXX_STANDARD 14)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall")

include_directories(${JNI_INCLUDE_DIRS})
include_directories(../../../build/generated/sources/headers/java/main/)

add_library(server os_server_Domain.h os_server_Socket.h os_server_SocketType.h os_server_Domain.cpp os_server_SocketType.cpp os_server_Socket.cpp)