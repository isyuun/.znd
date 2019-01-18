LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := bsse_nk_kr
LOCAL_SRC_FILES := ../jniLib/$(APP_ABI)/libbsse_nk_kr.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := sample

KYSOFTSYNTH_DIRECTORY := $(LOCAL_PATH)
SOURCE_FILES := $(shell find $(KYSOFTSYNTH_DIRECTORY) -name *.c)
SOURCE_FILES += $(shell find $(KYSOFTSYNTH_DIRECTORY) -name *.cpp)
SOURCE_FILES := $(sort $(SOURCE_FILES))
SOURCE_FILES := $(subst $(LOCAL_PATH)/,,$(SOURCE_FILES))

LOCAL_SRC_FILES := \
 $(SOURCE_FILES) \

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)

LOCAL_C_INCLUDES := $(LOCAL_PATH) \
					$(LOCAL_PATH)/BSL/	\
					$(LOCAL_PATH)/bssynth/	\
					$(LOCAL_PATH)/converter/	\
					$(LOCAL_PATH)/bssynth/MidiPlayer	\
					$(LOCAL_PATH)/bssynth/MidiPlayer/header	\
					$(LOCAL_PATH)/bssynth/SynthEngine	\

LOCAL_EXPORT_LDLIBS := -lGLESv2 \
                       -lEGL \
                       -llog \
                       -lz \
                       -landroid
                       
LOCAL_LDLIBS += -lOpenSLES -landroid -llog

LOCAL_CFLAGS := -DANDROID_ARM_NEON=TRUE -DANDROID_TOOLCHAIN=clang -D ST_NO_EXCEPTION_HANDLING -marm -mfloat-abi=softfp
LOCAL_CFLAGS += -O3 -fsigned-char -DBSMP_EXPORTS -DBSSYNTH_GS -DBSSYNTH_88 -DBSSYNTH_WTBL_ON_DEMAND -DBSSYNTH_KY -DBSSYNTH_FLOAT -DBSSYNTH_KY_RHYTHM_CHANGE
LOCAL_CPPFLAGS := -Wno-extern-c-compat
LOCAL_CPPFLAGS += -O3 -fsigned-char -DBSMP_EXPORTS -DBSSYNTH_GS -DBSSYNTH_88 -DBSSYNTH_WTBL_ON_DEMAND -DBSSYNTH_KY -DBSSYNTH_FLOAT -DBSSYNTH_KY_RHYTHM_CHANGE

LOCAL_STATIC_LIBRARIES += bsse_nk_kr

include $(BUILD_SHARED_LIBRARY)
