/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.local.auth.api.endpoint.util;

import org.apache.commons.logging.Log;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.local.auth.api.core.AuthManager;
import org.wso2.carbon.identity.local.auth.api.core.exception.AuthAPIClientException;
import org.wso2.carbon.identity.local.auth.api.endpoint.constant.AuthEndpointConstants;
import org.wso2.carbon.identity.local.auth.api.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.local.auth.api.endpoint.exception.BadRequestException;
import org.wso2.carbon.identity.local.auth.api.endpoint.exception.ClientErrorException;
import org.wso2.carbon.identity.local.auth.api.endpoint.exception.InternalServerErrorException;
import org.wso2.carbon.identity.local.auth.api.endpoint.exception.NotAcceptableException;
import org.wso2.carbon.identity.local.auth.api.endpoint.exception.NotFoundException;

import java.util.Map;

/**
 * This class includes utility methods.
 */
public class AuthAPIEndpointUtil {

    private AuthAPIEndpointUtil() {
    }

    public static AuthManager getAuthManager() {

        return (AuthManager) PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(AuthManager.class,
                null);
    }

    /**
     * This method is used to create a client exceptions with the known errorCode and message based on error type.
     *
     * @param description Error message description
     * @param code        Error code
     * @param errorType   Error type
     * @param properties  Additional properties
     * @param log         Logger
     * @param e           Exception object
     * @return ClientErrorException with the given errorCode and description.
     */
    public static ClientErrorException buildClientErrorException(String description, String code,
                                                                 AuthAPIClientException.ErrorType errorType,
                                                                 Map<String, String> properties, Log log, Throwable e) {

        if (AuthAPIClientException.ErrorType.BAD_REQUEST.equals(errorType)) {
            return buildBadRequestException(description, code, properties, log, e);
        } else if (AuthAPIClientException.ErrorType.NOT_ACCEPTABLE.equals(errorType)) {
            return buildNotAcceptableException(description, code, properties, log, e);
        } else if (AuthAPIClientException.ErrorType.NOT_FOUND.equals(errorType)) {
            return buildNotFoundException(description, code, properties, log, e);
        }

        return buildBadRequestException(description, code, properties, log, e);
    }

    /**
     * This method is used to create a BadRequestException with the known errorCode and message.
     *
     * @param description Error message description.
     * @param code        Error code.
     * @param properties Additional properties
     * @param log Logger
     * @param e Exception object
     * @return BadRequestException with the given errorCode and description.
     */
    public static BadRequestException buildBadRequestException(String description, String code,  Map<String,String> properties,
                                                               Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(AuthEndpointConstants.STATUS_BAD_REQUEST_MESSAGE_DEFAULT, description, code,
                properties);
        logDebug(AuthEndpointConstants.STATUS_BAD_REQUEST_MESSAGE_DEFAULT, log, e);
        return new BadRequestException(errorDTO);
    }

    /**
     * This method is used to create a NotAcceptableException with the known errorCode and message.
     *
     * @param description Error message description.
     * @param code        Error code.
     * @param properties  Additional properties
     * @param log         Logger
     * @param e           Exception object
     * @return BadRequestException with the given errorCode and description.
     */
    public static NotAcceptableException buildNotAcceptableException(String description, String code, Map<String,
            String> properties, Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(AuthEndpointConstants.STATUS_NOT_ACCEPTABLE_MESSAGE_DEFAULT, description,
                code, properties);
        logDebug(AuthEndpointConstants.STATUS_NOT_ACCEPTABLE_MESSAGE_DEFAULT, log, e);
        return new NotAcceptableException(errorDTO);
    }

    /**
     * This method is used to create a NotFoundException with the known errorCode and message.
     *
     * @param description Error message description.
     * @param code        Error code.
     * @param properties  Additional properties
     * @param log         Logger
     * @param e           Exception object
     * @return NotFoundException with the given errorCode and description.
     */
    public static NotFoundException buildNotFoundException(String description, String code, Map<String, String>
            properties, Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(AuthEndpointConstants.STATUS_NOT_FOUND_MESSAGE_DEFAULT, description, code,
                properties);
        logDebug(AuthEndpointConstants.STATUS_NOT_FOUND_MESSAGE_DEFAULT, log, e);
        return new NotFoundException(errorDTO);
    }

    /**
     * This method is used to create an InternalServerErrorException with the known errorCode.
     *
     * @param code Error Code.
     * @return a new InternalServerErrorException with default details.
     */
    public static InternalServerErrorException buildInternalServerErrorException(String code, Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(AuthEndpointConstants.STATUS_INTERNAL_SERVER_ERROR_MESSAGE_DEFAULT,
                AuthEndpointConstants.STATUS_INTERNAL_SERVER_ERROR_MESSAGE_DEFAULT, code, null);
        logError(log, e);
        return new InternalServerErrorException(errorDTO);
    }

    private static ErrorDTO getErrorDTO(String message, String description, String code, Map<String,String>
            properties) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(code);
        errorDTO.setMessage(message);
        errorDTO.setDescription(description);
        errorDTO.setProperties(properties);
        return errorDTO;
    }

    private static void logError(Log log, Throwable throwable) {

        log.error(throwable.getMessage(), throwable);
    }

    private static void logDebug(String message, Log log, Throwable throwable) {

        if (log.isDebugEnabled()) {
            log.debug(message, throwable);
        }
    }
}
