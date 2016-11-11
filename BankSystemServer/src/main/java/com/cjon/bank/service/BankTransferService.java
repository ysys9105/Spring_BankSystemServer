package com.cjon.bank.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.ui.Model;

import com.cjon.bank.dao.BankDAO;

public class BankTransferService implements BankService {

	@Override
	public void execute(Model model) {
		
		DataSource dataSource = (DataSource) model.asMap().get("dataSource");
		HttpServletRequest request = (HttpServletRequest) model.asMap().get("request");
		String sendMemberId = request.getParameter("sendMemberId");
		String receiveMemberId = request.getParameter("receiveMemberBalance");
		String transferBalance = request.getParameter("transferBalance");
		Connection con = null;
		boolean result = false;
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false); //트랜잭션 시작
			
			BankDAO dao = new BankDAO(con);
			boolean withrawResult = dao.updateWithdraw(sendMemberId, transferBalance);
			boolean depositResult = dao.updateDeposit(receiveMemberId, transferBalance);
			boolean historyResult = dao.updateHistory(sendMemberId, receiveMemberId, transferBalance);
			if (withrawResult && depositResult && historyResult) {
				result = true;
				con.commit();
			} else {
				result = false;
				con.rollback();
			}
			model.addAttribute("RESULT", result);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}

	}


}
