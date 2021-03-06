package ca.mcgill.ecse321.rideshare9.security;
import ca.mcgill.ecse321.rideshare9.jwt.JWTAuthenticationFilter;
import ca.mcgill.ecse321.rideshare9.jwt.JWTLoginFilter;
import ca.mcgill.ecse321.rideshare9.service.UserService;
import ca.mcgill.ecse321.rideshare9.service.impl.CustomAuthenticationProvider;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * JWTLoginFilter + JWTAuthenticationFilter
 */

/**
 * DO NOT EDIT IT ON YOUR OWN!!!
 * ATTENTION: DON'T EDIT ANY CLASS WHOSE NAME HAS "User" or "Security" or "service" or related! Otherwise, no one can log in this system anymore! 
 * if you have suggestions, please contact me in group chat! 
 * @author yuxiangma
 */


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityConfig(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/user/sign-up", "/user/get-is-unique", "/user/mainpg", "/adv/active-advertisements", "/user/active-drivers", "/user/active-passengers",
                			"/adv/get-top-drivers", "/map/get-top-passengers", "/adv/get-top-adv").permitAll()
                //.antMatchers(HttpMethod.GET, "/adv/active-advertisements").permitAll()
                //.antMatchers(HttpMethod.POST, "/adv/passenger/get-all-adv").permitAll() uncomment this and remove advcontroller @PreAuth to enable anyone access advertisement search
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTLoginFilter(authenticationManager()))
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), userService))
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        String[] origins = {"http://localhost:8087", "https://ride-sharer2.herokuapp.com", "https://ride-sharer.herokuapp.com"};
        configuration.setAllowedOrigins(Arrays.asList(origins));
        String[] methods = {"HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"};
        configuration.setAllowedMethods(Arrays.asList(methods));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        String[] headers = {"*"};
        configuration.setAllowedHeaders(Arrays.asList(headers));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider(bCryptPasswordEncoder, userService));
    }
}
