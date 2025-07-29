package com.services.salesorder.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonStatic {
    public static Long tenantID =  10000007L;
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final long MAXIMUM_REQUEST_RETRY_ATTEMPTS = 3;
    public static final Duration MIN_BACKOFF_TIME = Duration.ofMillis(500);

    public static final String V1 = "/api/v1";
    public static final String V2 = "/api/v2";
    public static final String VERSION_ANT = "/api/v*";

    public static final String MAINTAINER = "/maintainer";
    public static final String ADMIN = "/admin";
    public static final String PROTEC = "/protect";

    public static final String MAINTENANCE_FILTER = VERSION_ANT + MAINTAINER + "/**";
    public static final String ADMIN_FILTER = VERSION_ANT + ADMIN + "/**";
    public static final String PROTECT_FILTER = VERSION_ANT + PROTEC + "/**";
}
