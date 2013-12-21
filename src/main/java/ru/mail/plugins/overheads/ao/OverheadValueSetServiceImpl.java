/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.ao;


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import ru.mail.plugins.overheads.entities.UsersOverhead;

import com.atlassian.activeobjects.external.ActiveObjects;


public class OverheadValueSetServiceImpl implements OverheadValueSetService
{
    private final ActiveObjects ao;

    public OverheadValueSetServiceImpl(ActiveObjects ao)
    {
        this.ao = checkNotNull(ao);
    }

    @Override
    public UsersOverhead addRecord(String userName, Long overhead, String qaName)
    {
        final UsersOverhead record = ao.create(UsersOverhead.class);

        record.setUserName(userName);
        record.setOverhead(overhead);
        record.setQaName(qaName);
        record.save();

        return record;
    }

    @Override
    public void removeRecord(UsersOverhead ent)
    {
        ao.delete(ent);
    }

    @Override
    public void removeRecords(List<UsersOverhead> entities)
    {
        for (UsersOverhead entity : entities)
        {
            removeRecord(entity);
        }
    }

    @Override
    public UsersOverhead getRecordByUsername(String userName)
    {
        UsersOverhead[] recs = ao.find(UsersOverhead.class, "USER_NAME = ?",
            userName);
        if (recs != null && recs.length > 0)
        {
            if (recs.length > 1)
            {
                throw new IllegalArgumentException(
                    "params count should be <= 1");
            }
            return recs[0];
        }
        else
        {
            return null;
        }
    }

    @Override
    public List<UsersOverhead> getAllRecords()
    {
        return newArrayList(ao.find(UsersOverhead.class));
    }

}
