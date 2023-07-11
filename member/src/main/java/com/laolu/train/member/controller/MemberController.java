package com.laolu.train.member.controller;

import com.laolu.train.common.resp.CommonResp;
import com.laolu.train.member.req.MemberLoginReq;
import com.laolu.train.member.req.MemberRegisterReq;
import com.laolu.train.member.req.MemberSendCodeReq;
import com.laolu.train.member.resp.MemberLoginResp;
import com.laolu.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Integer> count(){
        int count = memberService.count();
        return new CommonResp<>(count);
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req){
        long id = memberService.register(req);
        return new CommonResp<>(id);
    }

    @PostMapping("/send-code")
    public CommonResp<Long> register(@Valid @RequestBody MemberSendCodeReq req){
        memberService.sendCode(req);
        return new CommonResp<>();
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> register(@Valid MemberLoginReq req){
        MemberLoginResp resp = memberService.login(req);
        return new CommonResp<>(resp);
    }
}
