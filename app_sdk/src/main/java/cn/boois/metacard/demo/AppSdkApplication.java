package cn.boois.metacard.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.metacard.adapter.SDKConfig;

@SpringBootApplication
public class AppSdkApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AppSdkApplication.class, args);
		SDKConfig.AppId = ""; // 为了安全可以从环境变量中获取
		SDKConfig.SecretToken = "";// 为了安全可以从环境变量中获取
	}

}
