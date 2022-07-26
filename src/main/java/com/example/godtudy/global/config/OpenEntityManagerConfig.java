package com.example.godtudy.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class OpenEntityManagerConfig {

    @Bean
    public FilterRegistrationBean<OpenEntityManagerInViewFilter> openEntityManagerInViewFilter() {
        FilterRegistrationBean<OpenEntityManagerInViewFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterFilterRegistrationBean.setOrder(Integer.MIN_VALUE); // 예시를 위해 최우선 순위로 Filter 등록
        return filterFilterRegistrationBean;
    }

}

//    https://tecoble.techcourse.co.kr/post/2020-08-31-entity-lifecycle-1/
//    https://tecoble.techcourse.co.kr/post/2020-09-20-entity-lifecycle-2/
