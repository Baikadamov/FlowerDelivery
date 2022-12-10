package kz.narxoz.Isc2.services;

import kz.narxoz.Isc2.models.Auth.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  Users getUserByEmail(String email);

  Users createUser(Users users);

}