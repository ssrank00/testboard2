package com.testboard2.controller;

import javax.servlet.http.HttpServletRequest;

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
	@GetMapping("/member/memberWriteFormNew")
	public String memberWriteForm(@RequestParam(value = "num", required = false) Integer num, Model model) {
		// required 옵션의 dafault 값은 true (false 인 경우엔 작성해줘야함)
		// RequestParam이 null인지 비교시 주의사항
		// Primitive type(원시타입)인 int는 null 값을 가질수 없음
		// null이 필요한 경우엔 Integer 사용.
		if (num != null) {
			System.out.println("num = " + num + " >>> 회원 정보 수정 Form 페이지 처리");
			// num 값이 null이 아닌 경우 param이 넘어온 것으로 "회원 정보 수정 Form 페이지 " 처리

			// Step #1) 전달받은 num값과 일치하는 회원 정보를 DB에서 가져옴
			MemberDTO m1 = memberService.getMemberOne(num);

			// DB에서 가져온 회원정보가 없을 경우 --> m1 객체가 null 인 경우
			if (m1 == null) {
				// case #1 : 리다이렉트 (가장 간단함)
				// return "redirect:/";

				/*
				 * case #2 : PrintWriter 사용 import 필요, 추가 처리 필요(코드) HttpServletResponse
				 * response) throws Exception{ ...}
				 */

				// case #3 : Model을 사용해 특정 페이지(Error Message Page)로 데이터 값들을 보내서 출력하는 방법
				model.addAttribute("msg", "회원 정보가 없습니다. 메인 페이지로 이동합니다.");
				model.addAttribute("url", "/");

				return "/member/messageAlert"; // messageAlert.html
			}

			System.out.println(m1.toString());

			// Step #2) DB로부터 가져온 회원정보를 Form 페이지로 전달
			model.addAttribute("memberDTO", m1); // 회원 정보 전달
			model.addAttribute("formTitle", "Modification"); // 수정 처리 화면으로 보여주기위한 title 값 전달
			model.addAttribute("num", num);

		} else {
			System.out.println("num = null >>> 회원 등록 Form 페이지 처리");
			// num 값이 null인 경우 넘어온 param이 없으므로 "회원 등록 Form 페이지" 처리
			model.addAttribute("memberDTO", new MemberDTO());
			model.addAttribute("formTitle", "Registration"); // 회원 등록 화면으로 보여주기위한 title 값 전달

		}

		return "/member/memberWriteFormNew"; // (thymeleaf/)member/memberWriteFormNew.html
	}

	/*
	 * 회원 등록 OK 사용자 정보를 받아 등록하는 부분
	 */
	@PostMapping("/member/memberWriteOk")
	public String insertMember(MemberDTO m1, Model model) {

		// 사용자 등록 처리
		try {
			// 등록 처리(Controller -> Service)
			System.out.println(m1.getName());
			System.out.println(m1.getId());
			System.out.println(m1.getPhone());
			memberService.insertMember(m1);

			// 등록 안내 메시지 출력
			model.addAttribute("msg", "회원 등록이 처리되었습니다. 메인 페이지로 이동합니다.");
			model.addAttribute("url", "/");

			return "/member/messageAlert";

		} catch (Exception e) {
			// err 처리
		}

		return "redirect:/"; // 사용자 등록 후 보여줄 화면
		/*
		 * 그냥 리턴 vs redirect 리턴 1. 별 차이 없음 2. 다만 redirect의 경우 다시 한번 해당 URL로 HTTP 요청을 넣는
		 * 형태.
		 */

	}

	/*
	 * 회원 수정 OK 사용자 정보를 받아 수정하는 부분
	 */
	@PostMapping("/member/memberUpdateOk")
	public String updateMember(MemberDTO m1, HttpServletRequest request, Model model) {

		String num_ = request.getParameter("num");
		int num = Integer.parseInt(num_);
		// 사용자 등록 처리
		try {
			// 수정 처리(Controller -> Service)
			System.out.println(m1.getName());
			System.out.println(m1.getId());
			System.out.println(m1.getPhone());
			
			memberService.updateMember(m1);

			// 안내 메시지 및 URL 정보를 전달 --> messageAlert.html
			model.addAttribute("msg", "회원 정보가 수정되었습니다. 확인 페이지로 이동합니다.");
			model.addAttribute("url", "/member/memberWriteFormNew?num=" + num);

			return "/member/messageAlert"; // messageAlert.html

		} catch (Exception e) {
			// err 처리
		}

		return "redirect:/member/memberWriteFormNew?num=" + num; // 사용자 수정 후 보여줄 화면

	}

}
