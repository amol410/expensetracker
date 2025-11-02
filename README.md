# Expense Tracker - Android App

A modern, feature-rich expense tracking application built with Jetpack Compose and Firebase, designed to help users manage their personal finances efficiently.

## Overview

Expense Tracker is a native Android application that enables users to track daily expenses, visualize spending patterns, and maintain better financial awareness. The app features a clean, intuitive interface built with Material Design 3 principles and leverages Firebase for authentication and crash reporting.

## Features

### Core Functionality
- **Expense Management**: Add, edit, and delete expense entries with detailed information
- **Category Organization**: Organize expenses into predefined categories (Food, Transport, Shopping, Bills, Entertainment, Health, Education, Other)
- **Multi-Currency Support**: Track expenses in multiple currencies with proper formatting
- **User Authentication**: Secure email/password authentication via Firebase
- **Dashboard Analytics**: Visual overview of spending patterns and statistics
- **Monthly Tracking**: View and analyze expenses by month
- **Statistics & Insights**: Comprehensive spending analysis with category-wise breakdowns

### Technical Features
- **Offline-First Architecture**: Local database with Room for seamless offline functionality
- **Real-time Updates**: Reactive UI with Kotlin Flows and StateFlow
- **Crash Reporting**: Integrated Firebase Crashlytics for stability monitoring
- **Material Design 3**: Modern UI with dynamic theming and smooth animations
- **MVVM Architecture**: Clean separation of concerns with ViewModel pattern
- **Dependency Injection**: Dagger Hilt for efficient dependency management

## Tech Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)

### Libraries & Dependencies
- **Architecture Components**
  - ViewModel & LiveData
  - Room Database
  - Navigation Compose

- **Dependency Injection**
  - Dagger Hilt

- **Firebase Services**
  - Firebase Authentication
  - Firebase Crashlytics
  - Firebase Analytics

- **UI & Design**
  - Material Design 3 (Material3)
  - Compose Material Icons Extended

- **Reactive Programming**
  - Kotlin Coroutines
  - Kotlin Flows

## Project Structure

```
app/src/main/java/com/dolphin/expense/
├── auth/                    # Authentication management
├── data/                    # Data layer
│   ├── dao/                # Database access objects
│   ├── database/           # Room database configuration
│   └── repository/         # Repository implementations
├── di/                      # Dependency injection modules
├── navigation/             # Navigation configuration
├── ui/
│   ├── components/         # Reusable UI components
│   ├── screens/            # App screens
│   ├── theme/              # Theme and styling
│   └── viewmodel/          # ViewModels
└── utils/                  # Utility classes
```

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns:

- **View Layer**: Jetpack Compose UI components
- **ViewModel Layer**: Business logic and state management
- **Repository Layer**: Data abstraction and coordination
- **Data Layer**: Room database and Firebase integration

### Key Architectural Decisions
- Single Activity architecture with Compose Navigation
- Repository pattern for data access abstraction
- StateFlow for reactive state management
- Hilt for compile-time dependency injection

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK with API 34
- Firebase account

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/amol410/expensetracker.git
   cd expensetracker
   ```

2. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app to your Firebase project
   - Download `google-services.json`
   - Place it in the `app/` directory
   - Enable Firebase Authentication (Email/Password)
   - Enable Firebase Crashlytics

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Open the project in Android Studio
   - Sync Gradle files
   - Run on an emulator or physical device

### Configuration

Create a `local.properties` file in the root directory (if not present):
```properties
sdk.dir=/path/to/your/Android/Sdk
```

## Usage

### First Time Setup
1. Launch the app
2. Select your preferred currency
3. Create an account or sign in
4. Start adding expenses

### Adding Expenses
1. Tap the "+" button on the dashboard
2. Enter expense details (amount, category, description, date)
3. Save the expense

### Viewing Statistics
- Navigate to the Statistics tab to view spending breakdowns
- Analyze expenses by category
- Track monthly spending trends

## Database Schema

### ExpenseEntity
- `id`: Primary key (auto-generated)
- `amount`: Expense amount
- `category`: Expense category
- `description`: Optional description
- `date`: Expense date
- `userId`: Associated user ID
- `timestamp`: Creation timestamp

### BudgetEntity
- Budget tracking for future features
- Category-wise budget allocation

## Firebase Integration

### Authentication
- Email/password authentication
- Secure user session management
- Automatic token refresh

### Crashlytics
- Real-time crash reporting
- Custom event logging
- Performance monitoring

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Security & Privacy

- User authentication via Firebase
- Local data encryption with Room
- Secure credential storage
- No personal data shared with third parties
- Comprehensive privacy policy included

See [PRIVACY_POLICY.md](PRIVACY_POLICY.md) for details.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Roadmap

### Planned Features
- [ ] Budget setting and alerts
- [ ] Recurring expense support
- [ ] Data export (CSV, PDF)
- [ ] Cloud backup and sync
- [ ] Receipt photo attachments
- [ ] Multiple account support
- [ ] Dark mode theme
- [ ] Expense sharing between users
- [ ] Advanced analytics and reports

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Backend services by [Firebase](https://firebase.google.com/)
- Icons from [Material Design Icons](https://material.io/resources/icons/)

## Contact

**Developer**: Amol
**GitHub**: [@amol410](https://github.com/amol410)
**Repository**: [expensetracker](https://github.com/amol410/expensetracker)

## Support

If you encounter any issues or have questions:
1. Check the [Issues](https://github.com/amol410/expensetracker/issues) page
2. Create a new issue with detailed information
3. Review the [documentation](https://github.com/amol410/expensetracker/wiki)

---

**Note**: This app requires a `google-services.json` file from Firebase to build. The file is not included in the repository for security reasons. Follow the Firebase Setup instructions above to obtain your own configuration file.

Made with ❤️ using Kotlin and Jetpack Compose
