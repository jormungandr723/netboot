package com.dcis.netboot;

import com.dcis.netboot.nettyconfig.Httpserver;
import com.dcis.netboot.nettyconfig.Nettyconfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class NetbootApplication implements CommandLineRunner {
    @Resource
    Nettyconfig nettyconfig;
    public static void main(String[] args) {
        SpringApplication.run(NetbootApplication.class, args);
    }
    @Override
    public void run(String... strings) throws InterruptedException, IllegalAccessException, InstantiationException {
        Httpserver.start(nettyconfig);
    }
}
