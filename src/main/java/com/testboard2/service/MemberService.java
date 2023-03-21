package com.testboard2.service;

import com.testboard2.dto.MemberDTO;

public interface MemberService {
	
	public void insertMember(MemberDTO memberDTO);
	public MemberDTO getMemberOne(int num);
		
}
