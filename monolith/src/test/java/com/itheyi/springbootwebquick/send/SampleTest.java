package com.itheyi.springbootwebquick.send;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleTest {

    @Test
    void buildTemplateParamUsesAliyunGeneratedCodePlaceholder() throws Exception {
        Sample sample = new Sample();
        setField(sample, "validTime", 300L);

        assertEquals("{\"code\":\"##code##\",\"min\":\"5\"}", sample.buildTemplateParam());
    }

    @Test
    void buildTemplateParamRoundsUpMinutes() throws Exception {
        Sample sample = new Sample();
        setField(sample, "validTime", 301L);

        assertEquals("{\"code\":\"##code##\",\"min\":\"6\"}", sample.buildTemplateParam());
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
