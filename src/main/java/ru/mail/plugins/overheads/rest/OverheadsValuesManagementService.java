/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.rest;


import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ru.mail.plugins.overheads.ao.OverheadRolesService;
import ru.mail.plugins.overheads.ao.OverheadValueSetService;
import ru.mail.plugins.overheads.common.Utils;
import ru.mail.plugins.overheads.entities.OverheadRoles;
import ru.mail.plugins.overheads.entities.UsersOverhead;

import com.atlassian.core.util.InvalidDurationException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.DefaultProjectRoleManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleAndActorStore;
import com.atlassian.jira.user.UserProjectHistoryManager;
import com.atlassian.jira.util.I18nHelper;


@Path("/overheadvalssrv")
public class OverheadsValuesManagementService
{
    private final Logger log = Logger.getLogger(OverheadsValuesManagementService.class);
    private static final ProjectRoleAndActorStore projectRoleAndActorStore = ComponentManager
        .getComponentInstanceOfType(ProjectRoleAndActorStore.class);
    private static final UserProjectHistoryManager userProjectHistoryManager = ComponentManager
        .getComponentInstanceOfType(UserProjectHistoryManager.class);

    private final DefaultProjectRoleManager roleManager;
    private final OverheadRolesService overheadRolesService;
    private final OverheadValueSetService overheadValueSetService;

    public OverheadsValuesManagementService(OverheadRolesService overheadRolesService, OverheadValueSetService overheadValueSetService)
    {
        this.roleManager = new DefaultProjectRoleManager(projectRoleAndActorStore);
        this.overheadRolesService = checkNotNull(overheadRolesService);
        this.overheadValueSetService = checkNotNull(overheadValueSetService);
    }

    @GET
    @Path("/changeval")
    @Produces({MediaType.TEXT_HTML})
    public Response changeVal(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadsValuesManagementService::changeval - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }

        String username = req.getParameter("username");
        String overhead = req.getParameter("overhead");
        String qa = req.getParameter("qa");

        if (!Utils.isValidStr(username))
        {
            log.error("OverheadsValuesManagementService::changeval - Invalid params");
            return Response.ok(i18n.getText("mailru.service.invalid.params")).status(400).build();
        }

        Project currentProject = userProjectHistoryManager.getCurrentProject(Permissions.BROWSE, user);

        boolean isUsernameValid = false;
        Collection<ProjectRole> projectRoles = roleManager.getProjectRoles(user, currentProject);
        User userParam = ComponentManager.getInstance().getUserUtil().getUser(username);

        outerloop: for (ProjectRole projectRole : projectRoles)
        {
            List<OverheadRoles> overheadRoles = overheadRolesService.getRecordsByMaster(projectRole.getId());
            for (OverheadRoles entity : overheadRoles)
            {
                if (roleManager.isUserInProjectRole(userParam, roleManager.getProjectRole(entity.getDetailRole()), currentProject))
                {
                    isUsernameValid = true;
                    break outerloop;
                }
            }
        }

        if (!isUsernameValid)
        {
            log.error("OverheadsValuesManagementService::changeval - Access denied for this user or username is invalid");
            return Response.ok(i18n.getText("mailru.service.invalid.param.username")).status(400).build();
        }

        Long overheadValue = null;
        if (overhead == null || "".equals(overhead))
        {
            overheadValue = Long.valueOf(0);
        }
        else
        {
            try
            {
                overheadValue = ComponentManager.getInstance().getJiraDurationUtils().parseDuration(overhead, authCtx.getLocale());
            }
            catch (InvalidDurationException e1)
            {
                log.error("OverheadsValuesManagementService::changeval - Invalid overhead format");
                return Response.ok(i18n.getText("mailru.service.invalid.param.overhead.format")).status(400).build();
            }
        }

        User qaUser = ComponentManager.getInstance().getUserUtil().getUser(qa);
        String qaValue;
        if (qaUser != null)
        {
            qaValue = qa;
        }
        else
        {
            qaValue = null;
        }

        UsersOverhead usersOverhead = overheadValueSetService.getRecordByUsername(username);
        if (usersOverhead == null)
        {
            overheadValueSetService.addRecord(username, overheadValue, qaValue);
        }
        else
        {
            usersOverhead.setQaName(qaValue);
            usersOverhead.setOverhead(overheadValue);
            usersOverhead.save();
        }

        String referrer = req.getHeader("referer");
        URI uri;
        try
        {
            uri = new URI(referrer);
        }
        catch (URISyntaxException e)
        {
            log.error("OverheadsValuesManagementService::changeval - Invalid uri");
            return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
        }

        return Response.seeOther(uri).build();
    }
  
    @GET
    @Path("/getoverheaddata")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getOverheadData(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadsValuesManagementService::getOverheadData - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        
        String username = req.getParameter("username");
        
        User aUser = ComponentAccessor.getUserManager().getUser(username);
        if (aUser == null)
        {
            log.error("OverheadsValuesManagementService::getOverheadData - Invalid params");
            return Response.ok(i18n.getText("mailru.service.invalid.params")).status(400).build();
        }
        
        UsersOverhead usersOverhead = overheadValueSetService.getRecordByUsername(username);
        OverheadDataEntity ent = new OverheadDataEntity();
        if (usersOverhead != null)
        {
            User qa = ComponentAccessor.getUserManager().getUser(usersOverhead.getQaName());
            if (qa != null)
            {
                ent.setQa(qa.getDisplayName());
            }
            if (usersOverhead.getOverhead() != null)
            {
                ent.setTime(ComponentManager.getInstance().getJiraDurationUtils().getFormattedDuration(usersOverhead.getOverhead(), authCtx.getLocale()));
            }
        }
        
        return Response.ok(ent).build();
    }
}
