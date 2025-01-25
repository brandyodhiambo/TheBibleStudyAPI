package com.brandyodhiambo.bibleApi.feature.usermgt.service.confirmationToken;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.ConfirmationToken;

public interface ConfirmationTokenService {
    public void sendEmailConfirmation(String email,String token);
    public void saveConfirmationToken(ConfirmationToken confirmationToken);
    public ConfirmationToken findByToken(String token);
}
