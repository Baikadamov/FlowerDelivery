package kz.narxoz.Isc2.config;


import kz.narxoz.Isc2.services.impl.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


  @Autowired
  private UserServices userService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.exceptionHandling().accessDeniedPage("/403");

    http.authorizeRequests().antMatchers("/css/**","/js/**").permitAll();
    http.formLogin()
        .loginProcessingUrl("/signin").permitAll()
        .usernameParameter("user_email")
        .passwordParameter("user_password")
        .defaultSuccessUrl("/profile")
        .failureUrl("/login?error")
        .loginPage("/login");

    http.logout()
        .logoutSuccessUrl("/login")
        .logoutUrl("/signout");

    http.csrf().disable();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

}
