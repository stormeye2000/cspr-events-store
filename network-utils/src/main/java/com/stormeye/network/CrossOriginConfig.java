package com.stormeye.network;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Marker Interface for CORS configuration
 */
@CrossOrigin(origins = {"audit.stormeye2000.com", "store.stormeye2000.com"}, methods = {RequestMethod.GET})
public interface CrossOriginConfig {}
