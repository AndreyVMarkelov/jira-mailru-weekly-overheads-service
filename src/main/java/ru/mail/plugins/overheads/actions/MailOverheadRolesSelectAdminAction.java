/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.actions;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import ru.mail.plugins.overheads.ao.OverheadRolesService;
import ru.mail.plugins.overheads.entities.OverheadRoles;
import ru.mail.plugins.overheads.settings.PluginSettingsManager;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.DefaultProjectRoleManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleAndActorStore;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;


public class MailOverheadRolesSelectAdminAction extends JiraWebActionSupport
{
    private LinkedHashMap<ProjectRole, ArrayList<ProjectRole>> roles = new LinkedHashMap<ProjectRole, ArrayList<ProjectRole>>();

    private final OverheadRolesService overheadRolesService;

    private final DefaultProjectRoleManager roleManager;

    private final ApplicationProperties applicationProperties;

    private final String taskTemplate;
    private final String qaId;
    private final String addressee;
    private final Date jobLastRun;

    public MailOverheadRolesSelectAdminAction(
        OverheadRolesService overheadRolesService,
        ApplicationProperties applicationProperties,
        PluginSettingsManager settings)
    {
        ProjectRoleAndActorStore projectRoleAndActorStore = ComponentManager
            .getComponentInstanceOfType(ProjectRoleAndActorStore.class);
        this.overheadRolesService = checkNotNull(overheadRolesService);
        roleManager = new DefaultProjectRoleManager(projectRoleAndActorStore);
        this.applicationProperties = applicationProperties;
        taskTemplate = settings.getTaskIssue();
        qaId = settings.getQaCFId();
        addressee = settings.getAddressee();
        jobLastRun = settings.getJobLastRun();

        cleanStoredRecords();

        List<OverheadRoles> storedRecs = overheadRolesService.getAllRecords();
        for (OverheadRoles rec : storedRecs)
        {
            Long masterStoredId = rec.getMasterRole();
            Long detailStoredId = rec.getDetailRole();

            if (masterStoredId != null && detailStoredId != null)
            {
                ArrayList<ProjectRole> detailStoredIds = roles.get(roleManager
                    .getProjectRole(masterStoredId));
                if (detailStoredIds == null)
                {
                    detailStoredIds = new ArrayList<ProjectRole>();
                    detailStoredIds.add(roleManager
                        .getProjectRole(detailStoredId));
                    roles.put(roleManager.getProjectRole(masterStoredId),
                        detailStoredIds);
                }
                else
                {
                    detailStoredIds.add(roleManager
                        .getProjectRole(detailStoredId));
                }
            }
        }
    }

    private void cleanStoredRecords()
    {
        List<OverheadRoles> storedRecs = overheadRolesService.getAllRecords();
        Collection<ProjectRole> allRoles = roleManager.getProjectRoles();
        List<Long> allRolesIds = new ArrayList<Long>();
        for (ProjectRole role : allRoles)
        {
            allRolesIds.add(role.getId());
        }

        for (OverheadRoles rec : storedRecs)
        {
            if (!allRolesIds.contains(rec.getMasterRole())
                || !allRolesIds.contains(rec.getDetailRole()))
            {
                overheadRolesService.removeRecord(rec);
            }
        }
    }

    public boolean hasAdminPermission()
    {
        User user = getLoggedInUser();
        if (user == null)
        {
            return false;
        }

        if (getPermissionManager().hasPermission(Permissions.ADMINISTER,
            getLoggedInUser()))
        {
            return true;
        }

        return false;
    }

    public LinkedHashMap<ProjectRole, ArrayList<ProjectRole>> getRoles()
    {
        return roles;
    }

    public Collection<ProjectRole> getAllRoles()
    {
        return roleManager.getProjectRoles();
    }

    public String getBaseUrl()
    {
        return applicationProperties.getBaseUrl();
    }

    public String getTaskTemplate()
    {
        return taskTemplate;
    }

    public String getQaId()
    {
        return qaId;
    }

    public Date getJobLastRun()
    {
        return jobLastRun;
    }

    public String getAddressee()
    {
        return addressee;
    }
}
