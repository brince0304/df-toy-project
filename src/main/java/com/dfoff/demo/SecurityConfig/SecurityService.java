package com.dfoff.demo.SecurityConfig;

import com.dfoff.demo.Domain.EnumType.SecurityRole;
import com.dfoff.demo.Domain.UserAccount;
import com.dfoff.demo.Repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> _account = userAccountRepository.findById(username);
        if (_account.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        UserAccount account = _account.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (SecurityRole role : account.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getValue()));
        }
        return UserAccount.PrincipalDto.builder()
                .username(account.getUserId())
                .password(account.getPassword())
                .nickname(account.getNickname())
                .email(account.getEmail())
                .authorities(authorities)
                .build();
    }
}