package com.ssport.service.users.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.ssport.dto.users.*;
import com.ssport.exception.ErrorCode;
import com.ssport.exception.SportAppException;
import com.ssport.model.users.*;
import com.ssport.repository.datajpa.PasswordResetTokenRepository;
import com.ssport.repository.datajpa.RoleRepository;
import com.ssport.repository.datajpa.UserRepository;
import com.ssport.service.processing.KafkaMessageProducer;
import com.ssport.service.tokens.CustomTokenService;
import com.ssport.service.users.UserService;
import com.ssport.util.Constants;
import com.ssport.util.DateTimeUtil;
import com.ssport.util.Platform;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomTokenService customTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final KafkaMessageProducer kafkaMessageProducer;
    @Value("${clientApp.google.appId}")
    private String clientGoogleAppId;
    @Value("${clientApp.google.androidAppId}")
    private String clientGoogleAndroidAppId;
    @Value("${clientApp.google.IOSAppId}")
    private String clientGoogleIOSAppId;
    @Value("${clientApp.apple.clientId}")
    private String appleClientId;
    @Value("${clientApp.apple.teamId}")
    private String appleTeamId;
    @Value("${clientApp.apple.keyId}")
    private String appleKeyId;
    @Value("${clientApp.apple.url}")
    private String appleUrl;
    @Value("${clientApp.apple.certificate}")
    private String appleCertificate;
    private ObjectMapper mapper = new ObjectMapper();

    @Resource
    private ResourceLoader resourceLoader;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository,
                           CustomTokenService customTokenService, PasswordResetTokenRepository passwordResetTokenRepository,
                           KafkaMessageProducer kafkaMessageProducer) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customTokenService = customTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    private PrivateKey generatePrivateKey() {
        try {

            InputStream inputStream = resourceLoader.getResource("classpath:" + appleCertificate).getInputStream();

            PEMParser pemParser = new PEMParser(new InputStreamReader(inputStream));

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

            return converter.getPrivateKey(object);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SportAppException(ErrorCode.UNEXPECTED_ERROR, "Unexpected error");
        }
    }

    private String generateJWT() {
        PrivateKey pKey = generatePrivateKey();

        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, appleKeyId)
                .setIssuer(appleTeamId)
                .setAudience("https://appleid.apple.com")
                .setSubject(appleClientId)
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.ES256, pKey)
                .compact();
    }

    private PublicKey getPublicKey(String keyIdentifierSentByApp) {
        try {

            String str = new String(Base64.getDecoder().decode(keyIdentifierSentByApp));
            URL url = new URL("https://appleid.apple.com/auth/keys");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            if (status >= 200 && status < 300) {
                Scanner scanner = new Scanner(con.getInputStream());
                String result = scanner.useDelimiter("/*\"\"*/").next();
                JSONObject obj = new JSONObject(result);
                JSONArray keyArray = obj.getJSONArray("keys");

                ApplePublicKey[] keys = mapper.readValue(keyArray.toString(), ApplePublicKey[].class);

                for (ApplePublicKey key : keys) {
                    if (key.getKid().equals(keyIdentifierSentByApp)) {
                        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getN()));
                        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getE()));
                        return KeyFactory.getInstance(key.getKty()).generatePublic(new RSAPublicKeySpec(modulus, exponent));
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SportAppException(ErrorCode.UNEXPECTED_ERROR, "Unexpected error");
        }
    }

    public TokenResponse appleAuth(String accessCode) {
        String token = generateJWT();
        return getTokenResponse(accessCode, token);
    }

    private TokenResponse getTokenResponse(String authorizationCodeSentByApp, String token) {
        try {
            URL url = new URL("https://appleid.apple.com/auth/token");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            StringBuilder sb = new StringBuilder();
            sb.append("client_id=").append(appleClientId).append("&")
                    .append("code=").append(authorizationCodeSentByApp).append("&")
                    .append("client_secret=").append(token).append("&")
                    .append("grant_type=").append("authorization_code");
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(sb.toString().getBytes());
            }
            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                Scanner scanner = new Scanner(con.getInputStream());
                String result = scanner.useDelimiter("/*\"\"*/").next();
                TokenResponse tokenResponse = mapper.readValue(result, TokenResponse.class);

                return tokenResponse;
            } else {
                throw new SportAppException(ErrorCode.AUTHORIZATION_ERROR, "Cannot validate token. Status: " + status);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SportAppException(ErrorCode.UNEXPECTED_ERROR, "Unexpected error");
        }
    }

    private Claims getClaims(String keyIdentifier, String idToken) {
        PublicKey publicKey = getPublicKey(keyIdentifier);
        try {
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(idToken).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SportAppException(ErrorCode.VALIDATION_ERROR, "Cannot parse user's claims");
        }
    }

    @Override
    public boolean validatePassword(User user, String passwordToCheck) {
        return passwordEncoder.matches(passwordToCheck, user.getPassword());
    }

    @Override
    @Transactional
    public UserDataResponseDTO registerNewUser(NewUserDTO newUserDTO) {
        return createNewUser(newUserDTO, true);
    }

    @Override
    public String completeUpdate(ChangePasswordDTO changePasswordDTO) {
        VerificationCodeDto vcDto = new VerificationCodeDto();
        vcDto.setCode(changePasswordDTO.getCode());
        vcDto.setEmail(changePasswordDTO.getEmail());

        Map<String, Boolean> result = checkVerificationCode(vcDto);
        if (result.get("isEmailVerified")) {
            return changePassword(changePasswordDTO);
        } else {
            return "Wrong email verification code.";
        }

    }

    @Override
    public UserDataResponseDTO loginWithEmail(LoginDTO loginDto) {

        Authentication authentication = customTokenService.getAuthentication(loginDto.getEmail(), loginDto.getPassword());

        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        UserResponseDTO userResponseDto = new UserResponseDTO(user);

        UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();
        userDataResponseDto.setUser(userResponseDto);

//      If email is not verified, then return users and isEmailVerified-flag(false)
        userDataResponseDto.setEmailVerified(user.isEmailVerified());

        if (!user.isEmailVerified()) {
            userDataResponseDto.setEmailVerified(user.isEmailVerified());
            return userDataResponseDto;
        }

        OAuth2AccessToken accessToken = customTokenService.getToken(userResponseDto.getEmail());
        userDataResponseDto.setToken(accessToken);
        userDataResponseDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        return userDataResponseDto;
    }

    @Override
    @Transactional
    public void deleteUserProfile(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with id <" + userId + "> not found");
        }
    }

    @Override
    @Transactional
    public UserDataResponseDTO updateUserProfile(UUID userId, UpdateProfileDTO profileDTO) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return updateUserProfile(profileDTO, user.get());
        } else {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with id <" + userId + "> not found");
        }
    }

    @Override
    @Transactional
    public UserDataResponseDTO createUserProfile(NewUserDTO userDTO) {
        return createNewUser(userDTO, true);
    }

    @Override
    public UserDataResponseDTO getProfile(Authentication authentication) {
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();

        UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();

        UserResponseDTO userResponseDTO = new UserResponseDTO(currentUser);
        userDataResponseDto.setUser(userResponseDTO);

        return userDataResponseDto;
    }

    @Override
    public UserDataResponseDTO getUserProfile(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();
            userDataResponseDto.setUser(new UserResponseDTO(user.get()));

            return userDataResponseDto;
        } else {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with id <" + userId + "> not found");
        }
    }

    @Override
    @Transactional
    public UserDataResponseDTO updatePassword(Authentication authentication, UpdatePasswordDTO updatePasswordDto) {
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();

        if (validatePassword(currentUser, updatePasswordDto.getOldPassword())) {
            currentUser.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
            userRepository.save(currentUser);

            UserDataResponseDTO responseDTO = new UserDataResponseDTO();
            responseDTO.setUser(new UserResponseDTO(currentUser));
            responseDTO.setToken(customTokenService.getToken(currentUser.getEmail()));

            return responseDTO;
        } else {
            throw new SportAppException(ErrorCode.INVALID_PASSWORD, "Old password is wrong");
        }
    }

    @Override
    @Transactional
    public UserDataResponseDTO updateProfile(Authentication authentication, UpdateProfileDTO updateProfileDto) {
        User currentUser = ((UserPrincipal) authentication.getPrincipal()).getUser();
        boolean isDataChanged = false;

        UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();

        if (updateProfileDto.getEmail() != null && !updateProfileDto.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            if (doesEmailExist(updateProfileDto.getEmail())) {
                throw new SportAppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email <" + updateProfileDto.getEmail() + "> is already taken. Try another");
            }

            customTokenService.revokeTokens(currentUser.getEmail());
            customTokenService.revokeTokens(updateProfileDto.getEmail());

            currentUser.setEmail(updateProfileDto.getEmail());
            currentUser.setEmailVerified(false);
            userDataResponseDto.setEmailVerified(false);
            currentUser.setAttemptsCount(0);

            String verificationCode = generateRandomNumericString(Constants.VERIFICATION_CODE_LENGTH);
            currentUser.setVerificationCode(verificationCode);
            isDataChanged = true;
        }

        if (updateProfileDto.getFullName() != null && !updateProfileDto.getFullName().isEmpty()) {
            currentUser.setFullName(updateProfileDto.getFullName());
            isDataChanged = true;
        }

        if (isDataChanged) {
            currentUser = userRepository.save(currentUser);
        }

        userDataResponseDto.setUser(new UserResponseDTO(currentUser));
        return userDataResponseDto;
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(passwordResetDTO.getUserId()));
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with id <" + passwordResetDTO.getUserId() + "> not found");
        }

        User user = optionalUser.get();

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user).orElse(new PasswordResetToken());
        passwordResetToken.setToken(generateRandomNumericString(Constants.PASSWORD_RESET_TOKEN_LENGTH));
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(DateTimeUtil.getLocalDateTimeUtc().plusMinutes(Constants.PASSWORD_UPDATE_TOKEN_EXPIRATION));
        passwordResetToken.setAttemptsCount(0);

        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);

        kafkaMessageProducer.sendEmailPasswordResetCode(passwordResetToken);
    }

    @Override
    @Transactional
    public void passwordRecovery(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with email <" + email + "> not found");
        }

        User user = optionalUser.get();

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user).orElse(new PasswordResetToken());
        passwordResetToken.setToken(generateRandomNumericString(Constants.PASSWORD_RESET_TOKEN_LENGTH));
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(DateTimeUtil.getLocalDateTimeUtc().plusMinutes(Constants.PASSWORD_RESET_TOKEN_EXPIRATION));
        passwordResetToken.setAttemptsCount(0);

        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);

        kafkaMessageProducer.sendEmailPasswordRecoveryCode(passwordResetToken);
    }

    private PasswordResetToken getPasswordResetToken(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with email <" + email + "> not found");
        }
        User user = optionalUser.get();

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user).orElse(new PasswordResetToken());
        passwordResetToken.setToken((generateRandomNumericString(Constants.PASSWORD_RESET_TOKEN_LENGTH)));
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(DateTimeUtil.getLocalDateTimeUtc().plusMinutes(Constants.PASSWORD_UPDATE_TOKEN_EXPIRATION));
        passwordResetToken.setAttemptsCount(0);

        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }

    @Override
    @Transactional(noRollbackFor = SportAppException.class)
    public String changePassword(ChangePasswordDTO changePasswordDTO) {
        Optional<User> optionalUser = userRepository.findByEmailAndIsEmailVerifiedIsTrue(changePasswordDTO.getEmail());
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with email <" + changePasswordDTO.getEmail() + "> not found");
        }

        User user = optionalUser.get();

        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByUser(user);
        if (!optionalPasswordResetToken.isPresent()) {
            throw new SportAppException(ErrorCode.CODE_NOT_FOUND, "Password recovery code not found.");
        }

        PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
        if (passwordResetToken.getExpiryDate().isBefore(DateTimeUtil.getLocalDateTimeUtc())) {
            throw new SportAppException(ErrorCode.TIME_EXPIRED, "Token usage time has expired");
        }

        if (passwordResetToken.getAttemptsCount() >= Constants.PASSWORD_RESET_NUMBER_ATTEMPTS) {
            throw new SportAppException(ErrorCode.NUMBER_OF_ATTEMPTS_EXCEEDED, "Number of attempts to enter the correct code exceeded. Repeat the password recovery procedure again.");
        }

        if (!changePasswordDTO.getCode().equals(passwordResetToken.getToken())) {
            passwordResetToken.setAttemptsCount(passwordResetToken.getAttemptsCount() + 1);
            passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);

            throw new SportAppException(ErrorCode.WRONG_CODE, "You entered the wrong code. You have "
                    + (Constants.PASSWORD_RESET_NUMBER_ATTEMPTS - passwordResetToken.getAttemptsCount()) + " attempt(s) left.");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);

        return "Password successfully changed.";
    }

    @Override
    @Transactional
    public void getVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndIsEmailVerifiedIsFalse(email);
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "Unverified user with email <" + email + "> not found");
        }
        User user = optionalUser.get();
        String verificationCode = generateRandomNumericString(Constants.VERIFICATION_CODE_LENGTH);
        user.setVerificationCode(verificationCode);
        user.setAttemptsCount(0);
        userRepository.save(user);

        kafkaMessageProducer.sendEmailVerificationCode(optionalUser.get(), verificationCode);
    }


    @Override
    @Transactional(noRollbackFor = SportAppException.class)
    public Map checkVerificationCode(VerificationCodeDto verificationCodeDto) {
        Optional<User> optionalUser = userRepository.findByEmailAndIsEmailVerifiedIsFalse(verificationCodeDto.getEmail());
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User with email <" + verificationCodeDto.getEmail() + "> not found");
        }
        User user = optionalUser.get();
        if (user.getAttemptsCount() >= Constants.VERIFICATION_CODE_NUMBER_ATTEMPTS) {
            throw new SportAppException(ErrorCode.NUMBER_OF_ATTEMPTS_EXCEEDED, "Number of attempts to enter the correct code exceeded. Repeat the verification code procedure again.");
        }
        if (user.getVerificationCode().equals(verificationCodeDto.getCode())) {
            user.setEmailVerified(true);
            userRepository.save(user);
            return Collections.singletonMap("isEmailVerified", true);
        } else {
            user.setAttemptsCount(user.getAttemptsCount() + 1);
            userRepository.save(user);
            userRepository.flush();
            throw new SportAppException(ErrorCode.WRONG_CODE, "You entered the wrong code. You have "
                    + (Constants.VERIFICATION_CODE_NUMBER_ATTEMPTS - user.getAttemptsCount()) + " attempt(s) left.");
        }
    }

    @Override
    @Transactional
    public UserDataResponseDTO changeEmail(ChangeEmailDto changeEmailDto) {
        Optional<User> optionalUser = userRepository.findByIdAndEmailAndIsEmailVerifiedIsFalse(changeEmailDto.getUserId(),
                changeEmailDto.getOldEmail());
        if (!optionalUser.isPresent()) {
            throw new SportAppException(ErrorCode.USER_NOT_FOUND, "User not found");
        }

        UserDataResponseDTO userDataResponseDTO = new UserDataResponseDTO();
        userDataResponseDTO.setEmailVerified(false);

        if (doesEmailExist(changeEmailDto.getNewEmail())) {
            throw new SportAppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email is already in use");
        } else {
            User user = optionalUser.get();
            String verificationCode = generateVerificationCode();
            user.setEmail(changeEmailDto.getNewEmail());
            user.setVerificationCode(verificationCode);
            userRepository.save(user);

            kafkaMessageProducer.sendEmailVerificationCode(user, verificationCode);
            userDataResponseDTO.setUser(new UserResponseDTO(user));

            return userDataResponseDTO;
        }
    }

    @Override
    @Transactional
    public UserDataResponseDTO socialRegister(NewSocialUserDto socialUserDto) throws GeneralSecurityException, IOException {
        UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();
        userDataResponseDto.setSocialInput(true);

        if (socialUserDto.getProvider().equals("facebook")) {
            Facebook facebook = new FacebookTemplate(socialUserDto.getToken());
            org.springframework.social.facebook.api.User facebookUser = facebook.fetchObject("me",
                    org.springframework.social.facebook.api.User.class, Constants.facebookFieldsForSignUp);

            User user = getUser(facebookUser.getEmail());

            if (user.getFullName() == null) {
                user.setFullName(facebookUser.getFirstName() + " " + facebookUser.getLastName());
            }

            if (user.getAvatarLink() == null) {
                user.setAvatarLink(facebook.getBaseGraphApiUrl() + facebookUser.getId() + "/picture?type=large");
            }

            setUserData(user);

            if (user.getSignInProvider() == null) {
                user.setSignInProvider(SocialMediaService.FACEBOOK);
            }

            user = userRepository.save(user);

            userDataResponseDto.setUser(new UserResponseDTO(user));

            userDataResponseDto.setEmailVerified(user.isEmailVerified());

            if (!user.isEmailVerified()) {
                userDataResponseDto.setEmailVerified(user.isEmailVerified());
            }

            userDataResponseDto.setToken(customTokenService.getToken(user.getEmail()));
            userDataResponseDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

            return userDataResponseDto;

        } else if (socialUserDto.getProvider().equals("google")) {
            String appId;
            if (socialUserDto.getPlatform() == null) {
                appId = clientGoogleAppId;
            } else if (socialUserDto.getPlatform().toUpperCase().equals(Platform.ANDROID.name())) {
                appId = clientGoogleAndroidAppId;
            } else if (socialUserDto.getPlatform().toUpperCase().equals(Platform.IOS.name())) {
                appId = clientGoogleIOSAppId;
            } else {
                throw new SportAppException(ErrorCode.VALIDATION_ERROR, "Unknown platform");
            }
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(appId))
                    .build();
            GoogleIdToken idToken = verifier.verify(socialUserDto.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                User user = getUser(payload.getEmail());

                if (user.getFullName() == null) {
                    user.setFullName(payload.get("given_name") + " " + payload.get("family_name"));
                }

                if (user.getAvatarLink() == null) {
                    user.setAvatarLink((String) payload.get("picture"));
                }

                setUserData(user);

                if (user.getSignInProvider() == null) {
                    user.setSignInProvider(SocialMediaService.GOOGLE);
                }

                user = userRepository.save(user);

                userDataResponseDto.setUser(new UserResponseDTO(user));

                userDataResponseDto.setEmailVerified(user.isEmailVerified());

                if (!user.isEmailVerified()) {
                    userDataResponseDto.setEmailVerified(user.isEmailVerified());
                }

                userDataResponseDto.setToken(customTokenService.getToken(user.getEmail()));
                userDataResponseDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

                return userDataResponseDto;
            } else {
                throw new SportAppException(ErrorCode.INVALID_TOKEN, "Invalid token_id");
            }
        } else if (socialUserDto.getProvider().equals("apple")) {
            try {
                User user;
                String decodedAccessCode = new String(Base64.getDecoder().decode(socialUserDto.getToken()));

                TokenResponse response = appleAuth(decodedAccessCode);

                if (response != null) {
                    Map<String, String> headers = JwtHelper.headers(response.getIdToken());
                    String kid = headers.get("kid");
                    Claims claims = getClaims(kid, response.getIdToken());
                    user = getAppleUser(claims.get("sub", String.class), claims.get("email", String.class));
                    user.setSignInProvider(SocialMediaService.APPLE);
                    if (!user.isEmailVerified()) {
                        if (socialUserDto.getFullName() == null || socialUserDto.getFullName().trim().isEmpty()) {
                            throw new SportAppException(ErrorCode.VALIDATION_ERROR, "User fullName can't be empty for new users");
                        } else {
                            user.setFullName(socialUserDto.getFullName());
                        }
                    }

                    setUserData(user);
                    user = userRepository.save(user);
                    userDataResponseDto.setUser(new UserResponseDTO(user));
                    userDataResponseDto.setToken(customTokenService.getToken(user.getEmail()));
                    userDataResponseDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

                    return userDataResponseDto;
                } else {
                    throw new SportAppException(ErrorCode.USER_NOT_FOUND, "Cannot get user info");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (!(e instanceof SportAppException)) {
                    throw new SportAppException(ErrorCode.UNEXPECTED_ERROR, "Cannot get user info");
                } else {
                    throw (SportAppException) e;
                }
            }
        } else {
            throw new SportAppException(ErrorCode.INVALID_PROVIDER, "Invalid provider id");
        }
    }

    private User getAppleUser(String id, String email) {
        User user;
        Optional<User> userOptional = userRepository.findByAppleId(id);
        Optional<User> emailUserOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            if (emailUserOptional.isPresent()) {
                if (emailUserOptional.get().equals(userOptional.get())) {
                    user = userOptional.get();
                } else {
                    throw new SportAppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
                }
            } else {
                user = userOptional.get();
            }
        } else {
            if (emailUserOptional.isPresent()) {
                user = emailUserOptional.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setAppleId(id);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            }
        }

        return user;
    }

    private User getUser(String email) {
        User user;
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        }
        return user;
    }

    private void setUserData(User user) {
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals(Constants.ROLE_SOCIAL_USER))) {
            user.getRoles().add(roleRepository.findByName(Constants.ROLE_SOCIAL_USER));
        }

        if (user.getDateJoined() == null) {
            user.setDateJoined(DateTimeUtil.getLocalDateTimeUtc());
        }

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
        }
    }

    private UserDataResponseDTO updateUserProfile(UpdateProfileDTO updateProfileDto, User currentUser) {
        boolean isDataChanged = false;

        UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();
        userDataResponseDto.setEmailVerified(currentUser.isEmailVerified());

        if (updateProfileDto.getEmail() != null && !updateProfileDto.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            if (doesEmailExist(updateProfileDto.getEmail())) {
                throw new SportAppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email <" + updateProfileDto.getEmail() + "> is already taken. Try another");
            }

            customTokenService.revokeTokens(currentUser.getEmail());
            customTokenService.revokeTokens(updateProfileDto.getEmail());

            currentUser.setEmail(updateProfileDto.getEmail());
            currentUser.setEmailVerified(false);
            userDataResponseDto.setEmailVerified(false);
            currentUser.setAttemptsCount(0);

            PasswordResetToken passwordResetToken = getPasswordResetToken(currentUser.getEmail());

            String verificationCode = generateRandomNumericString(Constants.VERIFICATION_CODE_LENGTH);
            currentUser.setVerificationCode(verificationCode);

            kafkaMessageProducer.sendEmailVerificationAndPasswordResetCode(passwordResetToken, verificationCode);


            isDataChanged = true;
        }

        if (updateProfileDto.getFullName() != null && !updateProfileDto.getFullName().isEmpty()) {
            currentUser.setFullName(updateProfileDto.getFullName());
            isDataChanged = true;
        }

        if (isDataChanged) {
            currentUser = userRepository.save(currentUser);
        }

        userDataResponseDto.setUser(new UserResponseDTO(currentUser));
        return userDataResponseDto;
    }


    private UserDataResponseDTO createNewUser(NewUserDTO newUserDTO, boolean needEmailVerification) {
        Optional<User> userOptional = userRepository.findByEmail(newUserDTO.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.isEmailVerified()) {
                throw new SportAppException(ErrorCode.USER_ALREADY_EXISTS, "User already exists " + newUserDTO.getEmail());
            }
        } else {
            user = new User();
        }

        user.setFullName(newUserDTO.getFullName());
        user.setEmail(newUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(newUserDTO.getPassword()));
        user.setDateJoined(DateTimeUtil.getLocalDateTimeUtc());
        UserDataResponseDTO userDataResponseDto = new UserDataResponseDTO();

        user.getRoles().add(roleRepository.findByName(Constants.ROLE_REGISTERED_USER));

        if (needEmailVerification) {

            user.setEmailVerified(false);
            user.setAttemptsCount(0);

            userDataResponseDto.setEmailVerified(false);

            String verificationCode = generateRandomNumericString(Constants.VERIFICATION_CODE_LENGTH);
            user.setVerificationCode(verificationCode);

            user = userRepository.save(user);

            kafkaMessageProducer.sendEmailVerificationCode(user, verificationCode);

        } else {
            user.setEmailVerified(true);
            userDataResponseDto.setEmailVerified(true);
            user = userRepository.save(user);
        }

//        OAuth2AccessToken accessToken = customTokenService.getToken(users.getEmail());

        userDataResponseDto.setUser(new UserResponseDTO(user));

//        userDataResponseDto.setAccessToken(accessToken.getValue());

        return userDataResponseDto;
    }


    private boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private String generateVerificationCode() {
        return UUID.randomUUID().toString();
    }

    private String generateRandomNumericString(int length) {
        Random random = new Random();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(random.nextInt(10));
        }

        return result.toString();
    }

}
