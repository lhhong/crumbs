package com.crumbs;

import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    EthereumBean ethereumBean() throws Exception {
        EthereumBean ethereumBean = new EthereumBean();
        Executors.newSingleThreadExecutor().
                submit(ethereumBean::start);

        return ethereumBean;
    }

    @Bean
    AccountBean accountBean() throws Exception {
        AccountBean accountBean = new AccountBean();
        Executors.newSingleThreadExecutor().
              submit(accountBean::start);
        return accountBean;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.rootUri("http://localhost:9000/").build();
    }
}
