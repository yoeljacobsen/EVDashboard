
# Makefile for EVDashboard

# Variables
ANDROID_HOME := $(HOME)/Android/Sdk
CMDLINE_TOOLS_URL := https://dl.google.com/android/repository/commandlinetools-linux-10406996_latest.zip

.PHONY: all clean install_deps build install

all: build

# Install dependencies for Ubuntu
install_deps:
	@echo "Installing dependencies..."
	sudo apt-get update
	sudo apt-get install -y openjdk-11-jdk wget unzip
	if [ ! -d "$(ANDROID_HOME)" ]; then \
		echo "Installing Android SDK..."; \
		mkdir -p $(ANDROID_HOME); \
		wget -q $(CMDLINE_TOOLS_URL) -O cmdline-tools.zip;  \
		unzip -q cmdline-tools.zip -d $(ANDROID_HOME)/cmdline-tools; \
		mv $(ANDROID_HOME)/cmdline-tools/cmdline-tools $(ANDROID_HOME)/cmdline-tools/latest;  \
		rm cmdline-tools.zip; \
		yes | $(ANDROID_HOME)/cmdline-tools/latest/bin/sdkmanager --licenses;  \
		$(ANDROID_HOME)/cmdline-tools/latest/bin/sdkmanager "platforms;android-34" "build-tools;34.0.0"; \
	fi

# Build the debug APK
build:
	@echo "Building the project..."
	./gradlew assembleDebug

# Install the app on a connected device
install:
	@echo "Installing the app..."
	./gradlew installDebug

# Clean the build directory
clean:
	@echo "Cleaning the project..."
	./gradlew clean

