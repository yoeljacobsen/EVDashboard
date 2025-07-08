#!/bin/bash
export LD_LIBRARY_PATH=/home/yoel/Android/Sdk/emulator/lib64/:/home/yoel/bin/android-studio/plugins/android-ndk/resources/lldb/lib64/:$LD_LIBRARY_PATH
export LD_PRELOAD=/home/yoel/bin/android-studio/plugins/android-ndk/resources/lldb/lib64/libc++abi.so.1
/home/yoel/Android/Sdk/extras/google/auto/desktop-head-unit -u -c /home/yoel/Development/Android/EVDashboard/mock_sensors.json