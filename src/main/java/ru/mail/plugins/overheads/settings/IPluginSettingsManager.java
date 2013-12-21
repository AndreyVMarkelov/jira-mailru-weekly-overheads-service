/*
 * Created by Dmitry Miroshnichenko 04-12-2012. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.settings;

import java.util.Date;


public interface IPluginSettingsManager
{

    public void setValue(final String key, final String value);

    public Object getValue(final String key);

    public void setTaskIssue(String issue);

    public String getTaskIssue();

    public void setJobLastRun(Date date);
    
    public Date getJobLastRun();
}
