package ru.mail.plugins.overheads.rest;


import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ru.mail.plugins.overheads.jobs.MailRuOverheadsMonitorImpl;
import ru.mail.plugins.overheads.settings.PluginSettingsManager;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.util.I18nHelper;


@Path("/overheadcommonsrv")
public class OverheadGeneralPurposesService
{
    private final Logger log = Logger
        .getLogger(OverheadGeneralPurposesService.class);
    
    private final PluginSettingsManager settings;

    public OverheadGeneralPurposesService(PluginSettingsManager settings)
    {
        this.settings = settings;
    }
    
    @GET
    @Path("/forcejob")
    @Produces({MediaType.TEXT_HTML})
    public Response forceJob(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("OverheadGeneralPurposesService::forceJob - User is not logged");
            return Response.ok(i18n.getText("mailru.service.user.notlogged"))
                .status(401).build();
        }
        if (!ComponentManager.getInstance().getPermissionManager()
            .hasPermission(Permissions.ADMINISTER, user))
        {
            log.error("OverheadGeneralPurposesService::forceJob - User is not admin");
            return Response.ok(i18n.getText("mailru.service.user.notadmin"))
                .status(403).build();
        }

        MailRuOverheadsMonitorImpl jobMonitor = MailRuOverheadsMonitorImpl.getLinkToMonitor();
        jobMonitor.onStart();
        settings.setForceNotification(true);
        
        String referrer = req.getHeader("referer");
        URI uri;
        try
        {
            uri = new URI(referrer);
        }
        catch (URISyntaxException e)
        {
            log.error("OverheadGeneralPurposesService::forceJob - Invalid uri");
            return Response.ok(i18n.getText("mailru.service.invalid.uri"))
                .status(500).build();
        }

        return Response.seeOther(uri).build();
    }
}
