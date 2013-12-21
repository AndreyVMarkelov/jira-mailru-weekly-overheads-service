package ru.mail.plugins.overheads.conditions;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;

public interface IOverheadRoleCheckCondition
{
    public boolean shouldDisplay(User user, JiraHelper jiraHelper);
}
