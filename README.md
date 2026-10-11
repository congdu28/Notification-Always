# Notification Always

Notification Always giúp chẩn đoán các cài đặt có thể làm thông báo đến chậm trên điện thoại dùng ROM quản lý nền chặt, thường gặp ở Xiaomi/Redmi/POCO, OPPO/realme/OnePlus, vivo/iQOO, HONOR và Huawei.

## Chức năng

- Kiểm tra Google Play Services có được cài và liệu API Google Play có sẵn theo bộ kiểm tra chính thức; hiển thị trạng thái Google Play Store.
- Hiển thị trạng thái hạn chế nền và tối ưu pin của Notification Always.
- Mở nhanh cài đặt Google Play Services, pin và tự khởi chạy để người dùng kiểm tra cài đặt trên ROM.
- Cho phép **thử nghiệm** gửi tín hiệu tới Google Play Services theo lịch 15–30 phút. Tính năng này mặc định tắt và không xác nhận Google Play Services đã nhận hoặc xử lý tín hiệu.
- Ghi riêng lần alarm thực sự chạy và lần app thử gửi tín hiệu.

Ứng dụng không yêu cầu quyền Internet, không đọc thông báo của ứng dụng khác và không gửi dữ liệu chẩn đoán tới máy chủ.

## Cách sử dụng

1. Tải APK mới nhất trong mục **Releases**, cài đặt và mở ứng dụng.
2. Xem **Chẩn đoán trên máy**. Nếu Google Play Services bị thiếu, tắt hoặc cần cập nhật, xử lý tình trạng đó trước.
3. Cho phép Google Play Services và từng ứng dụng chat, email hoặc ngân hàng cần nhận tin chạy nền, tự khởi chạy và hiển thị thông báo trong cài đặt ROM.
4. Nếu muốn đánh giá cơ chế gửi tín hiệu, bật **thử nghiệm định kỳ** và so sánh độ trễ thông báo trên cùng điện thoại khi bật và tắt. Có thể tắt bất cứ lúc nào.

## Giới hạn

Trạng thái Google Play Services khả dụng không xác nhận kết nối FCM đang hoạt động. Thời gian "thử gửi tín hiệu" cũng không phải bằng chứng thông báo đã được giao. Android hoặc ROM có thể trì hoãn hay chặn alarm, đặc biệt khi ứng dụng bị hạn chế nền. Nội dung và mức ưu tiên thông báo do ứng dụng gửi/nhận quyết định; Notification Always không thể thay đổi chúng.

Xem [hướng dẫn kiểm tra trên điện thoại thật](docs/device-validation.md) để đánh giá tác dụng của tính năng thử nghiệm.
