#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_br_ufma_nca_ergonomics_ergonomics_1test_testActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
