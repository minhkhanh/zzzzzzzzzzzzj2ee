package vbay.dao;

import vbay.model.TaiKhoan;

public interface TaiKhoanDao {
    TaiKhoan dangNhap(String tenTaiKhoan, String matKhau);
    boolean kiemTraTonTaiTenTaiKhoan(String tenDangNhap);
}