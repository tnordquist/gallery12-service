package edu.cnm.deepdive.gallery12service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class Gallery12ServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(Gallery12ServiceApplication.class, args);
  }

}
