package com.cloudvault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {
    private MultipartFile file;
    private ExpiryDuration expiryDuration;

    public enum ExpiryDuration {
        FIVE_MINUTES(5),
        ONE_HOUR(60),
        SIX_HOURS(360),
        TWENTY_FOUR_HOURS(1440),
        SEVEN_DAYS(10080);

        private final int minutes;

        ExpiryDuration(int minutes) {
            this.minutes = minutes;
        }

        public int getMinutes() {
            return minutes;
        }
    }
}
