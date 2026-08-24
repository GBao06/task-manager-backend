# Task Manager Backend (Spring Boot + MySQL)

Backend cho đề tài "Xây dựng nền tảng quản lý công việc cho nhóm phát triển
phần mềm ứng dụng ma trận Eisenhower", theo kiến trúc **Modular Monolith**
đã thiết kế ở Chương 2.

## 1. Yêu cầu cài đặt trước

- **JDK 17** trở lên
- **Maven** (Eclipse thường có sẵn plugin m2e)
- **MySQL** (có thể dùng MySQL Server cài riêng, hoặc XAMPP/WAMP đã có sẵn MySQL)

## 2. Chuẩn bị database

Không bắt buộc phải tạo database thủ công — file cấu hình đã bật
`createDatabaseIfNotExist=true`, Spring Boot sẽ tự tạo database
`taskmanager_db` khi chạy lần đầu (miễn là user MySQL có quyền tạo database).

Nếu muốn tự tạo trước cho chắc, mở MySQL Workbench / phpMyAdmin / terminal
mysql, chạy:

```sql
CREATE DATABASE taskmanager_db;
```

Sau đó mở file `src/main/resources/application.properties`, sửa lại
`spring.datasource.username` và `spring.datasource.password` cho đúng với
MySQL trên máy bạn (mặc định XAMPP: username `root`, password để trống).

## 3. Import project vào Eclipse

1. Mở Eclipse → **File → Import... → Maven → Existing Maven Projects**
2. Chọn thư mục `task-manager-backend` (thư mục chứa file `pom.xml`)
3. Eclipse sẽ tự tải các thư viện (Spring Boot, JPA, Security, JWT...) —
   lần đầu có thể mất vài phút vì cần tải qua mạng
4. Nếu Eclipse báo thiếu "Maven Integration (m2e)": vào
   **Help → Eclipse Marketplace**, tìm "m2e" và cài vào

Nếu gặp lỗi `ClassNotFoundException` khi chạy: chuột phải project →
**Maven → Update Project... (Alt+F5)** → tick "Force Update" → OK, sau đó
**Project → Clean...** rồi chạy lại.

## 4. Chạy chương trình

Đảm bảo **MySQL Server đang chạy** (nếu dùng XAMPP: mở XAMPP Control Panel,
bấm Start ở dòng MySQL).

Chuột phải vào `TaskManagerApplication.java` → **Run As → Java Application**

Nếu chạy thành công, Hibernate sẽ tự tạo bảng `users` trong database
(do `spring.jpa.hibernate.ddl-auto=update`), và server chạy ở:

```
http://localhost:8080
```

## 5. Kiểm thử nhanh API (dùng Postman hoặc curl)

**Đăng ký (UC01):**
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Dương Gia Bảo",
  "email": "bao@example.com",
  "password": "123456"
}
```

**Đăng nhập (UC02):**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "bao@example.com",
  "password": "123456"
}
```

Cả hai API đều trả về `token` (JWT) — dùng token này ở header
`Authorization: Bearer <token>` cho các API khác sau này (cần đăng nhập).

## 6. Cấu trúc thư mục theo module (Modular Monolith)

```
com.taskmanager
├── auth/          → Auth Module (đã code: đăng ký, đăng nhập, JWT)
├── config/         → Cấu hình Security, JWT filter
├── task/           → Task Module (sẽ code tiếp: CRUD công việc)
├── matrix/         → Matrix Engine (sẽ code tiếp: gợi ý phân loại UC08)
├── sprint/         → Sprint Module (UC09, UC10)
├── gantt/          → Gantt/CPM Module (UC13, UC14, UC18)
├── team/           → Team Module (UC03, UC04)
└── notification/   → Notification Module (UC12)
```

Mỗi module khi code tiếp nên có: `Entity` (bảng dữ liệu) → `Repository`
(truy vấn) → `Service` (nghiệp vụ) → `Controller` (API), theo đúng khuôn
mẫu của module `auth` đã làm sẵn.

## 7. Gợi ý thứ tự code tiếp theo

1. **Team Module**: Entity `Team`, `TeamMember` (theo ERD) → API tạo
   nhóm, mời thành viên (UC03, UC04)
2. **Task Module**: Entity `Task` → CRUD công việc cơ bản (UC05, UC06)
3. **Matrix Engine**: logic tính điểm + gợi ý phân loại (UC07, UC08)
4. **Sprint Module** → **Gantt/CPM Module** → **Notification Module**

## 8. Lưu ý khi dùng MySQL (khác với PostgreSQL)

- Cột `id` kiểu UUID được lưu dưới dạng `CHAR(36)` trong MySQL (đã cấu hình
  sẵn trong `User.java` bằng `@Column(columnDefinition = "CHAR(36)")`) —
  khi tạo Entity mới cho các bảng khác (Task, Team, Sprint...), nhớ thêm
  đúng annotation này cho trường `id`.
- Nếu dùng XAMPP, nhớ bật MySQL trong XAMPP Control Panel trước khi chạy
  Spring Boot, nếu không sẽ báo lỗi kết nối
  `Communications link failure`.
