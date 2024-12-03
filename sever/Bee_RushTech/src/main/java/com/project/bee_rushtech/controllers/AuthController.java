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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.dtos.LoginDTO;
import com.project.bee_rushtech.dtos.ResetPasswordDTO;
import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.LoginResponse;
import com.project.bee_rushtech.responses.ResetPasswordResponse;
import com.project.bee_rushtech.responses.UserResponse;
import com.project.bee_rushtech.services.EmailService;
import com.project.bee_rushtech.services.GoogleService;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        private final EmailService emailService;
        private final PasswordEncoder passwordEncoder;
        private final OAuth2AuthorizedClientService authorizedClientService;
        private final GoogleService googleUserInfoService;

        @Value("${project.jwt.refresh-token-validity-in-seconds}")
        private long jwtRefreshExpiration;

        public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                        AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        EmailService emailService, OAuth2AuthorizedClientService authorizedClientService,
                        GoogleService googleUserInfoService) {
                this.userService = userService;
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.emailService = emailService;
                this.passwordEncoder = passwordEncoder;
                this.authorizedClientService = authorizedClientService;
                this.googleUserInfoService = googleUserInfoService;

        }

        @PostMapping("/register")
        @ApiMessage("Register successfully")
        public ResponseEntity<UserResponse> register(@Valid @RequestBody User user) throws InvalidException {
                if (this.userService.checkUserExists(user.getEmail())) {
                        throw new InvalidException("Email is already taken");
                }
                String hashPassword = this.passwordEncoder.encode(user.getPassword());
                user.setPassword(hashPassword);
                if (user.getEmail().contains(".edu.vn")) {
                        user.setRole("STUDENT");
                } else
                        user.setRole("CUSTOMER");
                User newUser = this.userService.handleCreateUser(user);

                UserResponse userResponse = new UserResponse();
                userResponse.setId(newUser.getId());
                userResponse.setFullName(newUser.getFullName());
                userResponse.setEmail(newUser.getEmail());
                userResponse.setPhoneNumber(newUser.getPhoneNumber());
                userResponse.setAddress(newUser.getAddress());
                userResponse.setRole(newUser.getRole());
                return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        }

        @PostMapping("/login")
        @ApiMessage("Login successfully")
        public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDTO loginDTO,
                        HttpServletResponse response) {

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
                                userDB.getFullName(), userDB.getRole());
                resLoginDTO.setUser(userLogin);
                String access_token = this.securityUtil.createAccessToken(loginDTO.getUsername(), userLogin);
                resLoginDTO.setAccessToken(access_token);
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
                ResponseCookie cookie = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .maxAge(jwtRefreshExpiration)
                                .path("/")
                                .build();

                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

                return ResponseEntity.ok()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token)
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

        @PostMapping("/logout")
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

        @PostMapping("/reset-password")
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
                                "Dear " + currentUser.getFullName() + ",\n\n"
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

        @PutMapping("/reset-password")
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

        @GetMapping("/login-with-google")
        @ApiMessage("Login successfully")
        public ResponseEntity<LoginResponse> getAccessToken(OAuth2AuthenticationToken authentication,
                        HttpServletResponse response) {
                String registrationId = authentication.getAuthorizedClientRegistrationId();

                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                                registrationId, authentication.getName());

                String accessToken = client.getAccessToken().getTokenValue();

                Map userInfoResponse = googleUserInfoService.getUserInfo(accessToken);
                String email = (String) userInfoResponse.get("email");
                String name = (String) userInfoResponse.get("name");
                if (this.userService.checkUserExists(email) == false) {
                        User user = new User();
                        user.setEmail(email);
                        user.setFullName(name);
                        user.setRole("GOOGLE");
                        user.setPassword("LOGIN_WITH_GOOGLE");
                        user.setPhoneNumber("0000000000");
                        this.userService.handleCreateUser(user);
                }

                LoginResponse resLoginDTO = new LoginResponse();
                User userDB = this.userService.getUserByEmail(email);
                LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(userDB.getId(), userDB.getEmail(),
                                userDB.getFullName(), userDB.getRole());
                resLoginDTO.setUser(userLogin);
                String access_token = this.securityUtil.createAccessToken(email, userLogin);
                String refreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);
                resLoginDTO.setAccessToken(access_token);
                this.userService.updateUserToken(refreshToken, email);

                ResponseCookie cookie = ResponseCookie
                                .from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .maxAge(3600 * 24)
                                .path("/")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(resLoginDTO);
        }

}
