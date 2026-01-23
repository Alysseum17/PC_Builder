package com.pcbuilder.core.modules.auth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pcbuilder.core.modules.auth.dto.*;
import com.pcbuilder.core.modules.auth.jwt.JwtTokenProvider;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TwoFactorService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    @Transactional
    public Enable2FAResponse enable2FA(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is already enabled");
        }
        GoogleAuthenticatorKey credentials = googleAuthenticator.createCredentials();
        String secretKey = credentials.getKey();
        user.setTwoFactorSecret(secretKey);
        Set<String> backupCodes = generateBackupCodes();
        user.setBackupCodes(backupCodes);

        userRepository.save(user);

        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                "PCBuilder",
                user.getEmail(),
                credentials
        );
        return Enable2FAResponse.builder().
                qrCodeUrl(qrCodeUrl).
                secret(secretKey).
                backupCodes(new ArrayList<String>(backupCodes)).
                build();
    }
    @Transactional
    public MessageResponse verify2FASetup(String username, Verify2FASetupRequest request) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is already enabled");
        }
       if (user.getTwoFactorSecret() == null) {
              throw new RuntimeException("2FA setup not initiated");
       }
       int code = Integer.parseInt(request.getCode());
       boolean isCodeValid = googleAuthenticator.authorize(user.getTwoFactorSecret(), code);
         if (!isCodeValid) {
              throw new RuntimeException("Invalid 2FA code");
         }
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
        return new MessageResponse("2FA enabled successfully");
    }
    public MessageResponse disable2FA(String username, String code) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled");
        }
        if(!verify2FACode(user.getUsername(), code)) {
            throw new RuntimeException("Invalid 2FA code");
        }
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        user.setBackupCodes(new HashSet<>());
        userRepository.save(user);
        return new MessageResponse("2FA disabled successfully");
    }
    @Transactional
    public JwtResponse verify2FALogin(Verify2FALoginRequest request) {
        if(!jwtTokenProvider.validateToken(request.getTempToken())) {
            throw new RuntimeException("Invalid temp token");
        }
        String username = jwtTokenProvider.getUsernameFromJWT(request.getTempToken());
        if(!verify2FACode(username, request.getCode())) {
            throw new RuntimeException("Invalid 2FA code");
        }
        String authToken = jwtTokenProvider.generateToken(username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);
        return JwtResponse.builder().authToken(authToken).refreshToken(refreshToken).build();
    }
    public boolean verify2FACode(String username, String code) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("2FA is not enabled");
        }
        if(user.getBackupCodes() != null && user.getBackupCodes().contains(code)) {
            Set<String> backupCodes = user.getBackupCodes();
            backupCodes.remove(code);
            userRepository.save(user);
            return true;
        }
        try {
            int intCode = Integer.parseInt(code);
            return googleAuthenticator.authorize(user.getTwoFactorSecret(), intCode);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private Set<String> generateBackupCodes() {
        SecureRandom secureRandom = new SecureRandom();
        return IntStream.range(0, 10).
                mapToObj(i -> String.format("%08d", secureRandom.nextInt(100_000_000)))
                .collect(Collectors.toSet());
    }
    public String generateQRCodeImage(String qrCodeUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
