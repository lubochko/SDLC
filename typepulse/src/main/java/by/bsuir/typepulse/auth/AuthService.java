package by.bsuir.typepulse.auth;

import by.bsuir.typepulse.domain.Role;
import by.bsuir.typepulse.domain.UserAccount;
import by.bsuir.typepulse.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount register(String login, String email, String rawPassword) {
        if (userAccountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }
        if (userAccountRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Логин уже занят");
        }
        UserAccount user = new UserAccount();
        user.setLogin(login.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(Role.USER);
        return userAccountRepository.save(user);
    }
}
