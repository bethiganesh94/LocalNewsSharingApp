# Local News Sharing App

## Project Overview

The Local News Sharing App is an Android application designed to help communities stay informed by allowing users to share, discover, and engage with local news. The app promotes community awareness by enabling users to create posts, comment on news, save important articles, and stay updated with real-time information.

---

## Key Features

### User Authentication

* User registration and login
* Persistent login using SharedPreferences
* Secure user data storage using Firebase Realtime Database

### Home Screen

* Auto-sliding Top News banner
* Auto-sliding Local User Posts
* Quick navigation options:

    * Local News
    * Saved News
    * Create Post
    * My Posts

### News Posting

* Create news posts with:

    * Title
    * Category
    * Description
    * Place
    * Date
    * Image
* Image upload using ImgBB
* News data stored in Firebase Realtime Database

### Comments System

* View total comments count for each post
* Add comments using dialog input
* View comments in a dialog
* Real-time updates using Firebase listeners

### Save News

* Save and unsave news locally
* Saved news accessible from Saved News screen
* Bookmark icon toggle

### Profile Management

* View profile details:

    * Name
    * Email
    * City
    * Date of Birth
    * Profile Picture
* Edit profile information
* Upload profile picture to ImgBB
* Logout functionality

### About & Contact

* About Us screen with project details
* Contact Us screen with email support
* Gmail intent integration for sending emails

---

## Technologies Used

### Android

* Kotlin
* Jetpack Compose
* Material Design 3

### Backend & Storage

* Firebase Realtime Database
* ImgBB API for image hosting

### Libraries

* Coil (Image loading)
* OkHttp (Network requests)
* Firebase SDK
* AndroidX Navigation Compose

---

## Software Design Highlights

* MVVM-style separation of UI and logic
* Declarative UI using Jetpack Compose
* Reusable and modular components
* Real-time data synchronization
* Scalable JSON-based data structure

---

1. Download or clone the project
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Add Firebase configuration file (google-services.json)
5. Add your ImgBB API key
6. Run the app on an emulator or physical device

---

## Developer Information

Student Name: Ganesh Bethi
Student Number: S3359881
Project Type: Academic / College Project

---



