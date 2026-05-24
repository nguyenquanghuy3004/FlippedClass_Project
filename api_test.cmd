@echo off
chcp 65001 >nul
echo ========================================================
echo FILE SCRIPT CMD TEST API - FLIPPED CLASS
echo Yêu cầu: Đảm bảo Spring Boot đang chạy ở cổng 8080
echo ========================================================
echo.

echo [1] ĐĂNG KÝ TÀI KHOẢN (SIGN UP)
curl -X POST http://localhost:8080/api/auth/signup -H "Content-Type: application/json" -d "{\"username\": \"hocsinh1\", \"email\": \"hocsinh1@gmail.com\", \"password\": \"Password123\", \"role\": [\"student\"]}"
echo.
echo.

echo [2] ĐĂNG NHẬP (SIGN IN)
curl -X POST http://localhost:8080/api/auth/signin -H "Content-Type: application/json" -d "{\"username\": \"hocsinh1\", \"password\": \"Password123\"}"
echo.
echo.
echo --- LƯU Ý: COPY CHUỖI 'accessToken' TỪ KẾT QUẢ TRÊN ĐỂ DÙNG CHO CÁC API DƯỚI ĐÂY ---
echo.
set /p TOKEN="Dán Token vào đây (chuỗi dài bắt đầu bằng ey...): "
echo.

echo [3] HOÀN THIỆN HỒ SƠ SINH VIÊN
curl -X POST http://localhost:8080/api/auth/complete-profile -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"studentCode\": \"HE160000\", \"className\": \"SE1601\", \"major\": \"Software Engineering\", \"enrollmentYear\": 2021}"
echo.
echo.

echo [4] TẠO LỚP HỌC MỚI
curl -X POST http://localhost:8080/api/learning-spaces -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"name\": \"Lớp học Lập trình Web Căn bản\", \"description\": \"Hướng dẫn xây dựng website\", \"visibility\": \"PUBLIC\"}"
echo.
echo.

echo [5] XIN VÀO LỚP HỌC (JOIN)
set /p INVITE_CODE="Nhập mã mời (Invite Code) vừa tạo ở bước trên: "
curl -X POST http://localhost:8080/api/learning-spaces/join -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"inviteCode\": \"%INVITE_CODE%\"}"
echo.
echo.

echo [6] ĐỔI MẬT KHẨU
curl -X POST http://localhost:8080/api/auth/change-password -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"oldPassword\": \"Password123\", \"newPassWord\": \"NewPassword456\", \"confirmPassword\": \"NewPassword456\"}"
echo.
echo.

echo ========================================================
echo TEST API ĐÃ XONG!
pause
