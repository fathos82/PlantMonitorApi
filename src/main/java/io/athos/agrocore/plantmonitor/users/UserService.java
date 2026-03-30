package io.athos.agrocore.plantmonitor.users;

import io.athos.agrocore.plantmonitor.errors.NotFoundException;
import io.athos.agrocore.plantmonitor.security.dtos.AuthUpdateRequest;
import io.athos.agrocore.plantmonitor.security.dtos.RegisterAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.athos.agrocore.plantmonitor.ObjectHelperUtils.setIfNotNull;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public User createUser(RegisterAuthRequest request, String password) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(password);

        user.setPhone(request.phone());
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    public User updateUser(User persistentUser, AuthUpdateRequest request) {
        setIfNotNull(request.name(), persistentUser::setName);
        setIfNotNull(request.phone(), persistentUser::setPhone);
//        setIfNotNull(request.birthday(), persistentUser::setBirthday);
//        setIfNotNull(request.imageProfileUrl(), persistentUser::setImageProfileUrl);

        return userRepository.save(persistentUser);
    }
}