#include <jni.h>
#include <cmath>
#include <algorithm>

class Engine {
public:
    float frequency = 440.0f;
    float partials[512];
    Engine() {
        for (int i=0;i<512;i++) partials[i] = 1.0f/(i+1);
    }
    void setPartial(int i, float v) {
        if (i>=0 && i<512) partials[i] = std::clamp(v,0.0f,1.0f);
    }
};

extern "C" JNIEXPORT jlong JNICALL
Java_com_openharmor_app_MainActivity_nativeCreate(JNIEnv*, jobject) {
    return reinterpret_cast<jlong>(new Engine());
}
extern "C" JNIEXPORT void JNICALL
Java_com_openharmor_app_MainActivity_nativeDestroy(JNIEnv*, jobject, jlong h) {
    delete reinterpret_cast<Engine*>(h);
}
extern "C" JNIEXPORT void JNICALL
Java_com_openharmor_app_MainActivity_nativeSetFrequency(JNIEnv*, jobject, jlong h, jfloat hz) {
    if (h) reinterpret_cast<Engine*>(h)->frequency = hz;
}
extern "C" JNIEXPORT void JNICALL
Java_com_openharmor_app_MainActivity_nativeSetPartial(JNIEnv*, jobject, jlong h, jint i, jfloat v) {
    if (h) reinterpret_cast<Engine*>(h)->setPartial(i,v);
}
