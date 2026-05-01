Cross-Platform Inspection Prototype

Overview

This basic app is a proof-of-concept for a high-reliability, offline-first inspection system. Designed for field engineers working at remote infrastructure sites, it prioritises data durability and secure synchronisation in environments with intermittent or unavailable network connectivity.
​
What the App Does

The application facilitates the capture of critical site inspection data through a streamlined workflow:

* Offline Data Capture: Users can log detailed inspections regardless of their current signal strength. Every entry is immediately persisted to a secure local Room (SQLite) database, ensuring that data is never lost due to app crashes or battery failure.
* Intelligent Synchronisation: The app features a background sync engine built with Ktor HTTP. It monitors connectivity and automatically queues data for upload. Once a connection is restored, it pushes the capture history to a central hub using JSON serialisation.
* Visual Traceability: The interface provides real-time feedback via a “Sync Banner” and status icons. Users can see exactly which records are stored locally on their handset and which have been safely moved to the central server.
* Audit Readiness: To comply with GDPR and operational safety standards, the system generates unique UUIDs and server-side timestamps for every entry, creating a verifiable audit trail.
​

Technical Architecture

The system utilises a Hub-and-Spoke model to bridge mobile and desktop environments:

1. Mobile Spoke (Android): A native application built with Kotlin and Jetpack Compose, handling all local business logic and data persistence.
2. Management Hub (Desktop): A Laravel (PHP) backend that hosts the RESTful API and provides a web-based management dashboard accessible from any Windows 10 workstation.
​
Setup Instructions

1. Backend: Navigate to the Laravel directory, run php artisan migrate, and start the server using php artisan serve --host=0.0.0.0 --port=8000.
2. Mobile: Launch the Android app. It is configured to communicate with the host machine via the gateway IP http://10.0.2.2:8000.
