# Notification Always

Notification Always giúp chẩn đoán các cài đặt có thể làm thông báo đến chậm trên điện thoại dùng ROM quản lý nền chặt, thường gặp ở Xiaomi/Redmi/POCO, OPPO/realme/OnePlus, vivo/iQOO, HONOR và Huawei.

## Chức năng

- Kiểm tra Google Play Services và Google Play Store có được cài và bật hay không bằng API sẵn có của Android. Kiểm tra này không xác định ROM có đầy đủ tính năng Google hay không.
- Hiển thị trạng thái hạn chế nền và tối ưu pin của Notification Always.
- Mở nhanh cài đặt Google Play Services, pin và tự khởi chạy để người dùng kiểm tra cài đặt trên ROM. Người dùng có thể cho phép app bỏ qua tối ưu pin khi bật thử nghiệm định kỳ.
- Cho phép **thử nghiệm** gửi tín hiệu tới Google Play Services theo lịch 15 phút khi sạc hoặc dùng mạng di động, 20 phút trong điều kiện thường, 30 phút khi bật tiết kiệm pin, và 60 phút khi pin dưới 20% (trừ khi đang sạc). Tính năng này mặc định tắt và không xác nhận Google Play Services đã nhận hoặc xử lý tín hiệu.
- Ghi riêng lần alarm thực sự chạy và lần app thử gửi tín hiệu.

Ứng dụng không yêu cầu quyền Internet, không đọc thông báo của ứng dụng khác và không gửi dữ liệu chẩn đoán tới máy chủ.

## Cách sử dụng

1. Tải APK mới nhất trong mục **Releases**, cài đặt và mở ứng dụng.
2. Xem **Chẩn đoán trên máy**. Nếu Google Play Services bị thiếu hoặc tắt, xử lý tình trạng đó trước. Phiên bản hiển thị chỉ để đối chiếu; app không kết luận phiên bản đó có cập nhật hay đủ khả năng nhận FCM.
3. Cho phép Google Play Services và từng ứng dụng chat, email hoặc ngân hàng cần nhận tin chạy nền, tự khởi chạy và hiển thị thông báo trong cài đặt ROM.
4. Nếu muốn đánh giá cơ chế gửi tín hiệu, bật **thử nghiệm định kỳ** và so sánh độ trễ thông báo trên cùng điện thoại khi bật và tắt. Có thể tắt bất cứ lúc nào.

## Giới hạn

Trạng thái cài đặt của Google Play Services không xác nhận kết nối FCM đang hoạt động. Thời gian "thử gửi tín hiệu" cũng không phải bằng chứng thông báo đã được giao. Các broadcast thử nghiệm không phải API được Google hỗ trợ, nên hiệu quả cần được đo trên máy thật. Android hoặc ROM có thể trì hoãn hay chặn alarm, đặc biệt khi ứng dụng bị hạn chế nền. Nội dung và mức ưu tiên thông báo do ứng dụng gửi/nhận quyết định; Notification Always không thể thay đổi chúng.

Xem [hướng dẫn kiểm tra trên điện thoại thật](docs/device-validation.md) để đánh giá tác dụng của tính năng thử nghiệm.
