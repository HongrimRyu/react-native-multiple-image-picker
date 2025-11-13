project(MultipleImagePicker)
cmake_minimum_required(VERSION 3.9.0)

set (PACKAGE_NAME MultipleImagePicker)
set (CMAKE_VERBOSE_MAKEFILE ON)
set (CMAKE_CXX_STANDARD 20)

# 16KB 페이지 크기 지원을 위한 컴파일러 및 링커 플래그
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC")

# 16KB 페이지 크기 명시적 설정
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,-z,max-page-size=16384")

# 추가 16KB 지원 플래그
add_compile_options(
    -Wall
    -fexceptions
    -frtti
    -DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES
)

# Define C++ library and add all sources
add_library(${PACKAGE_NAME} SHARED
        src/main/cpp/cpp-adapter.cpp
)

# 16KB 페이지 크기 속성 설정
set_target_properties(${PACKAGE_NAME} PROPERTIES
    LINK_FLAGS "-Wl,-z,max-page-size=16384"
)

# Add Nitrogen specs :)
include(${CMAKE_SOURCE_DIR}/../nitrogen/generated/android/MultipleImagePicker+autolinking.cmake)

# Set up local includes
include_directories(
        "src/main/cpp"
        "../cpp"
)

find_library(LOG_LIB log)

# Link all libraries together
target_link_libraries(
        ${PACKAGE_NAME}
        ${LOG_LIB}
        android                                   # <-- Android core
)

# 16KB 페이지 크기를 위한 컴파일 정의
target_compile_definitions(${PACKAGE_NAME} PRIVATE
    ANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=1
)

# 빌드 후 16KB 검증 메시지
add_custom_command(TARGET ${PACKAGE_NAME} POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E echo "Built ${PACKAGE_NAME} with 16KB page size support"
)
