package com.stormeye.network;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Marker Interface for CORS configuration
 */
@CrossOrigin(origins = {"**/swagger-ui/**","/management/**","/api/**"}, methods = {RequestMethod.GET})
public interface CrossOriginConfig {}
