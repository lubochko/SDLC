package by.bsuir.typepulse.auth;

import by.bsuir.typepulse.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TypePulseUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public TypePulseUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByLogin(username)
                .or(() -> userAccountRepository.findByEmail(username.toLowerCase()))
                .map(TypePulseUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
