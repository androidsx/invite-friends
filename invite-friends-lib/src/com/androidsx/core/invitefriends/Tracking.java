package com.androidsx.core.invitefriends;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class Tracking {

    class Events {
        public static final String INVITE_SHOWN = "Invite friends popup shown";

        public static final String INVITE = "Invite friends button clicked";
    }

    /** Properties. Mapped to one or more events ad-hoc. */
    class Properties {
        public static final String METHOD = "Method";
        public static final String FRIENDS_INVITED = "Friends invited";
        public static final String STATUS = "Status";
        public static final String MESSAGE = "Message";
    }

    class Values {
        public static final String FACEBOOK = "Facebook";
        public static final String WHATSAPP = "WhatsApp";
        public static final String OTHER = "Other";
        public static final String NONE = "None of them";

        public static final String OK = "Ok";
        public static final String ERROR = "Error";
    }

    static Map<String, String> build(String... properties) {
        if (properties.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong format for the properties: " + Arrays.asList(properties));
        } else {
            final Map<String, String> propertyMap = new HashMap<String, String>();
            for (int i = 0; i < properties.length - 1; i += 2) {
                propertyMap.put(String.valueOf(properties[i]), properties[i + 1]);
            }
            return propertyMap;
        }
    }
}