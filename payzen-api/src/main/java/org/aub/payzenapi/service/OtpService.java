package org.aub.payzenapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final CacheManager cacheManager;
    private final ConcurrentHashMap<String, Long> otpTimestamps = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_MINUTES = 5;

    public void storeOtp(String key, String otp) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.put(key, otp);
            otpTimestamps.put(key, System.currentTimeMillis());
        }
    }

    public String getOtp(String key) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            Long timestamp = otpTimestamps.get(key);
            if (timestamp != null) {
                long currentTime = System.currentTimeMillis();
                if (TimeUnit.MILLISECONDS.toMinutes(currentTime - timestamp) >= OTP_VALIDITY_MINUTES) {
                    removeOtp(key);
                    return null;
                }
                Cache.ValueWrapper wrapper = cache.get(key);
                return wrapper != null ? (String) wrapper.get() : null;
            }
        }
        return null;
    }

    public void removeOtp(String key) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.evict(key);
            otpTimestamps.remove(key);
        }
    }
}