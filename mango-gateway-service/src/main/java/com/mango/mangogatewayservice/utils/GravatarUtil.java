package com.mango.mangogatewayservice.utils;

import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;
import de.bripkens.gravatar.Rating;

public class GravatarUtil {
    public static String gravatar(String email) {
        return new Gravatar()
                .setSize(50)
                .setHttps(true)
                .setRating(Rating.PARENTAL_GUIDANCE_SUGGESTED)
                .setStandardDefaultImage(DefaultImage.RETRO)
                .getUrl(email);
    }
}
