package io.athos.agrocore.plantmonitor.security;
import io.athos.agrocore.plantmonitor.security.dtos.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/auth/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("register/")
    public ResponseEntity<AuthRegisterResponse> registerUser(@Valid @RequestBody RegisterAuthRequest request){
        return ResponseEntity.ok(new AuthRegisterResponse(authService.registerUser(request)));
    }

    @PostMapping("login/")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginAuthRequest request){
        System.out.println(request);
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("refresh/")
    public ResponseEntity<AuthTokenResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }


    @PostMapping("/logout/")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/me/")
    public ResponseEntity<AuthResponse> me(@AuthenticationPrincipal SecurityUser authenticatedUser) {
        return ResponseEntity.ok(new AuthResponse(authenticatedUser.getPersistentUser()));
    }

    @PatchMapping("/me/")
    public ResponseEntity<AuthRegisterResponse> updateAuthenticatedUser(@AuthenticationPrincipal SecurityUser authenticatedUser, @RequestBody @Valid AuthUpdateRequest request) {

        return ResponseEntity.ok(new AuthRegisterResponse(authService.updateUser(authenticatedUser, request)));
    }

//
//    @GetMapping("/confirm-email")
//    public ResponseEntity<Void> confirmEmail(@RequestParam("token") String token) {
//        try {
//            authService.confirmEmail(token);
//
//            // Sucesso → vai para página de sucesso
//            URI redirect = URI.create(APP_URL + "/auth/activation-success");
//            return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
//
//        } catch (InvalidConfirmationToken e) {
//            URI redirect = URI.create(APP_URL + "/auth/activation-error?reason=invalid");
//            return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
//        }
//    }
//
//    @GetMapping("/resend-email/")
//    public ResponseEntity<Void> resendConfirmationEmail(@Param("email") String email) {
//        authService.resendEmail(email);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/forgot-password/")
//    public ResponseEntity<Void> changePassword(@RequestBody AuthForgotPassword request) {
//        authService.sendEmailToResetPassword(request);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/reset-password/")
//    public ResponseEntity<Void> resetPassword(@RequestBody AuthResetPasswordRequest request) {
//        authService.resetPassword(request);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/confirm-delete-account")
//    public ResponseEntity<Void> confirmAccountDeletion(@Param("token") String token) throws UserHasOwnerException {
//        try {
//            authService.confirmAccountDeletion(token);
//            URI redirect = URI.create(APP_URL+"/auth/delete-account-success");
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .location(redirect)
//                    .build();
//
//        } catch (InvalidConfirmationToken e) {
//            URI redirect = URI.create(APP_URL + "/auth/delete-account-error?reason=invalid");
//            return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
//        }
//    }



}
