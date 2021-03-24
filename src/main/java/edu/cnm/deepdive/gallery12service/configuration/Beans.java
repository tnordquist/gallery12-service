package edu.cnm.deepdive.gallery12service.configuration;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

  @Bean
  public Random random() {
    return new SecureRandom();
  }

  @Bean
  public ApplicationHome applicationHome() {
    return new ApplicationHome(getClass());
  }

}