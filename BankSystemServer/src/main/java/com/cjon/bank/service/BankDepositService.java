package com.cjon.bank.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.ui.Model;

import com.cjon.bank.dao.BankDAO;

public class BankDepositService implements BankService {

	@Override
	public void execute(Model model) {
		// TODO Auto-generated method stub
		
		HttpServletRequest request = (HttpServletRequest) model.asMap().get("request");
		String memberID = request.getParameter("memberId");
		String memberBalance = request.getParameter("memberBalance");
		DataSource dataSource = (DataSource) model.asMap().get("dataSource");
		Connection con = null;
		String state = "DEPOSIT";
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			
			BankDAO dao = new BankDAO(con);
			boolean result = dao.updateDeposit(memberID,memberBalance);
			
			if(result){
				dao.updateState(memberID, memberBalance, state);
				con.commit();
			}
			else {
				con.rollback();
			}
			model.addAttribute("RESULT",result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

}
