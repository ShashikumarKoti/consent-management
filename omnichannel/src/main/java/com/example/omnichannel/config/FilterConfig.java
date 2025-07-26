package com.example.omnichannel.config;

import com.example.consent.verifier.filter.RestApiCallFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {


    @Bean
    public FilterRegistrationBean<RestApiCallFilter> restApiCallFilter() {
        FilterRegistrationBean<RestApiCallFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RestApiCallFilter());
        registrationBean.addUrlPatterns("/*"); // Specify URL patterns to apply the filter
        registrationBean.setOrder(1); // Set filter order if multiple filters exist
        return registrationBean;
    }
}
