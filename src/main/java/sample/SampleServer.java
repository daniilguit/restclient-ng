package sample;

import org.springframework.boot.SpringApplication;
public class SampleServer {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SampleServiceApiImpl.class, args);
    }
}
