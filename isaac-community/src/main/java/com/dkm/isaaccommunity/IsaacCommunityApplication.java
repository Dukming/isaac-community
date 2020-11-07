package com.dkm.isaaccommunity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.dkm.isaaccommunity.dao")
@SpringBootApplication
public class IsaacCommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsaacCommunityApplication.class, args);
    }

}
