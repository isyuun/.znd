APP_STL :=gnustl_static

APP_CPPFLAGS := -frtti -DCC_ENABLE_CHIPMUNK_INTEGRATION=1 -std=c++11 -fsigned-char -fexceptions
APP_LDFLAGS := -latomic
APP_CFLAGS += -Wno-error=format-security

APP_ABI := armeabi-v7a
APP_PLATFORM := android-23

ifeq ($(NDK_DEBUG),1)
  APP_CPPFLAGS += -DCOCOS2D_DEBUG=1
  APP_OPTIM := debug
else
  APP_CPPFLAGS += -DNDEBUG
  APP_OPTIM := release
endif
