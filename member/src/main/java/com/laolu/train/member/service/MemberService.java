package com.laolu.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.laolu.train.common.exception.BusinessException;
import com.laolu.train.common.exception.BusinessExceptionEnum;
import com.laolu.train.member.domain.Member;
import com.laolu.train.member.domain.MemberExample;
import com.laolu.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import req.MemberRegisterReq;

import java.util.List;

@Service
public class MemberService {


    @Resource
    private MemberMapper memberMapper;

    public int count(){
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req){
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(list)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();

    }
}
