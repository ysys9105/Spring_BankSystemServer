package com.cjon.bank.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.ui.Model;

import com.cjon.bank.dao.BankDAO;
import com.cjon.bank.dto.BankDTO;

public class BankSelectMemberService implements BankService {

	@Override
	public void execute(Model model) {
		// TODO Auto-generated method stub
		//모든 사람에 대한 정보를 가져오는 로직을 수행 
				HttpServletRequest request = (HttpServletRequest) model.asMap().get("request");
				String memberID = request.getParameter("memberId");
				DataSource dataSource = (DataSource) model.asMap().get("dataSource");
				Connection con=null;
				try {
					con = dataSource.getConnection();
					con.setAutoCommit(false); //transaction
					BankDAO dao = new BankDAO(con);
					BankDTO dto = new BankDTO();
					dto = dao.selectMember(memberID);
					if (dao != null) {
						con.commit();
					} else{
						con.rollback();
					}
					
					model.addAttribute("RESULT", dto);
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

}
