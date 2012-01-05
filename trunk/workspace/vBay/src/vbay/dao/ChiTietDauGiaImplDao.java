package vbay.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vbay.model.SanPham;

@Repository("chiTietDauGiaDao")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ChiTietDauGiaImplDao implements ChiTietDauGiaDao{
	
	@Autowired 
	SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<SanPham> hotAuctions(){
		List<SanPham> dsSanPham = null;
		Session session = null;
		
		try{
			session = sessionFactory.getCurrentSession();
			String hql = "select ct.sanPham from ChiTietDauGia ct where ct.sanPham.tinhTrangSanPham.maTinhTrangSanPham=1";
			hql += " group by ct.sanPham.maSanPham order by count(ct.sanPham) desc";
			Query query = session.createQuery(hql);
			dsSanPham = query.setMaxResults(10).list();
		}catch (Exception ex){
			ex.printStackTrace();
			dsSanPham = null;
		}
		return dsSanPham;
	}
}