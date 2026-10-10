# Notification Always

Ứng dụng Android mã nguồn mở do **CD** phát triển, hỗ trợ giảm tình trạng thông báo đến trễ trên điện thoại có ROM quản lý nền và pin mạnh như Xiaomi/Redmi/POCO, OPPO/realme/OnePlus, vivo/iQOO, HONOR và Huawei.

## Tính năng

- Gửi yêu cầu kiểm tra và kết nối lại Google Play Services theo lịch khoảng 15–30 phút.
- Tự khởi động lịch sau khi bật lại điện thoại hoặc cập nhật ứng dụng.
- Điều chỉnh chu kỳ theo trạng thái sạc, pin yếu, tiết kiệm pin và kiểu kết nối mạng.
- Lối tắt tới cài đặt pin, tự khởi chạy và Google Play Services.
- Giao diện, trạng thái thiết bị và hướng dẫn bằng tiếng Việt.
- Không yêu cầu quyền Internet; không gửi dữ liệu chẩn đoán.

## Cách sử dụng

1. Cài Notification Always và mở ứng dụng.
2. Bật công tắc **Bật kiểm tra kết nối**.
3. Mở cài đặt pin của Notification Always, chọn **Không hạn chế** hoặc tắt tối ưu pin.
4. Mở mục **Tự khởi chạy** của ROM và cho phép ứng dụng tự khởi chạy.
5. Trong cài đặt Google Play Services, cho phép chạy nền và chọn mức pin **Không hạn chế** nếu có.
6. Bật tự khởi chạy cho ứng dụng chat/ngân hàng cần nhận tin. Khóa các ứng dụng đó trong đa nhiệm nếu ROM có tùy chọn.

Ứng dụng hỗ trợ kiểm tra lại kết nối nhưng không bảo đảm thông báo đến tức thời. Một số ROM có thể trì hoãn alarm nền hoặc đóng ứng dụng đích. GMS cần được cài trên máy.

## Build và phát hành APK

Cần JDK 17 và Android SDK Platform 35. Build APK cục bộ bằng:

```powershell
.\gradlew.bat assembleRelease
```

Workflow GitHub Actions tạo GitHub Release kèm một APK Universal khi push tag phiên bản dạng `v*`. Trước lần phát hành đầu, thêm các GitHub Actions secrets sau trong **Settings > Secrets and variables > Actions**:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Dùng cùng một keystore cho tất cả phiên bản để người dùng cài bản mới đè lên bản cũ. Với mỗi bản cập nhật, tăng `versionCode` và `versionName` trong `app/build.gradle`, push mã lên nhánh `main`, tạo tag (ví dụ `v1.0.1`) rồi push tag đó.

Project hiện phát hành một APK Universal; chưa có bản Xiaomi riêng hỗ trợ ghi danh sách chống đóng băng HyperOS.
