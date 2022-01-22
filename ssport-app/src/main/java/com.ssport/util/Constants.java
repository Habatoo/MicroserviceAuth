package com.ssport.util;

import com.google.maps.model.AddressType;
import com.google.maps.model.PlaceType;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Constants {

    public static final int SRID = 4326;

    // Roles
    public static final String ROLE_GUEST_USER = "ROLE_GUEST_USER";

    public static final String ROLE_REGISTERED_USER = "ROLE_REGISTERED_USER";

    public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";

    public static final String ROLE_RESTAURANT_USER = "ROLE_RESTAURANT_USER";

    public static final String ROLE_SOCIAL_USER = "ROLE_SOCIAL_USER";

    // Constant to define users, which are not restaurant-users or administrators
    public static final String ROLE_COMMON_USER = "ROLE_COMMON_USER";

    public static final String[] facebookFieldsForSignUp = {"id", "email", "first_name", "last_name", "picture"};

    public static final String[] facebookAllFields = {"id", "about", "age_range", "birthday", "context", "cover", "currency",
            "devices", "education", "email", "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown",
            "inspirational_people", "installed", "install_type", "is_verified", "languages", "last_name", "link", "locale",
            "location", "meeting_for", "middle_name", "name", "name_format", "political", "quotes", "payment_pricepoints",
            "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group", "timezone",
            "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift", "website", "work"};

    // PlaceTypes
    public static final Map<Integer, PlaceType> PLACE_TYPES = new ConcurrentHashMap<Integer, PlaceType>() {
        {
            put(1, PlaceType.BAR);
            put(2, PlaceType.CAFE);
            put(3, PlaceType.MEAL_DELIVERY);
            put(4, PlaceType.MEAL_TAKEAWAY);
            put(5, PlaceType.RESTAURANT);
        }
    };

    // PlaceAddressTypes
    public static final Map<Integer, AddressType> PLACE_ADDRESS_TYPES = new ConcurrentHashMap<Integer, AddressType>() {
        {
            put(1, AddressType.BAR);
            put(2, AddressType.CAFE);
            put(3, AddressType.MEAL_DELIVERY);
            put(4, AddressType.MEAL_TAKEAWAY);
            put(5, AddressType.RESTAURANT);
        }
    };

    public static final Locale DEFAULT_LOCALE = Locale.US;

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final int PLACE_CACHING_TIME = 7; // days

    public static final int PLACE_UPDATE_CACHING_TIME = 30; // days

    public static final int NEARBY_MIN_RADIUS = 2000; // radius

    public static final int NEARBY_MAX_RADIUS = 6000; // radius

    public static final int PASSWORD_RESET_TOKEN_EXPIRATION = 10; //minutes

    public static final int PASSWORD_UPDATE_TOKEN_EXPIRATION = 10080; //minutes in 7 days

    public static final int HAPPY_HOUR_IMAGE_EXPIRATION = 6;

    public static final int PLACE_IMAGE_EXPIRATION = 13;

    public static final int USER_PLACE_IMAGES_EXPIRATION = 6;

    public static final int COUPON_IMAGE_EXPIRATION = 6;

    public static final int GUEST_USER_LOGIN_MAX_COUNT = 50000;

    public static final int QUANTITY_COMPLAINTS_FOR_DELETE_HAPPY_HOUR = 5;

    public static final int PASSWORD_RESET_TOKEN_LENGTH = 6;

    public static final int PASSWORD_RESET_NUMBER_ATTEMPTS = 3;

    public static final int VERIFICATION_CODE_LENGTH = 6;

    public static final int VERIFICATION_CODE_NUMBER_ATTEMPTS = 3;

    public static final int PLACES_INFO_REPORT_MAX_ROWS = 250;

    public static final int PLACE_REVIEW_RECENCY_MONTHS = 3;

    public static final int PLACE_FAVORITES_MAX_SIZE = 25;

    public static final Map<String, MediaType> IMAGE_TYPES = new ConcurrentHashMap<String, MediaType>() {
        {
            put(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG);
            put(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_PNG);
            put(MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_GIF);
        }
    };

    public static final Map<String, String> PLACE_SORT_FIELDS = new ConcurrentHashMap<String, String>() {
        {
            put("totalLikes", "happy_hours_total_likes");
            put("totalReviews", "total_place_feedback");
            put("lastUpdated", "last_updated");
        }
    };

    public static final Map<String, String> HAPPY_HOUR_SORT_FIELDS = new ConcurrentHashMap<String, String>() {
        {
            put("dateCreated", "date_created");
        }
    };

    public static final String TIME_12HOURS_PATTERN = "(1[012]|[1-9]):[0-5][0-9] (AM|PM) [-/–] (1[012]|[1-9]):[0-5][0-9] (AM|PM)";

}
