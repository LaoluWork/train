package com.laolu.train.business.feign;

import com.laolu.train.common.req.MemberTicketReq;
import com.laolu.train.common.resp.CommonResp;
import org.springframework.stereotype.Component;

@Component
public class MemberFeignFallback implements MemberFeign {

    @Override
    public CommonResp<Object> save(MemberTicketReq req) {
        return null;
    }
}
