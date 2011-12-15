package vbay.dao;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vbay.model.TaiKhoan;

@Repository("taiKhoanDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TaiKhoanDaoImpl implements TaiKhoanDao {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = true)
    public TaiKhoan dangNhap(String tenTaiKhoan, String matKhau) {
        TaiKhoan taiKhoan = null;
        Session session = null;

        try {
            session = sessionFactory.getCurrentSession();

            String hqlString = "select tk from TaiKhoan tk"
                    + " where tk.tenTaiKhoan = :tenTaiKhoan and tk.matKhau = :matKhau and tk.coHieuLuc = 1";

            taiKhoan = (TaiKhoan) session.createQuery(hqlString)
                    .setString("tenTaiKhoan", tenTaiKhoan).setString("matKhau", matKhau).list()
                    .get(0);
        } catch (Exception e) {

            e.printStackTrace();

            taiKhoan = null;
        }

        return taiKhoan;
    }

}