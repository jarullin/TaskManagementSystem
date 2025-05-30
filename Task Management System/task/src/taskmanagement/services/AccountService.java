package taskmanagement.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskmanagement.entities.Account;
import taskmanagement.repositories.AccountRepository;

import java.util.List;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createAccount(String email, String password) {
        String emailPattern = "^(.+)@(\\S+)$";
        if (email == null
                || password == null
                || password.length() < 6
                || !email.matches(emailPattern)
                || accountRepository.existsByEmail(email)) {
            System.out.println("Account creation failed, email="+email+", password="+password);
            return false;
        }
        accountRepository.save(new Account(email, passwordEncoder.encode(password)));
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.getByEmail(email);
        System.out.println(account.getEmail()+" "+account.getPassword());
        return new User(account.getEmail(),account.getPassword(), List.of());
    }

    public boolean existsByEmail(String assignee) {
        return accountRepository.existsByEmail(assignee);
    }
}
