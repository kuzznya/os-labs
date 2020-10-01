cmake_minimum_required(VERSION 3.15)
project(socket)

find_package(JNI REQUIRED)

set(CMAKE_CXX_STANDARD 14)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall")

include_directories(${JNI_INCLUDE_DIRS})
include_directories(../../../build/generated/sources/headers/java/main)

add_library(socket os_socket_Domain.cpp os_socket_SocketType.cpp os_socket_Socket.cpp)