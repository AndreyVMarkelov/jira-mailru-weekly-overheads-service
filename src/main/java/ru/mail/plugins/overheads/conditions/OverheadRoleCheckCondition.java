/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.conditions;


import java.util.Collection;
import java.util.List;

import ru.mail.plugins.overheads.ao.OverheadRolesService;
import ru.mail.plugins.overheads.entities.OverheadRoles;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractJiraCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.roles.DefaultProjectRoleManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleAndActorStore;
import com.atlassian.jira.user.UserProjectHistoryManager;


public class OverheadRoleCheckCondition extends AbstractJiraCondition implements IOverheadRoleCheckCondition
{
    private static final ProjectRoleAndActorStore projectRoleAndActorStore = ComponentManager
        .getComponentInstanceOfType(ProjectRoleAndActorStore.class);
    private static final UserProjectHistoryManager userProjectHistoryManager = ComponentManager
        .getComponentInstanceOfType(UserProjectHistoryManager.class);

    private final OverheadRolesService overheadRolesService;

    private final DefaultProjectRoleManager roleManager;

    public OverheadRoleCheckCondition(OverheadRolesService overheadRolesService)
    {
        this.overheadRolesService = overheadRolesService;
        this.roleManager = new DefaultProjectRoleManager(projectRoleAndActorStore);
    }

    @Override
    public boolean shouldDisplay(User user, JiraHelper jiraHelper)
    {
        Project currentProject = userProjectHistoryManager.getCurrentProject(Permissions.BROWSE, user);

        Collection<ProjectRole> userRoles = roleManager.getProjectRoles(user, currentProject);
        List<OverheadRoles> recs;

        for (ProjectRole role : userRoles)
        {
            recs = overheadRolesService.getRecordsByMaster(role.getId());
            if (recs != null && recs.size() > 0)
            {
                return true;
            }
        }

        return false;
    }
}
