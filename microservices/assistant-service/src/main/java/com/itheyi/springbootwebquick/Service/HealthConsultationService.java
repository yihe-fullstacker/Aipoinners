package com.itheyi.springbootwebquick.Service;

import com.itheyi.springbootwebquick.mapper.HealthConsultationMapper;
import com.itheyi.springbootwebquick.pojo.HealthConsultation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//大模型能够实现业务操作
@Service
public class HealthConsultationService {
    //注入mapper，能够对数据库进行增删改查
    @Autowired
    private HealthConsultationMapper healthConsultationMapper;

    public void insert(HealthConsultation consultation) {
        healthConsultationMapper.insert(consultation);
    }

    public HealthConsultation findByPhone(String phone) {
        return healthConsultationMapper.findByPhone(phone);
    }
}
