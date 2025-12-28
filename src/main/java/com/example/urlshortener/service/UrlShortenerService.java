
package com.example.urlshortener.service;

import java.time.LocalDateTime;
import java.util.Optional;



import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.exception.AliasAlreadyExistsException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;

import jakarta.transaction.Transactional;


@Service 
public class UrlShortenerService {
    
    private final UrlMappingRepository urlMappingRepository;
    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }
    /**
     * Shortens a given URL, with options for a custom alias and an expiration.
     *
     * @param originalUrl   The long URL to shorten.
     * @param customAlias   An optional user-defined short code.
     * @param hoursToExpire An optional TTL in hours. If null, the link is
     *                      permanent.
     * @return The final shortCode.
     */
    @Transactional
    public String shortenUrl(String originalUrl, String customAlias,Integer hoursToExpire) {    
        if (StringUtils.hasText(customAlias)) {
            // Check if the custom alias is already taken.
            if (urlMappingRepository.findByShortCode(customAlias).isPresent()) {
                throw new AliasAlreadyExistsException("Alias '" + customAlias + "' is already in use.");
            }
            // If we reach here, the alias is available. We'll use it.
            UrlMapping newUrlMapping = new UrlMapping();
            newUrlMapping.setOriginalUrl(originalUrl);
            newUrlMapping.setCreationDate(LocalDateTime.now());
            newUrlMapping.setShortCode(customAlias); // Use the user's provided alias
            // If the user provided a TTL, calculate and set the expiration date.
            if (hoursToExpire != null) {
                newUrlMapping.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpire));
            } // If hoursToExpire is null, the expirationDate field remains null (permanent
              // link).
            urlMappingRepository.save(newUrlMapping);
            return customAlias;
        }
        else{
            UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setCreationDate(LocalDateTime.now());
        // If the user provided a TTL, calculate and set the expiration date.
        UrlMapping savedEntity = urlMappingRepository.save(urlMapping);
        String shortCode = encodeBase62(savedEntity.getId());
        savedEntity.setShortCode(shortCode);
        urlMappingRepository.save(savedEntity);
        return shortCode;
        }
        
    }

    
    @Transactional
    public String getOriginalUrlAndIncrementClicks(String shortCode) {
        // First, find the entity. If it's not found at all, throw the exception as before.
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));
        // Next, check if the URL has expired. If it has, we treat it as not found.
        if (urlMapping.getExpirationDate() != null &&
                urlMapping.getExpirationDate().isBefore(LocalDateTime.now())) {
            // The expiration date is set and it is in the past. The link has expired.

            // OPTION 1 (Our Choice): Throw UrlNotFoundException.
            // This is simple, pragmatic, and reuses our existing global 404 handler.
            throw new UrlNotFoundException("This link has expired and is no longer active.");
            
            // OPTION 2 (Alternative): For a more advanced implementation, you could:
            // 1. Create a new `UrlExpiredException`.
            // 2. Throw `new UrlExpiredException(...)` here.
            // 3. Add a new handler in `GlobalExceptionHandler` to map it to a 410 GONE
            // status.
        }
        // If the code reaches here, the link is valid (either permanent or not yet expired). Proceed with incrementing the click count and saving.
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }

    
    public UrlStatsResponse getStats(String shortCode) {
        
        
        
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No statistics found for short code: " + shortCode));
        String fullShortUrl = "http://localhost:8080/" + urlMapping.getShortCode();
        return new UrlStatsResponse(
            urlMapping.getOriginalUrl(),
            fullShortUrl,
            urlMapping.getCreationDate(),
            urlMapping.getClickCount()
        );
    }
    private String encodeBase62(Long number) {    
       if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        long num = number; 

        while (num > 0) {    
            int remainder = (int) (num % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}