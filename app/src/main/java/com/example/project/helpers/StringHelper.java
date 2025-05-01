package com.example.project.helpers;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringHelper {
    public static boolean isEmailValid(String email) {
        final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }
    public static boolean isValidVietnamPhone(String input) {
        // Loại bỏ ký tự không phải số
        String phone = input.replaceAll("[^0-9]", "");

        // Chuyển từ 84 -> 0 nếu cần
        if (phone.startsWith("84")) {
            phone = "0" + phone.substring(2);
        }

        // Kiểm tra độ dài và bắt đầu bằng các đầu số hợp lệ
        return (phone.length() == 10 || phone.length() == 11) && phone.matches("0[1-9][0-9]{8,9}");
    }
    public static boolean isValidPassword(String password){
        String PASSWORD_SPECIAL_CHARS = "@#$%^`<>&+=\"!ºª·#~%&'¿¡€,:;*/+-.=_\\[\\]\\(\\)\\|\\_\\?\\\\";
        int PASSWORD_MIN_SIZE = 8;
        String PASSWORD_REGEXP = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[" + PASSWORD_SPECIAL_CHARS + "])(?=\\S+$).{"+PASSWORD_MIN_SIZE+",}$";
        return password.matches(PASSWORD_REGEXP);
    }
    public static String getSubstringBetween(String str, char startChar, char endChar) {

        // Tìm vị trí của ký tự bắt đầu và kết thúc
        int startIndex = str.indexOf(startChar);
        int endIndex = str.indexOf(endChar, startIndex + 1);

        // Kiểm tra nếu tìm thấy cả hai ký tự
        if (startIndex != -1 && endIndex != -1) {

            // Cắt chuỗi từ startChar đến endChar
            return str.substring(startIndex + 1, endIndex).trim();
        } else {
            return "Không tìm thấy ký tự phù hợp!";
        }
    }
    public static String createPaymentIdsQuery(ArrayList<Integer> paymentIds) {

        // Sử dụng stream để tạo chuỗi theo định dạng "paymentIds=1&paymentIds=2&paymentIds=3"
        return paymentIds.stream()
                .map(id -> "paymentIds=" + id)  // Tạo chuỗi "paymentIds=ID"
                .collect(Collectors.joining("&"));  // Nối các phần tử lại với dấu "&"
    }
}
