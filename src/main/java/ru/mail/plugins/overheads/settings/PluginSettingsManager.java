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
    public static final String USER_ADDRESSEE = "addressee";

    public static final String JOB_LAST_RUN_KEY = "joblastrun";

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
}
