#!/bin/bash
adb forward tcp:5277 tcp:5277
export LD_LIBRARY_PATH=~/Android/Sdk/emulator/lib64
~/Android/Sdk/extras/google/auto/desktop-head-unit &