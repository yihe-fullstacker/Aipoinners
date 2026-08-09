package com.itheyi.springbootwebquick.mapper;

import com.itheyi.springbootwebquick.pojo.HealthConsultation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HealthConsultationMapper {

    @Insert("INSERT INTO health_consultation (patient_name, patient_gender, patient_phone, communication_time, province, main_symptoms, created_at) " +
            "VALUES (#{patientName}, #{patientGender}, #{patientPhone}, #{communicationTime}, #{province}, #{mainSymptoms}, NOW())")
    void insert(HealthConsultation consultation);

    @Select("SELECT * FROM health_consultation WHERE patient_phone = #{phone} ORDER BY created_at DESC LIMIT 1")
    HealthConsultation findByPhone(String phone);
}
