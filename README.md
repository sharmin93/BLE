# BLE Application Guide

This project is a Bluetooth Low Energy (BLE) application designed to interact with BLE devices. The application provides several key features, including scanning for nearby BLE devices, connecting to BLE devices, reading data from them, and displaying device information.

## Key Features

- **Scanning for Nearby BLE Devices**: Continuously scans for available BLE devices in the vicinity and displays them in a list.
- **Connecting to BLE Devices**: Allows users to select a device from the list to establish a connection.
- **Reading Data from BLE Devices**: Reads data from characteristics exposed by the BLE device, such as battery level, device name, MAC address, etc.
- **Displaying Device Information**: Shows detailed information about the connected BLE device, including its name, address, battery level, and available services.

## Installation

### Prerequisites

Ensure you have the following installed on your system before installing the application:

- Java Development Kit (JDK) 8 or above
- Android Studio (recommended for development)
- Gradle (if building from the command line)

### Steps to Install

1. Clone the project from the repository using the following GitHub link: https://github.com/sharmin93/BLE
2. Open the project in Android Studio.
3. Install dependencies. Android Studio should automatically download and install the required dependencies via Gradle.

## Build Instructions

### Using Android Studio

1. **Sync the project**: Click on `File > Sync Project with Gradle Files` if the project does not sync automatically.
2. **Build the project**: Navigate to `Build > Make Project`.

## Running the Application

### Using Android Studio

1. Connect an Android device or use an emulator (e.g., Android 11 was used during testing).
2. Connect to any nearby BLE device. A BLE-enabled device is required for testing (e.g., an Amazfit smartwatch was used during testing).
3. Run the project:
- Click on the Run button or press `Shift + F10`.
- Select the device to deploy the app.

## Usage

### Scanning for BLE Devices

- Upon launching the app, the scanning process will start after the user clicks a button and grants the necessary permissions that appear upon clicking.
- The app will display a list of all nearby BLE devices found.

### Connecting to a BLE Device

- Tap on any device in the list to connect.
- Upon a successful connection, the app will navigate to a detailed view of the device.

### Reading Data from BLE Devices

- After connecting, the app will display available services and characteristics of the device.
- Users can select specific characteristics to read their values, such as name, address, data.

### Displaying Device Information

- The app provides detailed information about the connected device, including its name, address, and battery level.

## Dependencies

This project relies on the following libraries:

- AndroidX
- Bluetooth Low Energy (BLE) APIs
- Material



