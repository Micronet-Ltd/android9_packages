LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := FastMmiSwitch
LOCAL_CERTIFICATE := platform

#LOCAL_JAVA_LIBRARIES := qcrilhook qcnvitems telephony-common \
                        qcrilhook qcnvitems
LOCAL_SDK_VERSION := current
include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
