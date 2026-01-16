# OOP
# HE THONG QUAN LY TOUR DU LICH (Tourism Management System)

Dự án cuối kỳ môn Lập trình Hướng đối tượng (OOP), được xây dựng bằng Java và thư viện giao diện JavaFX. Hệ thống cung cấp giải pháp quản lý toàn diện cho một công ty du lịch, bao gồm quản lý tour, khách hàng, nhân viên, đặt chỗ (booking), hóa đơn và báo cáo doanh thu.

## Tinh nang chinh

Hệ thống được phân quyền chặt chẽ cho 3 vai trò: Manager (Quản lý), Sale (Nhân viên kinh doanh) và Accountant (Kế toán).

### 1. Manager (Quan ly)
* **Quan ly Tai khoan:** Cấp tài khoản mới cho nhân viên, khóa/mở khóa tài khoản, phân quyền.
* **Quan ly Nhan vien:** Thêm, sửa thông tin, cho nghỉ việc nhân viên.
* **Quan ly Tour:** Tạo tour mới, cập nhật thông tin tour (giá, lịch trình, số chỗ...).
* **Phe duyet Yeu cau:**
    * Duyệt yêu cầu hủy Booking từ bộ phận Sale (xem xét lý do, tính phí phạt).
    * Duyệt yêu cầu sửa đổi Hóa đơn từ bộ phận Kế toán.

### 2. Sale (Nhan vien Kinh doanh)
* **Quan ly Khach hang:** Thêm mới, cập nhật thông tin khách hàng.
* **Quan ly Dat cho (Booking):**
    * Tạo Booking mới cho khách (chọn Tour, số người).
    * Cập nhật số lượng người đi (có kiểm tra quy định thời gian trước ngày khởi hành).
    * Gửi yêu cầu hủy Booking lên Quản lý (kèm lý do hủy).
* **Tra cuu Tour:** Xem danh sách tour, tình trạng chỗ trống, tìm kiếm tour.

### 3. Accountant (Ke toan)
* **Quan ly Hoa don:**
    * Lập hóa đơn thanh toán cho các Booking (hỗ trợ thanh toán một phần hoặc toàn bộ - đặt cọc).
    * Gửi yêu cầu sửa hóa đơn (nếu có sai sót) lên Quản lý phê duyệt.
* **Bao cao & Thong ke:**
    * Xem báo cáo doanh thu theo khoảng thời gian tùy chọn.
    * Thống kê số lượng hóa đơn, số lượng booking.
    * Xuất báo cáo ra file CSV để lưu trữ.

---

## Cong nghe su dung

* **Ngon ngu:** Java (JDK 17 trở lên).
* **Giao dien (GUI):** JavaFX (FXML).
* **Luu tru du lieu:** JSON (Su dung thu vien Jackson de doc/ghi file).
    * Co che DataStore: Tu dong tao file du lieu data/*.json ben ngoai file JAR khi chay de tranh mat du lieu.
* **Quan ly du an:** Maven (quan ly thu vien va build).
* **Mo hinh thiet ke:** MVC (Model - View - Controller) ket hop Repository Pattern.

---

## Cau truc du an

src/main/java
├── app             # Chua ham main khoi chay (AppLauncher, MainApp)
├── controller      # Xu ly su kien giao dien (JavaFX Controllers)
├── ctrl            # Xu ly nghiep vu logic (Business Logic Layers)
├── model           # Cac lop thuc the (Account, Booking, Tour, Staff...)
├── repository      # Lop giao tiep voi file JSON (Doc/Ghi du lieu)
├── util            # Tien ich (DataStore, UserSession, JsonMapperFactory)
└── exception       # Cac Custom Exception (DuplicateException, InvalidDateException...)

src/main/resources
├── view            # Cac file giao dien .fxml
└── data            # Cac file JSON mau (Template)

---

## Tai khoan dang nhap (Demo)

He thong co san cac tai khoan mac dinh de kiem thu (Du lieu trong Account.json):

| Vai tro | Username | Password |
| :--- | :--- | :--- |
| Quan ly | manager | 123456 |
| Sale | sale | 123456 |
| Ke toan | accountant | 123456 |

---

## Huong dan cai dat & Chay

1. **Yeu cau:** May tinh da cai dat Java JDK (phien ban 11 tro len, khuyen nghi Java 17 hoac 21).
2. **Mo du an:** Mo thu muc du an bang IntelliJ IDEA hoac VS Code.
3. **Cai dat thu vien:**
    * Neu dung Maven: Doi IDE tai cac thu vien trong pom.xml (JavaFX, Jackson...).
4. **Chay ung dung:**
    * Tim file src/main/java/app/AppLauncher.java.
    * Chay file nay (Run Java Application).
    * Luu y: Khong chay truc tiep MainApp.java neu khong cau hinh VM Options cho JavaFX.

## Luu y quan trong
* Du lieu khi chay se duoc luu tai thu muc data/ nam ngang hang voi thu muc src. Ban co the xoa thu muc nay de reset du lieu ve mac dinh ban dau.
* Cac file bao cao xuat ra se nam trong thu muc exports/.
