# 📅 Event Manager App

Ứng dụng quản lý sự kiện cá nhân trên Android với giao diện lịch tuần và hệ thống nhắc nhở thông minh.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MinSDK](https://img.shields.io/badge/Min%20SDK-24-green?style=for-the-badge)
![TargetSDK](https://img.shields.io/badge/Target%20SDK-36-blue?style=for-the-badge)

## 🎯 Tổng quan

**Event Manager App** là ứng dụng Android giúp người dùng quản lý lịch trình cá nhân một cách hiệu quả. Ứng dụng cung cấp giao diện lịch tuần trực quan, cho phép tạo, chỉnh sửa, xóa sự kiện và nhận thông báo nhắc nhở đúng giờ.

### Điểm nổi bật

- 🗓️ **Lịch tuần trực quan**: Hiển thị sự kiện theo buổi sáng/chiều cho 7 ngày
- ⏰ **Nhắc nhở thông minh**: Thông báo tùy chỉnh (đúng giờ, 15 phút, 30 phút, 1 giờ, 1 ngày trước)
- 👤 **Đa người dùng**: Hệ thống đăng nhập/đăng ký với quản lý session
- 🔒 **Bảo mật**: Mỗi user chỉ xem và quản lý sự kiện của mình
- ✅ **100% Test Coverage**: Đầy đủ unit tests cho tất cả components

## ✨ Tính năng

### 🔐 Quản lý tài khoản
- Đăng ký tài khoản mới
- Đăng nhập/Đăng xuất
- Quản lý session người dùng
- Mật khẩu và tên đăng nhập được validate

### 📆 Quản lý sự kiện
- **Tạo sự kiện mới**:
  - Chọn ngày, giờ bắt đầu và kết thúc
  - Thêm tiêu đề và ghi chú
  - Cấu hình thời gian nhắc nhở

- **Xem chi tiết sự kiện**:
  - Hiển thị đầy đủ thông tin
  - Định dạng ngày giờ dễ đọc
  - Thông tin nhắc nhở

- **Chỉnh sửa sự kiện**:
  - Cập nhật mọi thông tin
  - Tự động cập nhật alarm
  - Validate dữ liệu

- **Xóa sự kiện**:
  - Xác nhận trước khi xóa
  - Tự động hủy alarm
  - Kiểm tra quyền sở hữu

### 🗓️ Giao diện lịch tuần
- Hiển thị 7 ngày trong tuần
- Phân chia buổi sáng (< 12:00) và chiều (≥ 12:00)
- Click vào ô để tạo sự kiện nhanh
- Chuyển tuần/tháng dễ dàng
- Hiển thị tất cả sự kiện trong tuần

### 🔔 Hệ thống thông báo
- Thông báo push đúng giờ
- Nhắc trước: 15 phút, 30 phút, 1 giờ, 1 ngày
- Click notification để xem chi tiết
- Yêu cầu quyền "Exact Alarm" (Android 12+)
- Kênh thông báo tùy chỉnh

## 🏗️ Kiến trúc

Dự án sử dụng **Clean Architecture** kết hợp với **Repository Pattern** để đảm bảo code dễ maintain và test.

```
┌─────────────────────────────────────────────────┐
│           PRESENTATION LAYER                     │
│  (Activities, Adapters, Views)                  │
│  - MainActivity                                  │
│  - LoginActivity, RegisterActivity               │
│  - AddEventActivity, EditEventActivity           │
│  - EventDetailActivity                           │
│  - EventAdapter                                  │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│             DOMAIN LAYER                         │
│  (Use Cases, Models, Business Logic)            │
│  - LoginUseCase, RegisterUseCase                │
│  - CreateEventUseCase, UpdateEventUseCase        │
│  - DeleteEventUseCase, GetEventsUseCase          │
│  - Event Model, User Model                       │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              DATA LAYER                          │
│  (Repository, DAO, Database)                     │
│  - EventRepository                               │
│  - EventDao, UserDao                             │
│  - AppDatabase (SQLite)                          │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│              UTILS LAYER                         │
│  (Helpers, Validators, Schedulers)               │
│  - Validator                                     │
│  - DateTimeHelper                                │
│  - SessionManager                                │
│  - AlarmScheduler, AlarmReceiver                 │
└──────────────────────────────────────────────────┘
```

### Các thành phần chính

#### 1. Presentation Layer
- **Activities**: Quản lý UI và user interactions
- **Adapters**: Hiển thị danh sách events trong RecyclerView
- **ViewModels**: (Optional) Quản lý UI state

#### 2. Domain Layer
- **Use Cases**: Business logic cho từng tính năng
- **Models**: Entity classes (Event, User)
- **Interfaces**: Contracts giữa các layers

#### 3. Data Layer
- **Repository**: Abstraction cho data sources
- **DAO**: Database Access Objects
- **Database**: SQLite với AppDatabase

#### 4. Utils Layer
- **Validator**: Validation logic
- **DateTimeHelper**: Format và parse date/time
- **SessionManager**: Quản lý session đăng nhập
- **AlarmScheduler**: Schedule và quản lý alarms

## 🛠️ Công nghệ

### Core Technologies
- **Language**: Java 11
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 15 (API 36)
- **Compile SDK**: 36

### Android Components
- **Activities**: Quản lý màn hình
- **RecyclerView**: Hiển thị danh sách
- **AlarmManager**: Schedule notifications
- **NotificationManager**: Push notifications
- **SQLite**: Local database
- **SharedPreferences**: Session management
- **BroadcastReceiver**: Nhận alarm events

### Libraries & Dependencies
```gradle
// UI Components
implementation 'androidx.appcompat:appcompat:1.7.1'
implementation 'com.google.android.material:material:1.13.0'
implementation 'androidx.constraintlayout:constraintlayout:2.2.1'
implementation 'androidx.activity:activity:1.12.1'

// Testing
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.robolectric:robolectric:4.13'
testImplementation 'org.mockito:mockito-core:5.14.2'
androidTestImplementation 'androidx.test.ext:junit:1.3.0'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.7.0'
```

### Design Patterns
- **Clean Architecture**: Separation of concerns
- **Repository Pattern**: Abstraction của data layer
- **Use Case Pattern**: Encapsulate business logic
- **Singleton Pattern**: Database và Repository instances
- **Observer Pattern**: RecyclerView adapters
- **Builder Pattern**: Notification builders

## 📦 Cài đặt

### Yêu cầu hệ thống
- **Android Studio**: Arctic Fox (2020.3.1) trở lên
- **JDK**: 11 trở lên
- **Android SDK**: API 24-36
- **Gradle**: 8.13.2

### Clone và Setup

1. **Clone repository**
```bash
git clone https://github.com/yourusername/EventManagerApp.git
cd EventManagerApp
```

2. **Cấu hình Android SDK**

Tạo file `local.properties` trong thư mục root:
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

Hoặc để Gradle tự detect:
```bash
# On Windows
set ANDROID_HOME=C:\Users\YourUsername\AppData\Local\Android\Sdk

# On Mac/Linux
export ANDROID_HOME=/Users/YourUsername/Library/Android/sdk
```

3. **Build project**
```bash
# Clean và build
./gradlew clean build

# Hoặc build debug APK
./gradlew assembleDebug
```

4. **Chạy tests**
```bash
# Run tất cả unit tests
./gradlew test

# Run với report chi tiết
./gradlew test --info
```

5. **Install trên device/emulator**
```bash
# Cần có device đã kết nối
./gradlew installDebug

# Hoặc run từ Android Studio
# Nhấn Run (Shift + F10)
```

## 🚀 Sử dụng

### Khởi động lần đầu

1. **Đăng ký tài khoản**
   - Mở app → Màn hình Login
   - Click "Đăng ký tài khoản"
   - Nhập thông tin (username ≥ 3 ký tự, password ≥ 6 ký tự)
   - Submit → Tự động đăng nhập

2. **Cấp quyền thông báo**
   - Android 13+: Cho phép notification permission
   - Tất cả: Cho phép "Alarms & reminders" để đặt nhắc nhở chính xác

### Sử dụng cơ bản

#### Xem lịch tuần
- Màn hình chính hiển thị tuần hiện tại
- Phía trên: 7 header cho 7 ngày
- Mỗi ngày có 2 ô: Buổi sáng (trắng) và Buổi chiều (xám)
- Sự kiện hiển thị dạng card với tiêu đề và giờ

#### Tạo sự kiện mới
**Cách 1: Click vào ô ngày**
- Click vào ô sáng/chiều của ngày muốn tạo
- Ngày tự động điền sẵn
- Nhập tiêu đề, ghi chú
- Chọn giờ bắt đầu và kết thúc
- Chọn thời gian nhắc nhở
- Nhấn "Lưu"

**Cách 2: Dùng nút "+"**
- Click nút "+" trên toolbar
- Chọn ngày từ date picker
- Tiếp tục như cách 1

#### Xem chi tiết sự kiện
- Click vào card sự kiện trong lịch
- Màn hình detail hiển thị đầy đủ thông tin
- Có thể Edit hoặc Delete từ đây

#### Chỉnh sửa sự kiện
- Từ màn hình detail → Nhấn "Sửa"
- Cập nhật thông tin cần thiết
- Nhấn "Lưu" → Alarm tự động update

#### Xóa sự kiện
- Từ màn hình detail → Nhấn "Xóa"
- Xác nhận xóa
- Sự kiện và alarm bị xóa vĩnh viễn

#### Chuyển tuần/tháng
- Click vào ngày hiện tại ở trên cùng
- Chọn ngày mới từ calendar picker
- Lịch tự động load tuần chứa ngày đó

#### Đăng xuất
- Nhấn icon đăng xuất trên toolbar
- Xác nhận → Về màn hình login

## 🧪 Testing

### Test Suite

Dự án có **210+ test cases** với **100% coverage** cho business logic.

```bash
# Run tất cả tests
./gradlew test

# Run tests cho một module cụ thể
./gradlew app:testDebugUnitTest

# Run test cho một class
./gradlew test --tests EventTest
./gradlew test --tests ValidatorTest
```

### Test Report
Sau khi chạy tests, xem report HTML tại:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### Test Coverage Details

| Component | Files | Test Cases | Coverage |
|-----------|-------|------------|----------|
| Domain Models | 2 | 45 | 100% |
| Utilities | 3 | 84 | 100% |
| Use Cases | 6 | 63 | 100% |
| Repository | 1 | 18 | 100% |
| **TOTAL** | **12** | **210** | **100%** |

#### Test Files
- **Models**: `EventTest`, `UserTest`
- **Utils**: `ValidatorTest`, `DateTimeHelperTest`, `SessionManagerTest`
- **Use Cases**: `LoginUseCaseTest`, `RegisterUseCaseTest`, `CreateEventUseCaseTest`, `UpdateEventUseCaseTest`, `DeleteEventUseCaseTest`, `GetEventsUseCaseTest`
- **Repository**: `EventRepositoryTest`

### Testing Technologies
- **JUnit 4.13.2**: Test framework chính
- **Robolectric 4.13**: Android unit testing
- **Mockito 5.14.2**: Mocking framework

Chi tiết về test suite: [TEST_DOCUMENTATION.md](TEST_DOCUMENTATION.md)

## 📁 Cấu trúc dự án

```
EventManagerApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/eventmanagerapp/
│   │   │   │   ├── adapter/
│   │   │   │   │   └── EventAdapter.java
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── AppDatabase.java
│   │   │   │   │   │   ├── EventDao.java
│   │   │   │   │   │   └── UserDao.java
│   │   │   │   │   └── repository/
│   │   │   │   │       └── EventRepository.java
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Event.java
│   │   │   │   │   │   └── User.java
│   │   │   │   │   └── usecase/
│   │   │   │   │       ├── CreateEventUseCase.java
│   │   │   │   │       ├── UpdateEventUseCase.java
│   │   │   │   │       ├── DeleteEventUseCase.java
│   │   │   │   │       ├── GetEventsUseCase.java
│   │   │   │   │       ├── LoginUseCase.java
│   │   │   │   │       └── RegisterUseCase.java
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── AddEventActivity.java
│   │   │   │   │   ├── EditEventActivity.java
│   │   │   │   │   ├── EventDetailActivity.java
│   │   │   │   │   └── auth/
│   │   │   │   │       ├── LoginActivity.java
│   │   │   │   │       └── RegisterActivity.java
│   │   │   │   └── utils/
│   │   │   │       ├── AlarmReceiver.java
│   │   │   │       ├── AlarmScheduler.java
│   │   │   │       ├── DateTimeHelper.java
│   │   │   │       ├── SessionManager.java
│   │   │   │       └── Validator.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── activity_add_event.xml
│   │   │   │   │   ├── activity_edit_event.xml
│   │   │   │   │   ├── activity_event_detail.xml
│   │   │   │   │   ├── view_week_calendar.xml
│   │   │   │   │   └── item_event.xml
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── themes.xml
│   │   │   │   │   └── arrays.xml
│   │   │   │   └── xml/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/com/example/eventmanagerapp/
│   │           ├── domain/
│   │           │   ├── model/
│   │           │   │   ├── EventTest.java
│   │           │   │   └── UserTest.java
│   │           │   └── usecase/
│   │           │       ├── CreateEventUseCaseTest.java
│   │           │       ├── UpdateEventUseCaseTest.java
│   │           │       ├── DeleteEventUseCaseTest.java
│   │           │       ├── GetEventsUseCaseTest.java
│   │           │       ├── LoginUseCaseTest.java
│   │           │       └── RegisterUseCaseTest.java
│   │           ├── data/
│   │           │   └── repository/
│   │           │       └── EventRepositoryTest.java
│   │           └── utils/
│   │               ├── ValidatorTest.java
│   │               ├── DateTimeHelperTest.java
│   │               └── SessionManagerTest.java
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── local.properties
├── README.md
└── TEST_DOCUMENTATION.md
```

## 🔧 Cấu hình nâng cao

### Database Schema

#### Users Table
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    full_name TEXT,
    created_at INTEGER NOT NULL
);
```

#### Events Table
```sql
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    note TEXT,
    start_time INTEGER NOT NULL,
    end_time INTEGER NOT NULL,
    remind_before INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);
