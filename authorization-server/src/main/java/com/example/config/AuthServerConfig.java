package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

	private BCryptPasswordEncoder passwordEncoder;

	private JwtAccessTokenConverter tokenConvereter;

	private UserDetailsService userService;

	public AuthServerConfig(BCryptPasswordEncoder passwordEncoder, JwtAccessTokenConverter tokenConvereter,
			UserDetailsService userService) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.tokenConvereter = tokenConvereter;
		this.userService = userService;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer authEndpoints) throws Exception {
		authEndpoints.tokenStore(new JwtTokenStore(tokenConvereter)).accessTokenConverter(tokenConvereter)
				.userDetailsService(userService);
		super.configure(authEndpoints);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
					.withClient("stems_app")
					.secret(passwordEncoder.encode("stems_seceret"))
					.authorizedGrantTypes("authorization_code", "refresh_token")
					.scopes("read", "write")
					.redirectUris("http://localhost:5000/stems/login")
					.accessTokenValiditySeconds(2400).and()
					.withClient("ui")
					.authorizedGrantTypes("implicit")
					.scopes("read")
					.autoApprove(true)
					.redirectUris("http://localhost:5000/stems/home")
					.and()
					.withClient("stems_resource")
					.secret(passwordEncoder.encode("stems_seceret"))
					.authorizedGrantTypes("client_credentials", "refresh_token");
		super.configure(clients);
	}

	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()")
			.passwordEncoder(passwordEncoder);
	}

}