package com.company.project.configurer;

import com.company.project.filter.InputStreamWrapperFilter;
import com.company.project.filter.JwtAuthenticationFilter;
import com.company.project.filter.JwtLoginFilter;
import com.company.project.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Spring Security的安全配置
 */
@Component
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(WebSecurity web) {
        // Overridden to exclude some url's
        web.ignoring().antMatchers("/user/register"); //register不用经过filterchain
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new MyAccessDenied();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new MyAuthenticationFailEntryPointImpl();
    }


    /**
     * 在这里设置UserDetailsService
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //先简单设置密码明文存储
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        });
    }

    /**
     * 在这里设置哪些借口分别可以被哪些用户访问
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //设置请求被spring security拦截时的自定义信息
        http.authorizeRequests()
                .antMatchers("/user/**").permitAll()
                .and()
                .addFilterBefore(new InputStreamWrapperFilter(),JwtLoginFilter.class)
                .addFilter(new JwtLoginFilter(authenticationManager())).csrf().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager())).csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).accessDeniedHandler(accessDeniedHandler());

        //设置关闭服务的url只有在内网才能访问
        http.authorizeRequests()
                .antMatchers("/actuator/shutdown").hasIpAddress("127.0.0.1");
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

}