```

### Permissions

#### AndroidManifest.xml
```xml
<!-- Notification permission (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Exact alarm permission -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### ProGuard Rules
Nếu build release, thêm rules để giữ các class:
```proguard
# Keep domain models
-keep class com.example.eventmanagerapp.domain.model.** { *; }

# Keep use case Result classes
-keep class com.example.eventmanagerapp.domain.usecase.**$Result { *; }
```

## 🐛 Troubleshooting

### Lỗi thường gặp

#### 1. SDK location not found
**Giải pháp**: Tạo file `local.properties` với đường dẫn SDK

#### 2. Notification không hiển thị
**Giải pháp**: 
- Kiểm tra permission đã được cấp
- Android 13+: Kiểm tra POST_NOTIFICATIONS
- Kiểm tra notification channel đã được tạo

#### 3. Alarm không chạy đúng giờ
**Giải pháp**:
- Vào Settings → Apps → EventManagerApp → Alarms & reminders
- Enable "Allow setting alarms and reminders"
- Đảm bảo app không bị battery optimization

#### 4. Tests fail
**Giải pháp**:
- Chạy `./gradlew clean`
- Xóa folder `.gradle` và build lại
- Kiểm tra JDK version (cần Java 11+)

#### 5. Build failed - Gradle sync
**Giải pháp**:
- File → Invalidate Caches / Restart
- Xóa `.gradle` folder
- `./gradlew --stop` rồi build lại

## 🤝 Đóng góp

Contributions, issues và feature requests được chào đón!

### Quy trình đóng góp

1. Fork project
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

### Coding Standards
- Tuân thủ Clean Architecture
- Viết tests cho mọi business logic
- Comment rõ ràng cho code phức tạp
- Tuân thủ Java naming conventions

## 📄 License

```
MIT License

Copyright (c) 2025 Event Manager App

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
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
## 🙏 Acknowledgments

- Android Documentation
- Material Design Guidelines
- Stack Overflow Community
- Clean Architecture by Robert C. Martin
- Test-Driven Development practices
