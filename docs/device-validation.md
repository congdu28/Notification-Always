# Kiểm tra trên điện thoại thật

GitHub Actions kiểm tra mã, build APK và bảo đảm APK không yêu cầu quyền Internet. Nó không đo được độ trễ thông báo trên ROM nội địa.

## Kiểm tra chức năng của app

1. Cài APK mới và mở app. Thử nghiệm định kỳ phải đang tắt trên máy cài mới.
2. Xem trạng thái Google Play Services, Google Play Store, hạn chế nền và tối ưu pin. So sánh với cài đặt hệ thống trên máy; trạng thái cài/bật không chứng minh FCM hoạt động.
3. Nhấn **Thử gửi tín hiệu ngay**. Khi có mạng và Google Play Services đang bật, app cập nhật giờ thử gửi; màn hình vẫn ghi rõ Google Play Services chưa xác nhận xử lý. Khi mất mạng hoặc Google Play Services tắt, app phải báo đã bỏ qua và không cập nhật giờ thử gửi.
4. Bật thử nghiệm định kỳ. Xác nhận app hiện giờ alarm dự kiến. Để máy nghỉ qua ít nhất một chu kỳ, mở app và xem **Alarm chạy gần nhất**; giờ thực tế có thể trễ hơn dự kiến.
5. Tắt thử nghiệm. Sau lần alarm đã xếp, xác nhận không có lần gửi tín hiệu mới.
6. Nếu đã bật thử nghiệm, khởi động lại máy và kiểm tra app có xếp lại lịch hay không. Lặp lại khi bật chế độ hạn chế nền của ROM.
7. Bật tiết kiệm pin, giảm pin xuống dưới 15% hoặc ngắt mạng rồi xem chu kỳ chuyển sang 120 phút. Khi Google Play Services tắt, lịch tạm dừng; bật lại và mở app để xếp lịch lại.

## Đo hiệu quả đối với FCM

Cần một ứng dụng thử có đăng ký FCM và một máy gửi thử do người kiểm tra kiểm soát. Ghi thời điểm máy gửi chấp nhận yêu cầu và thời điểm ứng dụng thử nhận/hiển thị thông báo; không dùng giờ **Thử gửi tín hiệu** của Notification Always làm kết quả FCM.

Trên cùng điện thoại và cùng mạng, so sánh khi thử nghiệm định kỳ **tắt** và **bật**. Thử ở trạng thái màn hình tắt, Wi-Fi và mạng di động, sau khởi động lại, cùng với cài đặt pin/tự khởi chạy được giữ cố định. Ghi model, phiên bản ROM, Google Play Services, Google Play Store và độ trễ từng tin. Chỉ giữ cơ chế gửi định kỳ làm khuyến nghị nếu phép đo lặp lại cho thấy lợi ích rõ ràng so với lượng pin tiêu thụ.
