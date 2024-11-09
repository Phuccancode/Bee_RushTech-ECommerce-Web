package com.project.bee_rushtech.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.dtos.LoginDTO;
import com.project.bee_rushtech.dtos.ResetPasswordDTO;
import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.LoginResponse;
import com.project.bee_rushtech.responses.ResetPasswordResponse;
import com.project.bee_rushtech.services.EmailService;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;
import java.util.Objects;

@RestController
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        private final EmailService emailService;
        private final PasswordEncoder passwordEncoder;
        @Value("${project.jwt.refresh-token-validity-in-seconds}")
        private long jwtRefreshExpiration;

        public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                        AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        EmailService emailService) {
                this.userService = userService;
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.emailService = emailService;
                this.passwordEncoder = passwordEncoder;
        }

        @PostMapping("/auth/login")
        @ApiMessage("Login successfully")
        public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDTO loginDTO) {

                // Nạp input gồm username/password vào Security

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                // create a token

                SecurityContextHolder.getContext().setAuthentication(authentication); // set authentication vào
                                                                                      // SecurityContext
                LoginResponse resLoginDTO = new LoginResponse();
                User userDB = this.userService.getUserByEmail(loginDTO.getUsername());
                LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(userDB.getId(), userDB.getEmail(),
                                userDB.getFirstName());
                resLoginDTO.setUser(userLogin);
                String access_token = this.securityUtil.createAccessToken(authentication.getName(),
                                resLoginDTO.getUser());

                resLoginDTO.setAccess_token(access_token);
                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

                // update refresh token to user
                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());
                ResponseCookie cookie = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .maxAge(jwtRefreshExpiration)
                                .path("/")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @GetMapping("/auth/account")
        @ApiMessage("Get account successfully")
        public ResponseEntity<LoginResponse.UserLogin> getAccount() throws InvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUser = this.userService.getUserByEmail(email);
                LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
                if (currentUser != null) {
                        userLogin.setId(currentUser.getId());
                        userLogin.setEmail(currentUser.getEmail());
                        userLogin.setName(currentUser.getFirstName());
                } else {
                        throw new InvalidException("Not found user have login");
                }
                return ResponseEntity.ok().body(userLogin);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get User by refresh token")
        public ResponseEntity<LoginResponse> getRefreshToken(@CookieValue(name = "refresh_token") String refreshToken)
                        throws InvalidException {
                Jwt tokenDecoded = this.securityUtil.checkValidRefreshToken(refreshToken);
                String email = tokenDecoded.getSubject();

                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
                if (currentUser == null) {
                        throw new InvalidException("Refresh token is invalid");
                }

                LoginResponse resLoginDTO = new LoginResponse();
                User userDB = this.userService.getUserByEmail(email);
                LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(userDB.getId(), userDB.getEmail(),
                                userDB.getFirstName());
                resLoginDTO.setUser(userLogin);
                String access_token = this.securityUtil.createAccessToken(email, resLoginDTO.getUser());

                resLoginDTO.setAccess_token(access_token);
                // create refresh token
                String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

                // update refresh token to user
                this.userService.updateUserToken(newRefreshToken, email);
                ResponseCookie cookie = ResponseCookie
                                .from("refresh_token", newRefreshToken)
                                .httpOnly(true)
                                .maxAge(jwtRefreshExpiration)
                                .path("/")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout successfully")
        public ResponseEntity<Void> logout() throws InvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email.isEmpty()) {
                        throw new InvalidException("User is not authenticated");
                }
                this.userService.updateUserToken("", email);

                ResponseCookie cookie = ResponseCookie
                                .from("refresh_token", "")
                                .httpOnly(true)
                                .maxAge(0)
                                .path("/")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(null);
        }

        @PostMapping("/auth/resetpassword")
        public ResponseEntity<ResetPasswordResponse> resetPassword(HttpServletRequest request,
                        @RequestParam("email") String email)
                        throws InvalidException {
                User currentUser = this.userService.getUserByEmail(email);
                if (currentUser == null) {
                        throw new InvalidException("User not found");
                }
                String token = UUID.randomUUID().toString().replace("-", "");
                ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
                resetPasswordResponse.setToken(token);
                currentUser.setPasswordResetToken(token);
                this.userService.updatePasswordResetToken(token, currentUser);
                String resetUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                                + "/customer/resetpassword?token=" + token;

                Email newEmail = new Email(email, "[BeeRushTech] Reset your password",
                                "Dear " + currentUser.getFirstName() + ",\n\n"
                                                + "We noticed that you forgot your login password and you are requesting a new password for the account associated with "
                                                + email + ".\n\n"
                                                + "Please click the link below to reset your password:\n\n" + resetUrl
                                                + "\n\n"
                                                + "Yours,\n"
                                                + "The Bee RushTech team\n\n"
                                                + "Please contact us in the following ways:\n"
                                                + "Email: " + "beerushtech@gmail.com\n"
                                                + "Tel: 0987654321\n"
                                                + "Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City.\n");
                this.emailService.sendEmail(newEmail);
                return ResponseEntity.status(HttpStatus.OK).body(resetPasswordResponse);
        }

        @PutMapping("/auth/resetpassword")
        @ApiMessage("Reset password successfully")
        public ResponseEntity<Void> resetPassword(@Valid @RequestParam("token") String token,
                        @RequestBody ResetPasswordDTO resetPasswordDTOpassword)
                        throws InvalidException {
                User currentUser = this.userService.getUserByPasswordResetToken(token);
                if (currentUser == null) {
                        throw new InvalidException("Invalid token");
                }
                if (!Objects.equals(resetPasswordDTOpassword.getNewPassword(),
                                resetPasswordDTOpassword.getConfirmPassword())) {
                        throw new InvalidException("Password and confirm password do not match");
                }
                String hashPassword = this.passwordEncoder.encode(resetPasswordDTOpassword.getNewPassword());
                currentUser.setPassword(hashPassword);
                currentUser.setPasswordResetToken(null);
                currentUser.setRefreshToken(null);
                this.userService.handleUpdateUser(currentUser);
                return ResponseEntity.status(HttpStatus.OK).body(null);
        }

}
