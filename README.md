# MoneyNote

![MoneyNote App Screenshot - Placeholder](https://via.placeholder.com/600x400.png?text=MoneyNote+App+Screenshot)

## Overview

MoneyNote is a simple Android application developed as a final semester project (UAS) for Mobile Development Fundamentals. It aims to help users track their financial transactions (expenses) in a straightforward and intuitive manner, leveraging Firebase Realtime Database for secure and real-time data storage.

## Features

* **Splash Screen:** A welcoming splash screen displayed for 2-3 seconds upon app launch.
* **User Authentication:** Secure login and registration for users using email and password, powered by Firebase Authentication.
* **Financial Note Input:** A dedicated form to easily input new financial transactions, including:
    * **Category:** Describe the expense (e.g., Food, Transport, Bills).
    * **Amount:** The amount spent in Indonesian Rupiah (Rp).
    * **Date:** The date of the transaction, selected via a date picker.
* **Transaction List:** A comprehensive list view displaying all recorded financial transactions for the logged-in user.
* **User Profile:** A dedicated profile screen showing:
    * User's Name
    * User's Email
    * **Total Expenses:** A real-time calculated sum of all recorded expenses for the user.
* **Bottom Navigation:** Easy navigation between "Add Note", "List", and "Profile" sections via a user-friendly footer navigation bar.
* **Realtime Database:** All user data (profile and transactions) are stored and synchronized in real-time using Firebase Realtime Database.

## Technologies Used

* **Platform:** Android (Java)
* **Database:** Firebase Realtime Database
* **Authentication:** Firebase Authentication (Email & Password)
* **UI Components:** AndroidX, Material Design
* **Dependency Management:** Gradle

## Installation

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio (Bumblebee or newer recommended)
* Java Development Kit (JDK) 8 or higher
* A Firebase Project

### Setup Firebase

1.  **Create a Firebase Project:** Go to [Firebase Console](https://console.firebase.google.com/) and create a new project.
2.  **Add Android App:** Follow the on-screen instructions to add an Android application to your Firebase project.
    * Provide your Android package name (e.g., `com.yourcompany.moneynote`).
    * Optionally, add your SHA-1 fingerprint (recommended for better security, especially if you plan to use Google Sign-In later). You can get this from Android Studio's Gradle tab (`app > Tasks > android > signingReport`).
3.  **Download `google-services.json`:** After registering your app, download the `google-services.json` file.
4.  **Place `google-services.json`:** Copy the downloaded `google-services.json` file into your Android project's `app/` directory.
5.  **Enable Authentication Method:** In Firebase Console, navigate to `Authentication` > `Sign-in method` and enable **"Email/Password"**.
6.  **Add Firebase SDKs:** Ensure your `app/build.gradle` (Module) contains the necessary Firebase dependencies:
    ```gradle
    // ... (other dependencies)
    implementation platform('com.google.firebase:firebase-bom:33.0.0') // Use latest version
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-database'
    ```
    And in your project-level `build.gradle`, ensure the Google Services plugin is applied:
    ```gradle
    plugins {
        // ...
        id 'com.google.gms.google-services' version '4.4.1' apply false // Use latest version
    }
    ```
    And in your `app/build.gradle` (Module), apply the plugin:
    ```gradle
    plugins {
        id 'com.android.application'
        id 'com.google.gms.google-services' // This must be present
    }
    ```

### Clone and Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/MoneyNote.git](https://github.com/your-username/MoneyNote.git)
    cd MoneyNote
    ```
    (Replace `your-username` with your GitHub username if you're pushing this to your own repo).
2.  **Open in Android Studio:** Open the cloned project in Android Studio.
3.  **Sync Gradle:** Let Android Studio sync the project with Gradle files.
4.  **Run on Device/Emulator:** Connect an Android device or start an emulator and run the app.

## Usage

1.  **Splash Screen:** The app will start with a splash screen.
2.  **Login/Register:**
    * **Register:** Click "Register here" to create a new account. Provide your name, email, and password (min 6 characters).
    * **Login:** Use your registered email and password to log in.
3.  **Main Screen:** After successful login, you will be directed to the main screen with bottom navigation.
    * **Add Note (Plus icon):** Input your transaction category, amount (e.g., 50000), and date. Tap "Save Note" to record.
    * **List (List icon):** View all your recorded transactions.
    * **Profile (Person icon):** See your name, email, and the sum of all your expenses. You can also log out from this screen.

## Project Structure (Simplified)
