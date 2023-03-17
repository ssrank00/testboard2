package com.testboard2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.testboard2.dto.MemberDTO;
import com.testboard2.service.MemberService;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@GetMapping("/member/memberWriteForm")
	public String memberWriteForm() {
		
		return "/member/memberWriteForm"; // (thymeleaf/)member/memberWriteForm.html
	}
	
	
	/*
	 * 사용자 정보를 받아 등록하는 부분
	 */
	@PostMapping("/member/memberWriteOk")
	public String registerMember(MemberDTO m1) {
		
		// 사용자 등록 처리
		try {
			// 등록 처리(Controller -> Service)
			System.out.println(m1.getName());
			System.out.println(m1.getId());
			System.out.println(m1.getPhone());
			memberService.insertMember(m1);
			
			
		}catch(Exception e) {
			// err 처리
		}
		
		return "redirect:/"; // 사용자 등록 후 보여줄 화면
		/*
		 * 그냥 리턴 vs redirect 리턴
		 * 	1. 별 차이 없음
		 * 	2. 다만 redirect의 경우 다시 한번 해당 URL로 HTTP 요청을 넣는 형태.
		 */
		
	}

}
