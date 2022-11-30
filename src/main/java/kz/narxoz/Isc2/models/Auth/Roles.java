package kz.narxoz.Isc2.models.Auth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_roles")

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Roles implements GrantedAuthority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id ;

  @Column(name = "role")
  private String role;

  @Override
  public String getAuthority() {
    return role;
  }
}
