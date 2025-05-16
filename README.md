# 🛒 **Cruebee - Ứng dụng đặt thức ăn nhanh**

## 👨‍💻 **Nhóm 42**

| Họ tên                              | MSSV         |
| ----------------------------------- | ------------ |
| Lê Huỳnh Như Nguyệt                 | 22110385     |
| Phạm Ngọc Hòa                       | 22110330     |

---

## 📌 **Giới Thiệu Dự Án**

**Mục đích dự án**
- Với sự phát triển vượt bậc của thời đại công nghệ số, người dùng ngày nay có xu hướng tiếp cận các dịch vụ một cách nhanh chóng và tiện lợi thông qua các thiết bị di động. Nhận thấy nhu cầu mua sắm trực tuyến, đặc biệt là trong lĩnh vực ẩm thực, ngày càng được quan tâm bởi nhiều đối tượng người dùng, nhóm sinh viên chúng em đã lựa chọn thực hiện đề tài ***Ứng dụng đặt thức ăn nhanh – Cruebee***.
- Ứng dụng này được xây dựng nhằm hỗ trợ người tiêu dùng trong việc đặt món ăn nhanh tại nhà, giúp tiết kiệm thời gian, chi phí, đồng thời mang lại sự tiện lợi và trải nghiệm tốt hơn. Bên cạnh đó, ứng dụng cũng góp phần hỗ trợ các cửa hàng, quán ăn mở rộng kênh phân phối, gia tăng nguồn thu nhập, và tiếp cận nhiều khách hàng hơn trong thời đại số.

---
**Công nghệ sử dụng**
Để xây dựng và phát triển ứng dụng ***Cruebee – Ứng dụng đặt thức ăn nhanh*** , nhóm sử dụng các công nghệ sau:
- **Android(Java)**:
  Nền tảng chính để xây dựng giao diện người dùng và xử lý các thao tác trên thiết bị di động.
- **Volley**:
  Thư viện của Android hỗ trợ thực hiện các yêu cầu mạng (HTTP), dùng để giao tiếp với hệ thống backend thông qua API.
- **SpringBoot(Java)**:
  Framework phía backend dùng để xây dựng các RESTful API, xử lý logic nghiệp vụ và truy xuất cơ sở dữ liệu.
- **MySQL**:
  Hệ quản trị cơ sở dữ liệu dùng để lưu trữ thông tin người dùng, món ăn, đơn hàng và các dữ liệu liên quan đến hệ thống.

---

## ⚙️ **Cơ Chế Vận Hành**

**Hệ thống gồm 2 vai trò chính:**

- 👤 **USER (Người dùng)**

  - Đăng nhập, đăng ký, quên mật khẩu. 
  - Xem danh sách món ăn bán chạy, danh sách món đề xuất, danh sách sản phẩm theo phân loại. 
  - Quản lý giỏ hàng( thêm, xóa, sửa món ăn).
  - Xem chi tiết một sản phẩm.
  - Xem danh sách chương trình khuyến mãi, chi tiết của chương trình khuyến mãi. 
  - Chỉnh sửa tài khoản cá nhân.
  - Xem danh sách đơn hàng đã đặt, chi tiết đơn hàng, hủy đơn hàng. 
  - Xem danh sách địa chỉ nhận hàng, chi tiết của từng địa chỉ nhận hàng, thêm/xóa/sửa địa chỉ.
  - Thêm địa chỉ nhận hàng, chọn cửa hàng tùy theo địa chỉ nhận hàng mặc định.
  - Thanh toán đơn hàng
  - Thay đổi mật khẩu
  - Xóa tài khoản 

- 🧑‍💼 **GUEST(Khách)**
  
  - Đăng ký tài khoản. 
  - Xem danh sách món ăn bán chạy, danh sách món đề xuất, danh sách sản phẩm theo phân loại. 
  - Quản lý giỏ hàng( thêm, xóa, sửa món ăn).
  - Xem chi tiết một sản phẩm.
  - Xem danh sách chương trình khuyến mãi, chi tiết của chương trình khuyến mãi. 
  - Xem danh sách đơn hàng đã đặt, chi tiết đơn hàng, hủy đơn hàng. 
  - Thêm địa chỉ nhận hàng, chọn cửa hàng tùy theo địa chỉ nhận hàng mặc định.
  - Thanh toán đơn hàng

---

## 🛠️ **Hướng Dẫn Setup Dự Án**

### 🔧 **1. Cài đặt ban đầu**
Cách 1: Tạo thư mục → Clone lần lượt 2 repo dự án từ Github
- API : [https://github.com/MoonCruella/Cruebee-API-AndroidApp](https://github.com/MoonCruella/Cruebee-API-AndroidApp)
- Android APP: [https://github.com/MoonCruella/Cruebee-AndroidApp](https://github.com/MoonCruella/Cruebee-AndroidApp)
  
Cách 2: Download source code của toàn bộ dự án được sinh viên gửi trong phần nộp dự án cuối kì, sau đó tiến hành giải nén 

### 🗄️ **2. Cài đặt cơ sở dữ liệu**

- Mở **MySQL Workbench**:
  - Tạo database tên `ltdd`
  - Vào `Server → Data Import`
  - Chọn `Import from Dump Project Folder`
  - Chọn thư mục `db-cruebee-app` trong thư mục đi kèm với dự án 

### 💻 **3. Mở và chạy project**

- Mở **Android Studio**:
  - `File → Open → chọn thư mục vừa clone app về( hoặc chọn thư mục chứa project nếu tải trực tiếp source code về) 
- Mở **IntelliJ IDEA**
  - `File → Open → chọn thư mục vừa clone phần API về( hoặc chọn thư mục chứa API cho dự án nếu tải trực tiếp source code về) 
  - Mở file `application.yaml`, chỉnh sửa phần cấu hình database:
    ```yaml
    username: <Tên người dùng MySQL>
    password: <Mật khẩu MySQL>
    ```
- **RUN** bấm run cả 2 bên ứng dụng để tiến hành chạy ứng dụng Cruebee. 

