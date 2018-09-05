# $Id: Application.mk 212 2015-05-15 10:22:36Z oparviai $
#
# Build library bilaries for all supported architectures
#

#APP_ABI := armeabi armeabi-v7a arm64-v8a x86 x86_64 mips mips64 all
APP_ABI := all
APP_OPTIM := release
#APP_STL := stlport_static
APP_STL := c++_static
APP_CPPFLAGS := -fexceptions # -D SOUNDTOUCH_DISABLE_X86_OPTIMIZATIONS

