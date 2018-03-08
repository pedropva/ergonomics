//
// Created by pedropva on 31/01/2018.
//

#include "ClientSocket.h"
#include "SocketException.cpp"
#include <iostream>
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_br_ufma_nca_ergonomics_ergonomics_1test_testActivity_connectSocket(JNIEnv *env, jobject /* this */) {
    try
    {

        ClientSocket client_socket ( "192.168.200.174", 30000);

        std::string reply;

        try
        {
            client_socket << "Test message.";
            client_socket >> reply;
        }
        catch ( SocketException& ) {}

        reply = "We received this response from the server:\n\"" + reply + "\"\n";
        return env->NewStringUTF(reply.c_str());
    }
    catch ( SocketException& e )
    {
        std::string exception;
        exception =  "Exception was caught:" + e.description() + "\n";
        return env->NewStringUTF(exception.c_str());
    }
}