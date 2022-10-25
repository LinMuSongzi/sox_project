# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("cpp")
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(distribution_DIR ${CMAKE_SOURCE_DIR}/libs)
add_library( # Sets the name of the library.
        cpp

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp)

add_library( # Sets the name of the library.
        msz

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        mszi_test.cpp
        text_sum/subtraction.cpp
        )

#set_target_properties(msz PROPERTIES IMPORTED_LOCATION sox_source/subtraction.cpp)
# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
#include_directories(src/main/cpp/sox_source src/main/cpp/text_sum)
target_link_libraries( # Specifies the target library.
        cpp
        msz
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})