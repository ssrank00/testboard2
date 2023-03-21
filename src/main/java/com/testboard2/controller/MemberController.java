package com.testboard2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.testboard2.dto.MemberDTO;
import com.testboard2.service.MemberService;

@Controller
public class MemberController {
	
	
	/*
	 * DI
	 */
	@Autowired
	private MemberService memberService;
	
	
	/*
	 * 회원 등록 Form 페이지 + 회원 수정 Form 페이지
	 */
	@GetMapping("/member/memberWriteForm")
	public String memberWriteForm(
			@RequestParam( value="num", required=false) Integer num,
			Model model ) {
		// required 옵션의 dafault 값은 true (false 인 경우엔 작성해줘야함) 
		// RequestParam이 null인지 비교시 주의사항
		// Primitive type(원시타입)인 int는 null 값을 가질수 없음
		// null이 필요한 경우엔 Integer 사용.
		if(num != null) {
			System.out.println("num = "+num+" >>> 회원 정보 수정 Form 페이지 처리");
			//num 값이 null이 아닌 경우 param이 넘어온 것으로 "회원 정보 수정 Form 페이지 " 처리
			
			// Step #1) 전달받은 num값과 일치하는 회원 정보를 DB에서 가져옴
			MemberDTO m1=memberService.getMemberOne(num);
			System.out.println(m1.toString());
			
			// Step #2) DB로부터 가져온 회원정보를 Form 페이지로 전달
			model.addAttribute("memberDTO", m1); // 회원 정보 전달
			model.addAttribute("formTitle", "Modification"); // 수정 처리 화면으로 보여주기위한 title 값 전달
			
		}else {
			System.out.println("num = null >>> 회원 등록 Form 페이지 처리");
			// num 값이 null인 경우 넘어온 param이 없으므로 "회원 등록 Form 페이지" 처리
			MemberDTO m1 = new MemberDTO();
			model.addAttribute("memberDTO", m1);
			model.addAttribute("formTitle", "Registration"); // 회원 등록 화면으로 보여주기위한 title 값 전달
		
		}
		
		
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
