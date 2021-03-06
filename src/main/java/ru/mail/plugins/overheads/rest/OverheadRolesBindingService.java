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
import ru.mail.plugins.overheads.common.OverheadsUtils;
import ru.mail.plugins.overheads.entities.OverheadRoles;
import ru.mail.plugins.overheads.settings.PluginSettingsManager;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.DefaultProjectRoleManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleAndActorStore;
import com.atlassian.jira.util.I18nHelper;


@Path("/overheadrolesrv")
public class OverheadRolesBindingService
{
    private final Logger log = Logger.getLogger(OverheadRolesBindingService.class);
    private static final ProjectRoleAndActorStore projectRoleAndActorStore = ComponentManager
        .getComponentInstanceOfType(ProjectRoleAndActorStore.class);

    private final DefaultProjectRoleManager roleManager;
    private final OverheadRolesService overheadRolesService;
    private final PluginSettingsManager settings;

    public OverheadRolesBindingService(OverheadRolesService overheadRolesService, PluginSettingsManager settings)
    {
        this.roleManager = new DefaultProjectRoleManager(projectRoleAndActorStore);
        this.overheadRolesService = checkNotNull(overheadRolesService);
        this.settings = settings;
    }

    @GET
    @Path("/settask")
    @Produces({MediaType.TEXT_HTML})
    public Response setTask(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::setTask - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::setTask - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }

        String taskTemplate = req.getParameter("task");

