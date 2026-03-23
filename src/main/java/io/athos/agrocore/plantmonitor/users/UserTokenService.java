package io.athos.agrocore.plantmonitor.users;


import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class UserTokenService {
    @Autowired
    private  UserTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();



    public String generateToken(User user) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        String hash = hashSha256(token);
        UserToken userToken = new UserToken();
//        userToken.setUser(user);
        userToken.setToken(hash);
        repository.save(userToken);
        return token;
    }

//    public boolean validateAndConsomeToken(String token, UserToken.UserTokenType type) {
//        String hash = hashSha256(token);
//        return repository.findByTokenAndExpiresAtAfter(hash, LocalDateTime.now())
//                .filter(t -> t.getType() == type)
//                .map(t -> {
//                    repository.delete(t); // consome token
//                    return true;
//                })
//                .orElse(false);
//    }

    public boolean validateToken(String token) {
        String hash = hashSha256(token);
        return repository.findByTokenAndExpiresAtAfter(hash, LocalDateTime.now()).isPresent();
    }
    public UserToken getUserToken(String token) {
        String hash = hashSha256(token);
        return repository.findByTokenAndExpiresAtAfter(hash, LocalDateTime.now()).orElseThrow(() -> new NotFoundException(UserToken.class, "token", null ));
    }
    // Executa todos os dias ao meio-dia (12:00)
//    @Transactional
//    @Scheduled(cron = "0 0 12 * * *")
//    public void cleanupExpiredTokens() {
//        LocalDateTime now = LocalDateTime.now();
//        int removed = repository.deleteAllByExpiresAtBefore(now);
//        System.out.println("Tokens expirados removidos: " + removed);
//    }

    private String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes());
            return HexFormat.of().formatHex(encoded);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash", e);
        }
    }

    public void consomeToken(UserToken userToken) {
        repository.delete(userToken);
    }
}
