package io.feedpulse.service;

import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.model.User;
import io.feedpulse.repository.UserRepository;
import io.feedpulse.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SpringUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(SpringUserDetailsService.class);

    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public SpringUserDetailsService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return SpringUserDetails.build(user);
    }

    public UserDetails loadUserByJwtToken(String jwtToken) {
        String username = jwtUtil.extractUsername(jwtToken);
        Function<Claims, List<SimpleGrantedAuthority>> extractAuthorities = claims -> {
            List<LinkedHashMap> roles = (List<LinkedHashMap>) claims.get("roles");
            return roles.stream()
                    .map(roleMap -> new SimpleGrantedAuthority((String) roleMap.get("authority")))
                    .collect(Collectors.toList());
        };
        List<SimpleGrantedAuthority> authorities = jwtUtil.extractClaim(jwtToken, extractAuthorities);
        UUID userUuid = jwtUtil.extractUserUuid(jwtToken);
        return new SpringUserDetails(userUuid, username, "", true, false, authorities);
    }
}
