package com.crumbs;

import com.crumbs.ethereum.AccountBean;
import com.crumbs.ethereum.EthereumBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
