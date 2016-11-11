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

	// 우리는 VIEW로 JSP를 이용하지 않는다.
	//JSP를 이용할거면 String을 Return type으로 JSON을 쓰려면 VOID로 사용한다.(데이터 쏴주니까)
	// 클라이언트로부터 CALLBACK값을 받아야함
	// 출력이 JSP가 아니라 Stream을 열어서 클라이언트에게 JSON을 전송해야함
	@RequestMapping(value="/selectAllMember")
	public void selectAllMember(HttpServletRequest request,
							HttpServletResponse response,
							Model model){
		//입력처리(Client)
		String callback = request.getParameter("callback");
		
		//로직처리(Service)
		service = new BankSelectAllMemberService();
		model.addAttribute("dataSource", dataSource);
		service.execute(model);
		
		//출력처리
		ArrayList<BankDTO> list = (ArrayList<BankDTO>)model.asMap().get("RESULT");
		//ArrayList<BankDTO>를 JSON으로 바꾸어서 클라이언트에 전송
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
			//입력처리(Client)
			String callback = request.getParameter("callback");
			
			//로직처리(Service)
			service = new BankSelectMemberService();
			model.addAttribute("dataSource", dataSource);
			model.addAttribute("request",request);
			service.execute(model);
			
			//출력처리
			BankDTO dto = (BankDTO)model.asMap().get("RESULT");
			//ArrayList<BankDTO>를 JSON으로 바꾸어서 클라이언트에 전송
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
		//서비스 객체를 생성해서 로직처리를 해야함
		service = new BankDepositService();
		model.addAttribute("dataSource",dataSource);
		model.addAttribute("request",request);
		service.execute(model);
		//결과처리
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
		//서비스 객체를 생성해서 로직처리를 해야함
		service = new BankWithdrawService();
		model.addAttribute("dataSource",dataSource);
		model.addAttribute("request",request);
		service.execute(model);
		//결과처리
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

		//logic 처리
		service = new BankTransferService();
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("request", request);
		service.execute(model); //처리 결과를 model에 담는다
		//결과 처리
		//model에서 꺼내기 ->편하게 하기 위해 hashmap형태로 바꿈
		boolean result = (Boolean) model.asMap().get("RESULT"); //Boolean은 rapper 형식. boolean으로 타입캐스팅하면 안 됨.

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

		//logic 처리
		service = new BankCheckMemberService();
		model.addAttribute("dataSource", dataSource);
		model.addAttribute("checkMemberId", checkMemberId);
		service.execute(model); //처리 결과를 model에 담는다
		//결과 처리
		//model에서 꺼내기 ->편하게 하기 위해 hashmap형태로 바꿈
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
