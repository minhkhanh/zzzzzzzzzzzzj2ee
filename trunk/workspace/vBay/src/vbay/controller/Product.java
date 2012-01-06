package vbay.controller;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import vbay.dao.ChiTietDauGiaDao;
import vbay.dao.MultimediaDao;
import vbay.dao.SanPhamDao;
import vbay.model.ChiTietDauGia;
import vbay.model.ChiTietDauGiaId;
import vbay.model.Multimedia;
import vbay.model.SanPham;
import vbay.model.TaiKhoan;
import vbay.util.Utils;

@Controller
@RequestMapping("/Product.vby")
public class Product {

    @Autowired
    SanPhamDao sanPhamDao;

    @Autowired
    MultimediaDao multimediaDao;
    
    @Autowired
    ChiTietDauGiaDao chiTietDauGiaDao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView show(@RequestParam String maSanPham) {
        ModelAndView modelView = new ModelAndView("Product");

        SanPham sanPham = sanPhamDao.laySanPham(Integer.valueOf(maSanPham));
        modelView.addObject(sanPham);
        if (sanPham.getMultimedias().size() > 0) {
            modelView.addObject("multimedia01", sanPham.getMultimedias().toArray()[0]);
        }

        long status = -1;
        if (sanPham.getTinhTrangSanPham().getMaTinhTrangSanPham() == 1) { // 'dang dau gia'
            status = (sanPham.getNgayHetHan().getTime() - new java.util.Date().getTime()) / 1000;
        } else if (sanPham.getTinhTrangSanPham().getMaTinhTrangSanPham() == 2) { // 'dau gia thanh
                                                                                 // cong')
            status = -1;
        } else if (sanPham.getTinhTrangSanPham().getMaTinhTrangSanPham() == 2) { // 'het han')
            status = -2;
        }
        modelView.addObject("status", status);

        return modelView;
    }

    @RequestMapping(params = { "maSanPham", "giaDat" }, method = RequestMethod.POST)
    public @ResponseBody
    String bid(HttpSession session, String maSanPham, String giaDat) {
        TaiKhoan taiKhoan = (TaiKhoan) session.getAttribute(Utils.SESS_ACC);
        if (taiKhoan == null) {
            return "Vui lòng <b><a class='lnkLogIn' href=''>Đăng nhập lại.</a></b>";
        }
        
        SanPham sanPham = sanPhamDao.laySanPham(Integer.valueOf(maSanPham));
        if (sanPham.getTinhTrangSanPham().getMaTinhTrangSanPham() != 1) {   // dau gia ket thuc
            return "So sorry, the auction has just ended.";
        }
        
        int iGiaDat = Integer.valueOf(giaDat);
        if (sanPham.getGiaHienTai() > iGiaDat) {
            return "So sorry, a higher bid has just placed: " + sanPham.getGiaHienTai();
        }
        
        sanPham.setGiaHienTai(iGiaDat);
        sanPhamDao.capNhat(sanPham);
                
        ChiTietDauGia chiTietDauGia = new ChiTietDauGia();
        chiTietDauGia.setSanPham(sanPham);
        chiTietDauGia.setTaiKhoan(taiKhoan);
        chiTietDauGia.setGiaGiaoDich(iGiaDat);
        chiTietDauGia.setThoiGianGiaoDich(new Date());
        
        chiTietDauGiaDao.themChiTietDauGia(chiTietDauGia);

        return "Successfully.";
    }

    @RequestMapping(params = { "maMultimedia" }, method = RequestMethod.POST)
    public @ResponseBody
    String getProductDemo(HttpServletRequest request, String maMultimedia) {
        Multimedia multimedia = multimediaDao.layMultimedia(Integer.valueOf(maMultimedia));
        if (multimedia.getLoaiMultimedia().getMaLoaiMultimedia() == 1) { // multimedia is a picture
            return "<img align='middle' src='"
                    + Utils.createFullPath(request.getServletContext(), multimedia.getLinkURL())
                    + "' />";
        } else if (multimedia.getLoaiMultimedia().getMaLoaiMultimedia() == 2) { // multimedia is a
                                                                                // Youtube video
                                                                                // clip
            return "<iframe width='100%' height='300' src='http://www.youtube.com/embed/"
                    + multimedia.getLinkURL() + "' frameborder='0' allowfullscreen></iframe>";
        }

        return "";
    }
}
