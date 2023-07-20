package com.laolu.train.member.controller;

import com.laolu.train.common.context.LoginMemberContext;
import com.laolu.train.common.resp.CommonResp;
import com.laolu.train.common.resp.PageResp;
import com.laolu.train.member.req.PassengerQueryReq;
import com.laolu.train.member.req.PassengerSaveReq;
import com.laolu.train.member.resp.PassengerQueryResp;
import com.laolu.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody PassengerSaveReq req){
        passengerService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryReq req){
        // 因为如果是会员自己查询的话，不需要从前端传id过来
        req.setMemberId(LoginMemberContext.getId());
        PageResp<PassengerQueryResp> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id){
        passengerService.delete(id);
        return new CommonResp<>();
    }

}
