# MAFI - AI-Powered Note Taking Platform

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-28%2B-brightgreen.svg)](https://android-arsenal.com/api?level=28)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)

MAFI is an intelligent note-taking application for Android devices that leverages artificial intelligence to enhance your note management experience. The app provides powerful AI-driven features including text summarization, content expansion, classification, and intelligent question-answering capabilities.

## 🌟 Features

### Core Functionality
- **📝 Note Creation & Management**: Create, edit, and organize your notes with an intuitive interface
- **👤 User Authentication**: Secure user registration and login system
- **💾 Local Storage**: SQLite database for offline access and data persistence
- **🎨 Material Design**: Modern, responsive UI following Material Design guidelines

### AI-Powered Features
- **📄 Text Summarization**: Automatically generate concise summaries of your content
- **🔍 Deep Analysis**: Comprehensive text analysis with detailed insights
- **🏷️ Content Classification**: Intelligent categorization of your notes
- **❓ Question-Answering**: Generate relevant questions and answers from your content
- **🔧 Model Selection**: Choose from multiple AI models based on your needs

### User Experience
- **📱 Responsive Design**: Optimized for various Android screen sizes
- **🌙 Modern UI**: Clean interface with smooth animations and transitions
- **⚡ Performance**: Efficient data handling and quick response times
- **🔒 Privacy**: Local data storage with secure user management

## 🏗️ Architecture

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern:

```
app/
├── data/
│   ├── local/          # Local database and preferences
│   ├── model/          # Data models and entities
│   ├── remote/         # API services and network layer
│   └── repository/     # Data repositories
├── ui/
│   ├── fragment/       # UI fragments
│   ├── viewmodel/      # ViewModels for business logic
│   └── MainActivity    # Main activity
└── utils/              # Utility classes
```

### Key Components

#### Database Schema
- **Users Table**: User authentication and profile management
- **Contents Table**: Note storage with metadata
- **Content Types Table**: Categorization system

#### API Integration
- RESTful API communication using Retrofit
- Multiple AI model support
- Robust error handling and response parsing

## 🛠️ Technologies Used

### Android Framework
- **Minimum SDK**: API 28 (Android 9.0)
- **Target SDK**: API 35
- **Language**: Java
- **Build System**: Gradle with Kotlin DSL

### Dependencies
- **UI Framework**: AndroidX Material Components
- **Network**: Retrofit 2 with Gson converter
- **Database**: SQLite with custom database helper
- **Architecture**: AndroidX Lifecycle components
- **HTTP Client**: OkHttp with logging interceptor

### Development Tools
- Android Studio
- Gradle build system
- ProGuard for code obfuscation

## 🚀 Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 28+
- Java 11 or later

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/tunahanbg/mafi-main.git
   cd mafi-main
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Configure API Settings**
   - Update API base URL in your network configuration
   - Ensure your AI service endpoints are accessible

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   - Or use Android Studio's run button
   - Deploy to an Android device or emulator

## 🔧 Configuration

### API Setup
The app requires a backend API service for AI functionality. Configure the following endpoints:

- `POST /api/analyze/summarize` - Text summarization
- `POST /api/analyze/deep-analysis` - Deep content analysis
- `POST /api/analyze/classify` - Content classification
- `POST /api/analyze/question-answering` - Q&A generation
- `GET /api/models` - Available AI models
- `POST /api/set-model` - Model selection

### Database Configuration
The app uses SQLite for local storage. Database initialization includes:
- User management tables
- Content storage with foreign key relationships
- Default content types (Text, Image, Audio, Video)

## 📖 Usage

### Getting Started
1. **Registration**: Create a new account or log in with existing credentials
2. **Create Notes**: Use the text editor to create your first note
3. **AI Features**: Select text and use AI-powered tools:
   - **Summarize**: Generate quick summaries
   - **Deep Analysis**: Get comprehensive insights
   - **Classify**: Automatically categorize content
   - **Questions**: Generate relevant Q&A pairs

### AI Model Management
- Access Settings to view available AI models
- Select models based on your specific needs
- Monitor model performance and switching capabilities

### Content Organization
- Notes are automatically saved with timestamps
- Use content types for better organization
- Search and filter capabilities for easy retrieval

## 🎯 AI Features in Detail

### Text Summarization
Convert lengthy content into concise, meaningful summaries while preserving key information and context.

### Deep Analysis
Perform comprehensive analysis including:
- Content structure evaluation
- Key topic identification
- Semantic analysis
- Writing quality assessment

### Content Classification
Automatically categorize your notes into predefined or custom categories based on content analysis.

### Question-Answering
Generate intelligent questions and answers from your content to enhance learning and comprehension.

## 🔒 Privacy & Security

- **Local Storage**: All personal data stored locally on device
- **Secure Authentication**: Password-based user authentication
- **Data Encryption**: Sensitive data protection
- **Network Security**: HTTPS communication with API services

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Tunahan Büyükgebiz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```
