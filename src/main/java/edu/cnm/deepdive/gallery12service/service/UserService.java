package edu.cnm.deepdive.gallery12service.service;

import edu.cnm.deepdive.gallery12service.model.dao.UserRepository;
import edu.cnm.deepdive.gallery12service.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository repository) {
    this.userRepository = repository;
  }

    public User getOrCreate(String oauthKey, String displayName) {
    return userRepository.findFirstByOauthKey(oauthKey)
        .map((user) -> {
          user.setConnected(new Date());
          return userRepository.save(user);
        })
        .orElseGet(() -> {
          User user = new User();
          user.setOauthKey(oauthKey);
          user.setDisplayName(displayName);
          user.setConnected(new Date());
          return userRepository.save(user);
        });
  }

  public Optional<User> get(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
    Collection<SimpleGrantedAuthority> grants =
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    return new UsernamePasswordAuthenticationToken(
        getOrCreate(jwt.getSubject(), jwt.getClaim("name")), jwt.getTokenValue(),grants);
  }
}
