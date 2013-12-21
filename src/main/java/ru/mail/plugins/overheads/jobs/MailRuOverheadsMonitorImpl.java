/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.jobs;


import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import ru.mail.plugins.overheads.ao.OverheadValueSetService;
import ru.mail.plugins.overheads.settings.PluginSettingsManager;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;


public class MailRuOverheadsMonitorImpl implements LifecycleAware
{
    private final Logger logger = Logger
        .getLogger(MailRuOverheadsMonitorImpl.class);

    static final String KEY = MailRuOverheadsMonitorImpl.class.getName()
        + ":instance";
    private static final String JOB_NAME = MailRuOverheadsMonitorImpl.class
        .getName() + ":job";
    static final long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24; // one day
    static final long REPEAT_INTERVAL = ONE_DAY_IN_MILLIS * 7; // one
                                                               // week

    private final PluginScheduler pluginScheduler;
    private final OverheadValueSetService overheadValueSetService;
    private final PluginSettingsManager settings;

    private boolean isInited = false;

    private static MailRuOverheadsMonitorImpl jobLink;

    public MailRuOverheadsMonitorImpl(PluginScheduler pluginScheduler,
        OverheadValueSetService overheadValueSetService,
        PluginSettingsManager settings)
    {
        this.pluginScheduler = pluginScheduler;
        this.overheadValueSetService = overheadValueSetService;
        this.settings = settings;
        jobLink = this;
    }

    public static MailRuOverheadsMonitorImpl getLinkToJob()
    {
        return jobLink;
    }

    public void onStart()
    {
        logger.info("MailRuOverheadsMonitorImpl::onStart - Started at:"
            + new Date());
        Date runDate = settings.getJobLastRun();
        if (runDate == null)
        {
            runDate = new Date();
            logger
                .info("MailRuOverheadsMonitorImpl::onStart - This is first run at: "
                    + runDate);
        }
        else
        {
            logger.info("MailRuOverheadsMonitorImpl::onStart - Last run date:"
                + runDate);
        }

        pluginScheduler.scheduleJob(JOB_NAME, MailRuOverheadTask.class,
            new HashMap<String, Object>()
            {
                {
                    put(KEY, MailRuOverheadsMonitorImpl.this);
                }
            }, runDate, REPEAT_INTERVAL);
    }

    public boolean isInited()
    {
        return isInited;
    }

    public void setInited(boolean isInited)
    {
        this.isInited = isInited;
    }

    public OverheadValueSetService getOverheadValueSetService()
    {
        return overheadValueSetService;
    }

    public PluginSettingsManager getSettings()
    {
        return settings;
    }
}
