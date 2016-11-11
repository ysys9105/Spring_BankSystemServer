package com.cjon.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cjon.bank.dto.BankDTO;

public class BankDAO {

	private Connection con; 
	
	public BankDAO(Connection con) {
		this.con = con;
	}

	public ArrayList<BankDTO> selectAllMember(){

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<BankDTO> list = new ArrayList<BankDTO>();
		try{

			String sql = "select MEMBER_ID, MEMBER_NAME, MEMBER_ACCOUNT, MEMBER_BALANCE"
					+ " from bank_member_tb";
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			while(rs.next()){
				BankDTO dto = new BankDTO();
				dto.setMemberId(rs.getString("member_id"));
				dto.setMemberName(rs.getString("member_name"));
				dto.setMemberAccount(rs.getString("member_account"));
				dto.setMemberBalance(rs.getInt("member_balance"));
				list.add(dto);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try {
				rs.close();
				pstmt.close();
				
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return list;
	}
	
	public BankDTO selectMember(String memberID) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		BankDTO dto = new BankDTO();
		try{

			String sql = "select MEMBER_ID, MEMBER_NAME, MEMBER_ACCOUNT, MEMBER_BALANCE"
					+ " from bank_member_tb where MEMBER_ID=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberID);
			
			rs = pstmt.executeQuery();
			while(rs.next()){
				
				dto.setMemberId(rs.getString("member_id"));
				dto.setMemberName(rs.getString("member_name"));
				dto.setMemberAccount(rs.getString("member_account"));
				dto.setMemberBalance(rs.getInt("member_balance"));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try {
				rs.close();
				pstmt.close();
				
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return dto;
	
	}

	public boolean updateDeposit(String memberID, String memberBalance) {
		PreparedStatement pstmt = null;
		boolean result = false;
		String sql = "update bank_member_tb set member_balance=member_balance+? where member_id=?";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(memberBalance));
			pstmt.setString(2, memberID);

		
			int count = pstmt.executeUpdate();
			if(count==1){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return result;
	}

	public boolean updateWithdraw(String memberID, String memberBalance) {
		PreparedStatement pstmt = null;
		boolean result = false;
		BankDTO dto = new BankDTO();
		String sql = "update bank_member_tb set member_balance=member_balance-? where member_id=?";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(memberBalance));
			pstmt.setString(2, memberID);
			
			int count = pstmt.executeUpdate();
			if(count==1){
				String sql1 ="select member_id, member_balance from bank_member_tb where member_id=?";
				 PreparedStatement pstmt1=con.prepareStatement(sql1);
				    pstmt1.setString(1,  memberID);
				    ResultSet rs=pstmt1.executeQuery();
				    if(rs.next()){ 
				     dto.setMemberBalance(rs.getInt("member_balance"));
				    }
				    if(dto.getMemberBalance()<0){
				     System.out.println("잔액 부족으로 인한 출금 실패");
				     con.rollback();
				     result = false;
				    }else{
				     con.commit();  
				     result = true;

				    }
				    pstmt1.close();
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return result;
	}

	public boolean updateState(String memberID, String memberBalance, String state) {
		PreparedStatement pstmt = null;
		boolean result = false;
		String sql = "insert into bank_statement_tb (member_id, money, kind) values(?, ? ,?)";
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberID);
			pstmt.setInt(2, Integer.parseInt(memberBalance));
			pstmt.setString(3, state);

			
			int count = pstmt.executeUpdate();
			if(count==1){
				result = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				pstmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return result;
	}
	
	public boolean checkBalance(String memberId, String memberBalance) {
		PreparedStatement pstmt = null;
		boolean result = false;
		
		try {

			String sql = "update bank_member_tb set member_balance = member_balance - ? where member_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(memberBalance));
			pstmt.setString(2, memberId);
			
			int count = pstmt.executeUpdate();
			
			if (count == 1) {
				result = true;
			} else {
				result = false;
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				pstmt.close(); //트랜잭션 커밋/롤백한 뒤에 커넥션을 클로즈해야하므로 여기서는 con.close() 하면 안 됨.
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean updateHistory(String sendMemberId, String receiveMemberId, String transferBalance) {
		PreparedStatement pstmt = null;
		boolean result = false;
		
		try {

			String sql = "insert into bank_transfer_history_tb (transfer_money, send_member_id, receive_member_id)"
					+ " values (?, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(transferBalance));
			pstmt.setString(2, sendMemberId);
			pstmt.setString(3, receiveMemberId);
			
			int count = pstmt.executeUpdate();
			
			if (count == 1) {
				result = true;
			} else {
				result = false;
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				pstmt.close(); //트랜잭션 커밋/롤백한 뒤에 커넥션을 클로즈해야하므로 여기서는 con.close() 하면 안 됨.
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	public ArrayList<BankDTO> checkMemberId(String checkMemberId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<BankDTO> list = new ArrayList<BankDTO>();
		System.out.println("  .."+checkMemberId);
		
		try {

			String sql = "select s.money, s.member_id, s.kind, m.member_balance from bank_statement_tb s join bank_member_tb m on s.member_id = m.member_id where s.member_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, checkMemberId);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				BankDTO dto = new BankDTO();
				dto.setMemberId(rs.getString("s.member_id"));
				dto.setMemberName(rs.getString("s.kind"));
				dto.setMemberBalance(rs.getInt("s.money"));
				dto.setMemberAccount(rs.getString("m.member_balance"));
				list.add(dto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}
}
