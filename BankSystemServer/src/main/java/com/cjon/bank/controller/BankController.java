package com.cjon.bank.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cjon.bank.dto.BankDTO;
import com.cjon.bank.service.BankCheckMemberService;
import com.cjon.bank.service.BankDepositService;
import com.cjon.bank.service.BankSelectAllMemberService;
import com.cjon.bank.service.BankSelectMemberService;
import com.cjon.bank.service.BankService;
import com.cjon.bank.service.BankTransferService;
import com.cjon.bank.service.BankWithdrawService;

@Controller
public class BankController {
	
	private DataSource dataSource;
	
	@Autowired
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	private BankService service;

	// �츮�� VIEW�� JSP�� �̿����� �ʴ´�.
	//JSP�� �̿��ҰŸ� String�� Return type���� JSON�� ������ VOID�� ����Ѵ�.(������ ���ִϱ�)
	// Ŭ���̾�Ʈ�κ��� CALLBACK���� �޾ƾ���
	// ����� JSP�� �ƴ϶� Stream�� ��� Ŭ���̾�Ʈ���� JSON�� �����ؾ���
	@RequestMapping(value="/selectAllMember")
	public void selectAllMember(HttpServletRequest request,
							HttpServletResponse response,
							Model model){
		//�Է�ó��(Client)
		String callback = request.getParameter("callback");
		
		//����ó��(Service)
		service = new BankSelectAllMemberService();
		model.addAttribute("dataSource", dataSource);
		service.execute(model);
		
		//���ó��
		ArrayList<BankDTO> list = (ArrayList<BankDTO>)model.asMap().get("RESULT");
		//ArrayList<BankDTO>�� JSON���� �ٲپ Ŭ���̾�Ʈ�� ����
		String json = null;
		ObjectMapper om = new ObjectMapper();
		try {
			json = om.defaultPrettyPrintingWriter().writeValueAsString(list);
			response.setContentType("text/plain; charset=utf8");
			response.getWriter().println(callback + "("+json+")");
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="/selectMember")
	public void selectMember(HttpServletRequest request,
			HttpServletResponse response,
			Model model){
			//�Է�ó��(Client)
			String callback = request.getParameter("callback");
			
			//����ó��(Service)
			service = new BankSelectMemberService();
			model.addAttribute("dataSource", dataSource);
			model.addAttribute("request",request);
			service.execute(model);
			
			//���ó��
			BankDTO dto = (BankDTO)model.asMap().get("RESULT");
			//ArrayList<BankDTO>�� JSON���� �ٲپ Ŭ���̾�Ʈ�� ����
			String json = null;
			ObjectMapper om = new ObjectMapper();
			try {
					json = om.defaultPrettyPrintingWriter().writeValueAsString(dto);
					response.setContentType("text/plain; charset=utf8");
					response.getWriter().println(callback + "("+json+")");
			
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
	
	@RequestMapping(value="/deposit")
	public void deposit(HttpServletRequest request, HttpServletResponse response,
			Model model){
		String callback = request.getParameter("callback");
		//���� ��ü�� �����ؼ� ����ó���� �ؾ���
		service = new BankDepositService();
		model.addAttribute("dataSource",dataSource);
		model.addAttribute("request",request);
		service.execute(model);
		//���ó��
		boolean result = (Boolean) model.asMap().get("RESULT");
		response.setContentType("text/plain; charset = utf8");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(callback+"("+result+")");
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	@RequestMapping(value="/withdraw")
	public void withdraw(HttpServletRequest request, HttpServletResponse response,
			Model model){
		String callback = request.getParameter("callback");
		//���� ��ü�� �����ؼ� ����ó���� �ؾ���
		service = new BankWithdrawService();
		model.addAttribute("dataSource",dataSource);
		model.addAttribute("request",request);
		service.execute(model);
		//���ó��
		boolean result = (Boolean) model.asMap().get("RESULT");
		response.setContentType("text/plain; charset = utf8");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(callback+"("+result+")");
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
	}
	
	@RequestMapping(value="/transfer")
	public void transfer(HttpServletRequest request, HttpServletResponse response, Model model) {
		String callback = request.getParameter("callback");
		model.addAttribute("request", request);		

		//logic ó��
		service = new BankTransferService();
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("request", request);
		service.execute(model); //ó�� ����� model�� ��´�
		//��� ó��
		//model���� ������ ->���ϰ� �ϱ� ���� hashmap���·� �ٲ�
		boolean result = (Boolean) model.asMap().get("RESULT"); //Boolean�� rapper ����. boolean���� Ÿ��ĳ�����ϸ� �� ��.

		response.setContentType("text/plain; charset=utf8");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(callback + "(" + result + ")");
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/checkMember")
	public void checkMember(HttpServletRequest request, HttpServletResponse response, Model model) {
		String callback = request.getParameter("callback");
		String checkMemberId = request.getParameter("checkMemberId");

		//logic ó��
		service = new BankCheckMemberService();
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("checkMemberId", checkMemberId);
		service.execute(model); //ó�� ����� model�� ��´�
		//��� ó��
		//model���� ������ ->���ϰ� �ϱ� ���� hashmap���·� �ٲ�
		ArrayList<BankDTO> list = (ArrayList<BankDTO>) model.asMap().get("RESULT");
		
		String json = null;
		ObjectMapper om = new ObjectMapper();
		try {
			json = om.defaultPrettyPrintingWriter().writeValueAsString(list);
			response.setContentType("text/plain; charset=utf8");
			response.getWriter().println(callback + "(" + json + ")");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
	

}
