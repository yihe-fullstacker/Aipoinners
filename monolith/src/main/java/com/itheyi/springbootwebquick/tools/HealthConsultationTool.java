package com.itheyi.springbootwebquick.tools;

import com.itheyi.springbootwebquick.pojo.HealthConsultation;
import com.itheyi.springbootwebquick.Service.HealthConsultationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HealthConsultationTool {
    /*
    * AI 大模型 → HealthConsultationTool → HealthConsultationService → 数据库
    * 将前端上传的数据存储到数据库中
    * */
    @Autowired
    private HealthConsultationService healthConsultationService;

    @Tool("预约一对一健康咨询服务")
    public void addHealthConsultation(
            @P("患者姓名") String patientName,
            @P("患者性别") String patientGender,
            @P("患者电话") String patientPhone,
            @P("预约沟通时间，格式为: yyyy-MM-dd'T'HH:mm") String communicationTime,
            @P("患者所在省份") String province,
            @P("主要症状描述") String mainSymptoms
    ) {
        HealthConsultation consultation = new HealthConsultation(
                null,
                patientName,
                patientGender,
                patientPhone,
                LocalDateTime.parse(communicationTime),
                province,
                mainSymptoms,
                null
        );
        healthConsultationService.insert(consultation);
    }
/*
* AI Service 层 + Config 层实现大模型调用
Tool 层让大模型操作后端业务代码
* */
    @Tool("根据患者手机号查询预约详情")
    public HealthConsultation findHealthConsultation(@P("患者手机号") String patientPhone) {
        return healthConsultationService.findByPhone(patientPhone);
    }
}