        if (!OverheadsUtils.isValidStr(taskTemplate) || ComponentManager.getInstance().getIssueManager().getIssueObject(taskTemplate) != null)
        {
            settings.setTaskIssue(taskTemplate);

            String referrer = req.getHeader("referer");
            URI uri;
            try
            {
                uri = new URI(referrer);
            }
            catch (URISyntaxException e)
            {
                log.error("OverheadRolesBindingService::setTask - Invalid uri");
                return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
            }

            return Response.seeOther(uri).build();
        }
        else
        {
            log.error("OverheadRolesBindingService::setTask - Invalid task param");
            return Response.ok(i18n.getText("mailru.service.invalid.param.task")).status(400).build();
        }
    }

    @GET
    @Path("/setqaid")
    @Produces({MediaType.TEXT_HTML})
    public Response setQaId(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::setQaId - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::setQaId - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }

        String qaCfId = req.getParameter("qaid");

        if (ComponentManager.getInstance().getCustomFieldManager().getCustomFieldObject(qaCfId) != null)
        {
            settings.setQaCFId(qaCfId);

            String referrer = req.getHeader("referer");
            URI uri;
            try
            {
                uri = new URI(referrer);
            }
            catch (URISyntaxException e)
            {
                log.error("OverheadRolesBindingService::setQaId - Invalid uri");
                return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
            }

            return Response.seeOther(uri).build();
        }
        else
        {
            log.error("OverheadRolesBindingService::setQaId - Invalid qaid param");
            return Response.ok(i18n.getText("mailru.service.invalid.param.qaid")).status(400).build();
        }
    }

    @GET
    @Path("/setassigneeid")
    @Produces({MediaType.TEXT_HTML})
    public Response setAssigneeId(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::setAssigneeId - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::setAssigneeId - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }
        
        String assigneeCfId = req.getParameter("assigneeid");
        
        if (ComponentManager.getInstance().getCustomFieldManager().getCustomFieldObject(assigneeCfId) != null)
        {
            settings.setAssigneeCFId(assigneeCfId);
            
            String referrer = req.getHeader("referer");
            URI uri;
            try
            {
                uri = new URI(referrer);
            }
            catch (URISyntaxException e)
            {
                log.error("OverheadRolesBindingService::setAssigneeId - Invalid uri");
                return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
            }
            
            return Response.seeOther(uri).build();
        }
        else
        {
            log.error("OverheadRolesBindingService::setAssigneeId - Invalid assigneeid param");
            return Response.ok(i18n.getText("mailru.service.invalid.param.assigneid")).status(400).build();
        }
    }
    
    @GET
    @Path("/setfeaturegoalid")
    @Produces({MediaType.TEXT_HTML})
    public Response setFeatureGoalId(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::setFeatureGoalId - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::setFeatureGoalId - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }
        
        String featuregoalCfId = req.getParameter("featuregoalid");
        
        if (ComponentManager.getInstance().getCustomFieldManager().getCustomFieldObject(featuregoalCfId) != null)
        {
            settings.setFeatureGoalCFId(featuregoalCfId);
            
            String referrer = req.getHeader("referer");
            URI uri;
            try
            {
                uri = new URI(referrer);
            }
            catch (URISyntaxException e)
            {
                log.error("OverheadRolesBindingService::setFeatureGoalId - Invalid uri");
                return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
            }
            
            return Response.seeOther(uri).build();
        }
        else
        {
            log.error("OverheadRolesBindingService::setFeatureGoalId - Invalid featuregoal param");
            return Response.ok(i18n.getText("mailru.service.invalid.param.featuregoalid")).status(400).build();
        }
    }
    @GET
    @Path("/setseverityid")
    @Produces({MediaType.TEXT_HTML})
    public Response setSeverityId(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::setSeverityId - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::setSeverityId - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }
        
        String severityCfId = req.getParameter("severityid");
        
        if (ComponentManager.getInstance().getCustomFieldManager().getCustomFieldObject(severityCfId) != null)
        {
            settings.setSeverityCFId(severityCfId);
            
            String referrer = req.getHeader("referer");
            URI uri;
            try
            {
                uri = new URI(referrer);
            }
            catch (URISyntaxException e)
            {
                log.error("OverheadRolesBindingService::setSeverityId - Invalid uri");
                return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
            }
            
            return Response.seeOther(uri).build();
        }
        else
        {
            log.error("OverheadRolesBindingService::setSeverityId - Invalid severityid param");
            return Response.ok(i18n.getText("mailru.service.invalid.param.severityid")).status(400).build();
        }
    }

    @GET
    @Path("/setaddressee")
    @Produces({MediaType.TEXT_HTML})
    public Response setAddressee(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::setAddressee - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::setAddressee - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }

        String addressee = req.getParameter("addressee");

        if (ComponentManager.getInstance().getUserUtil().getUserObject(addressee) != null)
        {
            settings.setAddressee(addressee);

            String referrer = req.getHeader("referer");
            URI uri;
            try
            {
                uri = new URI(referrer);
            }
            catch (URISyntaxException e)
            {
                log.error("OverheadRolesBindingService::setAddressee - Invalid uri");
                return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
            }

            return Response.seeOther(uri).build();
        }
        else
        {
            log.error("OverheadRolesBindingService::setAddressee - Invalid user");
            return Response.ok(i18n.getText("mailru.service.invalid.param.addressee")).status(400).build();
        }
    }

    @GET
    @Path("/bind")
    @Produces({MediaType.TEXT_HTML})
    public Response bind(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadRolesBindingService::bind - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged")).status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager().hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadRolesBindingService::bind - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin")).status(403).build();
        }

        String masterRole = req.getParameter("masterrole");
        String[] detailRoles = req.getParameterValues("detailroles");

        if (!isValidRole(masterRole))
        {
            log.error("OverheadRolesBindingService::bind - Invalid param master role");
            return Response.ok(i18n.getText("mailru.service.invalid.param.master")).status(400).build();
        }
        if (!isValidRoles(detailRoles))
        {
            log.error("OverheadRolesBindingService::bind - Invalid params detail roles");
            return Response.ok(i18n.getText("mailru.service.invalid.param.detail")).status(400).build();
        }

        Long masterRoleId = Long.valueOf(masterRole);
        int len = detailRoles == null ? 0 : detailRoles.length;
        Long[] detailRoleIds = new Long[len];
        for (int i = 0; i < len; i++)
        {
            detailRoleIds[i] = Long.valueOf(detailRoles[i]);
        }

        List<OverheadRoles> storedRoles = overheadRolesService.getRecordsByMaster(masterRoleId);

        Collection<ProjectRole> allRoles = roleManager.getProjectRoles();

        for (ProjectRole role : allRoles)
        {
            Long roleId = role.getId();
            OverheadRoles rec = getRecordWithDetail(storedRoles, roleId);
            boolean inputHasRole = constainsRole(detailRoleIds, roleId);

            if (rec == null && inputHasRole)
            {
                overheadRolesService.addRecord(masterRoleId, roleId);
            }
            else if (rec != null && !inputHasRole)
            {
                overheadRolesService.removeRecord(rec);
            }
        }

        String referrer = req.getHeader("referer");
        URI uri;
        try
        {
            uri = new URI(referrer);
        }
        catch (URISyntaxException e)
        {
            log.error("OverheadRolesBindingService::bind - Invalid uri");
            return Response.ok(i18n.getText("mailru.service.invalid.uri")).status(500).build();
        }

        return Response.seeOther(uri).build();
    }

    private boolean constainsRole(Long[] array, Long roleId)
    {
        boolean result = false;
        if (!OverheadsUtils.isEmpty(array) && roleId != null)
        {
            int i = 0;
            while (!result && i < array.length)
            {
                result = roleId.equals(array[i]);
                i++;
            }
        }
        return result;
    }

    private OverheadRoles getRecordWithDetail(List<OverheadRoles> records, Long detail)
    {
        OverheadRoles result = null;
        if (!OverheadsUtils.isEmpty(records) && detail != null)
        {
            for (OverheadRoles rec : records)
            {
                if (detail.equals(rec.getDetailRole()))
                {
                    result = rec;
                    break;
                }
            }
        }

        return result;
    }

    private boolean isValidRole(String role)
    {
        boolean isLong = true;
        Long roleId = null;
        try
        {
            roleId = Long.valueOf(role);
        }
        catch (NumberFormatException e)
        {
            isLong = false;
        }

        return isLong && roleManager.getProjectRole(roleId) != null;
    }

    private boolean isValidRoles(String[] roles)
    {
        boolean res = true;
        if (OverheadsUtils.isEmpty(roles))
        {
            return res;
        }

        if (res)
        {
            int i = 0;
            while (res && i < roles.length)
            {
                res = isValidRole(roles[i]);
                i++;
            }
        }

        return res;
    }
}
