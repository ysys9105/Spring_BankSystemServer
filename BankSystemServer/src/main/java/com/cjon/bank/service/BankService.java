package com.cjon.bank.service;

import org.springframework.ui.Model;

public interface BankService {

	//모든 서비스 객체가 구현해야 할 메소드의 prototype을 정의
	public void execute(Model model);
	
}
