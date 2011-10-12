/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2011 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. This program is distributed in the 
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. You should have received a copy of the GNU General 
 * Public License along with this program. If not, see http://www.gnu.org/licenses/ 
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is 
 * making a combined work based on this library. Thus, the terms and 
 * conditions of the GNU General Public License cover the whole combination. 
 * 
 * As a special exception, the copyright holders of this library give you permission 
 * to link this library with independent modules to produce an executable, regardless 
 * of the license terms of these independent modules, and to copy and distribute 
 * the resulting executable under terms of your choice, provided that you also meet, 
 * for each linked independent module, the terms and conditions of the license of 
 * that module. An independent module is a module which is not derived from or 
 * based on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obligated to do so. If you do not 
 * wish to do so, delete this exception statement from your version. 
 *
 */
package org.geosdi.geoplatform.gui.server.service.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.soap.SOAPFaultException;
import org.geosdi.geoplatform.core.model.GPProject;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.geosdi.geoplatform.core.model.GPUser;
import org.geosdi.geoplatform.core.model.GPUserProjects;
import org.geosdi.geoplatform.exception.IllegalParameterFault;
import org.geosdi.geoplatform.gui.global.GeoPlatformException;
import org.geosdi.geoplatform.gui.global.security.GPRole;
import org.geosdi.geoplatform.gui.global.security.IGPUserDetail;
import org.geosdi.geoplatform.gui.server.ISecurityService;
import org.geosdi.geoplatform.gui.server.SessionUtility;
import org.geosdi.geoplatform.gui.server.converter.GPUserConverter;
import org.geosdi.geoplatform.gui.utility.GPSessionTimeout;
import org.geosdi.geoplatform.gui.utility.UserLoginEnum;
import org.geosdi.geoplatform.responce.collection.GuiComponentsPermissionMapData;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
@Service("securityService")
public class SecurityService implements ISecurityService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private GeoPlatformService geoPlatformServiceClient;
    private GPUserConverter userConverter;
    @Autowired
    private SessionUtility sessionUtility;

    @Override
    public IGPUserDetail userLogin(String userName, String password,
            HttpServletRequest httpServletRequest) throws GeoPlatformException {
        GPUser user = null;
        List<String> roles = null;
        List<GPUserProjects> listProjects = null;
        GuiComponentsPermissionMapData guiComponemtPermission;
        try {
            user = geoPlatformServiceClient.getUserDetailByUsernameAndPassword(
                    userName, password);

            roles = geoPlatformServiceClient.getUserAuthorities(user.getId());

            guiComponemtPermission = geoPlatformServiceClient.getUserGuiComponentVisible(
                    user.getId());

            listProjects = geoPlatformServiceClient.getUserProjectsByUserId(user.getId());
        } catch (ResourceNotFoundFault ex) {
            logger.error("SecurityService",
                    "Unable to find user with username: " + userName + " Error: " + ex);
            throw new GeoPlatformException(
                    "Unable to find user with username: " + userName);
        } catch (SOAPFaultException ex) {
            logger.error(
                    "Error on SecurityService: " + ex + " password incorrect");
            throw new GeoPlatformException("Password incorrect");
        } catch (IllegalParameterFault ilg) {
            logger.error(
                    "Error on SecurityService: " + ilg);
            throw new GeoPlatformException("Parameter incorrect");
        }
        GPProject project = null;
        if (listProjects == null || listProjects.isEmpty()) {
            GPUserProjects userProject = new GPUserProjects();
            project = new GPProject();
            project.setName("Default Project");
            project.setShared(false);
            userProject.setUserAndProject(user, project);
            userProject.setPermissionMask(BasePermission.ADMINISTRATION.getMask());
            this.saveProject(userProject);
        } else {
            project = listProjects.get(0).getProject();
        }
        this.storeUserAndProjectInSession(user, project, httpServletRequest);

        IGPUserDetail userDetail = this.userConverter.convertUserToDTO(user);

        for (String role : roles) {
            if (role.equals(GPRole.VIEWER.toString())) {
                userDetail.setViewer(true);
            }
        }

        userDetail.setComponentPermission(
                guiComponemtPermission.getGuiComponentsPermissionMap());

        return userDetail;
    }

    private void storeUserAndProjectInSession(GPUser user, GPProject defaultProject,
            HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        //TODO: Set the right time in seconds before session interrupt
        session.setMaxInactiveInterval(900);
        session.setAttribute(UserLoginEnum.USER_LOGGED.toString(), user);
        session.setAttribute(UserLoginEnum.DEFAULT_PROJECT.toString(), defaultProject);
    }

    public GPUser loginFromSessionServer(HttpServletRequest httpServletRequest)
            throws GeoPlatformException {
        GPUser user = null;
        try {
            user = this.sessionUtility.getUserAlreadyFromSession(httpServletRequest);
        } catch (GPSessionTimeout timeout) {
            throw new GeoPlatformException(timeout);
        }
        return user;
    }

    private void deleteUserFromSession(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        session.removeAttribute(UserLoginEnum.USER_LOGGED.toString());
    }

    @Override
    public void invalidateSession(HttpServletRequest httpServletRequest) throws GeoPlatformException {
        //deleteUserFromSession(httpServletRequest);
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @PostConstruct
    public void createConverter() {
        this.userConverter = new GPUserConverter();
    }

    /**
     * @param geoPlatformServiceClient the geoPlatformServiceClient to set
     */
    @Autowired
    public void setGeoPlatformServiceClient(
            @Qualifier("geoPlatformServiceClient") GeoPlatformService geoPlatformServiceClient) {
        this.geoPlatformServiceClient = geoPlatformServiceClient;
    }

    private void saveProject(GPUserProjects userProject) throws GeoPlatformException {
        try {
            this.geoPlatformServiceClient.saveProject(userProject.getUser().getUsername(),
                    userProject.getProject());
        } catch (ResourceNotFoundFault rnf) {
            this.logger.error("Failed to save project on SecurityService: " + rnf);
            throw new GeoPlatformException(rnf);
        } catch (IllegalParameterFault ilg) {
            logger.error(
                    "Error on SecurityService: " + ilg);
            throw new GeoPlatformException("Parameter incorrect on saveProject");
        }
    }
}
