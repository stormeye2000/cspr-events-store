package com.stormeye.network;

import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Marker Interface for CORS configuration
 */
@CrossOrigin(origins = {"audit.stormeye2000.com", "store.stormeye2000.com"})
public interface CrossOriginConfig {}
