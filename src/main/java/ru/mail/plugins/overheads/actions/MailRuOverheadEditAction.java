/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.actions;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.mail.plugins.overheads.ao.OverheadRolesService;
import ru.mail.plugins.overheads.ao.OverheadValueSetService;
import ru.mail.plugins.overheads.common.Consts;
import ru.mail.plugins.overheads.entities.OverheadRoles;
import ru.mail.plugins.overheads.entities.UsersOverhead;
import ru.mail.plugins.overheads.structures.UserOverheadData;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.DefaultProjectRoleManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleAndActorStore;
import com.atlassian.jira.user.UserProjectHistoryManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;


public class MailRuOverheadEditAction extends JiraWebActionSupport
{
    private static final ProjectRoleAndActorStore projectRoleAndActorStore = ComponentManager
        .getComponentInstanceOfType(ProjectRoleAndActorStore.class);
    private static final UserProjectHistoryManager userProjectHistoryManager = ComponentManager
        .getComponentInstanceOfType(UserProjectHistoryManager.class);

    private static final long serialVersionUID = -1472852852068396225L;

    private static final String DEFAULT_OVERHEAD = "0";

    private Map<User, UserOverheadData> overheads = new LinkedHashMap<User, UserOverheadData>();

    private final OverheadValueSetService overheadValueSetService;

    private final DefaultProjectRoleManager roleManager;

    private final ApplicationProperties applicationProperties;

    private final List<User> allUsers;
    
    private List<User> qaUsers;

    private class UserComparator implements Comparator<User>
    {
        @Override
        public int compare(User o1, User o2)
        {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    }

    public MailRuOverheadEditAction(OverheadRolesService overheadRolesService, OverheadValueSetService overheadValueSetService,
        ApplicationProperties applicationProperties)
    {
        this.overheadValueSetService = checkNotNull(overheadValueSetService);
        this.roleManager = new DefaultProjectRoleManager(projectRoleAndActorStore);
        this.applicationProperties = applicationProperties;
        Set<User> userSet = ComponentAccessor.getUserManager().getAllUsers();
        List<User> userList = new ArrayList<User>(userSet.size());
        for (User user : userSet)
        {
            userList.add(user);
        }
        Collections.sort(userList, new UserComparator());
        this.allUsers = userList;

        cleanStoredRecords();

        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        User user = authCtx.getLoggedInUser();
        if (user == null) // should never occur
        {
            log.error("MailRuOverheadEditAction:: User is not logged. Try to fix plugin.xml");
            addErrorMessage("mailru.overheads.edit.errors.user.login");
            return;
        }

        Project currentProject = userProjectHistoryManager.getCurrentProject(Permissions.BROWSE, authCtx.getLoggedInUser());

        Collection<ProjectRole> userRoles = roleManager.getProjectRoles(user, currentProject);
        List<User> displayUsers = new LinkedList<User>();
        for (ProjectRole userRole : userRoles)
        {
            List<OverheadRoles> roles = overheadRolesService.getRecordsByMaster(userRole.getId());

            for (OverheadRoles overheadRoles : roles)
            {
                ProjectRole detailRole = roleManager.getProjectRole(overheadRoles.getDetailRole());
                for (User aUser : allUsers)
                {
                    if (roleManager.isUserInProjectRole(aUser, detailRole, currentProject))
                    {
                        displayUsers.add(aUser);
                    }
                }
            }
        }
        Collections.sort(displayUsers, new UserComparator());
        
        initQAUsersSet();

        for (User displayUser : displayUsers)
        {
            UsersOverhead overhead = overheadValueSetService.getRecordByUsername(displayUser.getName());
            UserOverheadData data = new UserOverheadData();
            if (overhead != null)
            {
                data.setQaName(overhead.getQaName());
                data.setOverhead(ComponentManager.getInstance().getJiraDurationUtils()
                    .getShortFormattedDuration(overhead.getOverhead(), authCtx.getLocale()));
            }
            else
            {
                data.setQaName(null);
                data.setOverhead(DEFAULT_OVERHEAD);
            }
            overheads.put(displayUser, data);
        }
    }

    private void cleanStoredRecords()
    {
        List<UsersOverhead> allRecs = overheadValueSetService.getAllRecords();
        UserUtil userUtil = ComponentManager.getInstance().getUserUtil();

        for (UsersOverhead usersOverhead : allRecs)
        {
            if (userUtil.getUserObject(usersOverhead.getUserName()) == null)
            {
                overheadValueSetService.removeRecord(usersOverhead);
            }
        }
    }

    private void initQAUsersSet()
    {
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        String[] groups = Consts.getConstant("A1_USERS_GROUPS").split(",", -1);
        if (groups != null && groups.length > 0)
        {
            Collection<String> groupz = new HashSet<String>(groups.length);

            for (int i = 0; i < groups.length; i++)
            {
                Group gr = groupManager.getGroup(groups[i]);
                if (gr != null)
                {
                    groupz.add(gr.getName());
                }
            }

            qaUsers = new LinkedList<User>(ComponentAccessor.getUserUtil().getUsersInGroupNames(groupz));
            Collections.sort(qaUsers, new UserComparator());
        }
        else
        {
            qaUsers = new LinkedList<User>();
        }
    }

    public List<User> getQaUsers()
    {
        return qaUsers;
    }

    public Map<User, UserOverheadData> getOverheads()
    {
        return overheads;
    }

    public String getBaseUrl()
    {
        return applicationProperties.getBaseUrl();
    }

    public List<User> getAllUsers()
    {
        return allUsers;
    }
}
