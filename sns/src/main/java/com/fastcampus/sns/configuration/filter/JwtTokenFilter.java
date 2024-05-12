package com.fastcampus.sns.configuration.filter;


import com.fastcampus.sns.model.dto.User;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final UserService userService;

	private final String secretKey;


	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain)
		throws ServletException, IOException {
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String token;
		try {
			if (header == null || !header.startsWith("Bearer ")) {
				log.error("Authorization Header does not start with Bearer {}", request.getRequestURI());
				chain.doFilter(request, response);
				return;
			} else {
				token = header.split(" ")[1].trim();
			}

			String userName = JwtTokenUtils.getUsername(token, secretKey);
			User userDetails = userService.loadUserByUsername(userName);

			if (!JwtTokenUtils.validate(token, userDetails.getUsername(), secretKey)) {
				chain.doFilter(request, response);
				return;
			}
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails, null,
				userDetails.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (RuntimeException e) {
			chain.doFilter(request, response);
			return;
		}

		chain.doFilter(request, response);

	}
}