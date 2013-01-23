/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.jobs;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import ru.mail.plugins.overheads.ao.OverheadValueSetService;
import ru.mail.plugins.overheads.common.Utils;
import ru.mail.plugins.overheads.entities.UsersOverhead;
import ru.mail.plugins.overheads.settings.PluginSettingsManager;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.sal.api.scheduling.PluginJob;


public class MailRuOverheadTask implements PluginJob
{
    private final Logger logger = Logger.getLogger(MailRuOverheadTask.class);

    @Override
    public void execute(Map<String, Object> jobDataMap)
    {
        final MailRuOverheadsMonitorImpl monitor = (MailRuOverheadsMonitorImpl) jobDataMap
            .get(MailRuOverheadsMonitorImpl.KEY);

        if (monitor == null)
        {
            logger
                .error("MailRuOverheadTask::execute - MailRuOverheadsMonitorImpl is null");

            return;
        }

        if (!monitor.isInited())
        {
            monitor.setInited(true);
            logger
                .info("MailRuOverheadTask::execute - First initialization run. Job will not be executed");
        }
        else
        {
            Date currentDate = new Date();
            executeJob(monitor, currentDate);
        }
    }

    private void executeJob(final MailRuOverheadsMonitorImpl monitor,
        Date currentDate)
    {
        logger
            .info("MailRuOverheadTask::execute - Job: MailRuOverheadTask started at "
                + currentDate);

        createOverheadTasks(monitor.getOverheadValueSetService(),
            monitor.getSettings());
        monitor.getSettings().setJobLastRun(currentDate);

        logger
            .info("MailRuOverheadTask::execute - Job: MailRuOverheadTask finished at "
                + new Date());
    }

    private void createOverheadTasks(
        OverheadValueSetService overheadValueSetService,
        PluginSettingsManager settings)
    {
        String taskIssue = settings.getTaskIssue();
        if (Utils.isValidStr(taskIssue))
        {
            ComponentManager componentManager = ComponentManager.getInstance();
            IssueManager issueManager = componentManager.getIssueManager();
            Collection<User> allUsers = componentManager.getUserUtil()
                .getUsers();

            Calendar calendar = Calendar.getInstance();

            long startTime = calendar.getTimeInMillis();
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            if (today <= Calendar.MONDAY)
            {
                startTime += Math.abs(Calendar.MONDAY - today)
                    * MailRuOverheadsMonitorImpl.ONE_DAY_IN_MILLIS;
            }
            else
            {
                startTime += Math.abs(7 - today + Calendar.MONDAY)
                    * MailRuOverheadsMonitorImpl.ONE_DAY_IN_MILLIS;
            }

            long endTime = startTime
                + MailRuOverheadsMonitorImpl.REPEAT_INTERVAL
                - MailRuOverheadsMonitorImpl.ONE_DAY_IN_MILLIS;
            StringBuilder sb = new StringBuilder();
            sb.append("[week ");
            sb.append(formatDate(new Date(startTime)));
            sb.append('-');
            sb.append(formatDate(new Date(endTime)));
            sb.append("] Недельные оверхеды");

            for (User user : allUsers)
            {
                UsersOverhead overhead = overheadValueSetService
                    .getRecordByUsername(user.getName());
                if (overhead != null)
                {
                    Long overheadValue = overhead.getOverhead();

                    if (overheadValue != null && overheadValue > 0)
                    {
                        MutableIssue newIssue = componentManager
                            .getIssueFactory().cloneIssue(
                                issueManager.getIssueObject(taskIssue));

                        newIssue.setSummary(sb.toString());
                        newIssue.setDescription(sb.toString());
                        newIssue.setAssignee(user);
                        newIssue.setReporter(componentManager.getUserUtil()
                            .getUser(overhead.getQaName()));
                        newIssue.setEstimate(overheadValue);

                        try
                        {
                            issueManager.createIssueObject(user, newIssue);
                            logger
                                .info("MailRuOverheadTask::createOverheadTasks - Overhead issue created successfully");
                        }
                        catch (CreateException e)
                        {
                            logger
                                .error("MailRuOverheadTask::createOverheadTasks - Failed to create issue object");
                        }
                    }
                }
            }
        }
        else
        {
            logger
                .error("MailRuOverheadTask::createOverheadTasks - Missing template task issue");
        }
    }

    public String formatDate(Date date)
    {
        if (date != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy",
                ComponentManager.getInstance().getJiraAuthenticationContext()
                    .getLocale());
            return sdf.format(date);
        }
        else
        {
            return "";
        }
    }
}