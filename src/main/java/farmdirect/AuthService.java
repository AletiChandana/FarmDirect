
package farmdirect;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(
            String name,
            String email,
            String rawPassword,
            String role) {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || rawPassword == null
                || rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Enter your name, email, and a password of at least 8 characters.");
        }

        if (userRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new IllegalArgumentException(
                    "This email is already registered.");
        }

        if (role == null
                || !(role.equalsIgnoreCase("FARMER")
                || role.equalsIgnoreCase("CUSTOMER"))) {
            throw new IllegalArgumentException(
                    "Role must be FARMER or CUSTOMER.");
        }

        AppUser user = new AppUser();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role.toUpperCase());

        return userRepository.save(user);
    }
}

