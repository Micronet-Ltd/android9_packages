LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := LovdreamCit 

LOCAL_CERTIFICATE := platform

LOCAL_SDK_VERSION := current
#LOCAL_JAVA_LIBRARIES := qcom.fmradio
#LOCAL_JNI_SHARED_LIBRARIES := libqcomfm_jni 
#libfpjni_sw

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)
