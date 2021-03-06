/*
 * Created by Dmitry Miroshnichenko 04-12-2012. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.settings;


import java.util.Date;

import ru.mail.plugins.overheads.common.Consts;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;


public class PluginSettingsManager implements IPluginSettingsManager
{
    private final PluginSettingsFactory pluginSettingsFactory;

    public static final String TASK_ISSUE_KEY = "taskissue";
    public static final String CF_QA_ID_KEY = "cfqaid";
    public static final String CF_ASSIGNEE_ID_KEY = "cfassigneeid";
    public static final String CF_FEATUREGOAL_ID_KEY = "cffeaturegoalid";
    public static final String CF_SEVERITY_ID_KEY = "cfseverityid";
    public static final String USER_ADDRESSEE = "addressee";

    public static final String JOB_LAST_RUN_KEY = "joblastrun";
    public static final String FORCE_NOTIFICATION_KEY = "forcenotification";

    public PluginSettingsManager(
        final PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void setValue(final String key, final String value)
    {
        final PluginSettings settings = pluginSettingsFactory
            .createSettingsForKey(Consts.PLUGIN_KEY_MAILRU_OVERHEADS);
        settings.put(key, value);
    }

    @Override
    public Object getValue(final String key)
    {
        final PluginSettings settings = pluginSettingsFactory
            .createSettingsForKey(Consts.PLUGIN_KEY_MAILRU_OVERHEADS);
        return settings.get(key);
    }

    @Override
    public void setTaskIssue(String issue)
    {
        setValue(TASK_ISSUE_KEY, issue);
    }

    @Override
    public String getTaskIssue()
    {
        return (String) getValue(TASK_ISSUE_KEY);
    }

    @Override
    public void setQaCFId(String cfId)
    {
        setValue(CF_QA_ID_KEY, cfId);
    }

    @Override
    public String getQaCFId()
    {
        return (String) getValue(CF_QA_ID_KEY);
    }

    @Override
    public void setAssigneeCFId(String cfId)
    {
        setValue(CF_ASSIGNEE_ID_KEY, cfId);
    }
    
    @Override
    public String getAssigneeCFId()
    {
        return (String) getValue(CF_ASSIGNEE_ID_KEY);
    }
    
    @Override
    public void setFeatureGoalCFId(String cfId)
    {
        setValue(CF_FEATUREGOAL_ID_KEY, cfId);
    }
    
    @Override
    public String getFeatureGoalCFId()
    {
        return (String) getValue(CF_FEATUREGOAL_ID_KEY);
    }

    @Override
    public void setSeverityCFId(String cfId)
    {
        setValue(CF_SEVERITY_ID_KEY, cfId);
    }
    
    @Override
    public String getSeverityCFId()
    {
        return (String) getValue(CF_SEVERITY_ID_KEY);
    }

    @Override
    public String getAddressee()
    {
        return (String) getValue(USER_ADDRESSEE);
    }

    @Override
    public void setAddressee(String user)
    {
        setValue(USER_ADDRESSEE, user);
    }

    @Override
    public void setJobLastRun(Date date)
    {
        String dateRepresentation;

        if (date == null)
        {
            dateRepresentation = null;
        }
        else
        {
            dateRepresentation = String.valueOf(date.getTime());
        }
        setValue(JOB_LAST_RUN_KEY, dateRepresentation);
    }

    @Override
    public Date getJobLastRun()
    {
        Object storedObject = getValue(JOB_LAST_RUN_KEY);
        if (storedObject instanceof String)
        {
            return new Date(Long.valueOf((String) storedObject));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setForceNotification(boolean flag)
    {
        setValue(FORCE_NOTIFICATION_KEY, String.valueOf(flag));
    }

    @Override
    public boolean isForceNotification()
    {
        return Boolean.valueOf((String) getValue(FORCE_NOTIFICATION_KEY));
    }
}
