package hello.hellospring.controller;

import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {

    private final MemberService memberService;

    // @Autowired private MemberService memberService; -> 필드 주입
    //생성자를 통해 주입 권장 / 생성 시점에 주입하고 건들지 않음
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
