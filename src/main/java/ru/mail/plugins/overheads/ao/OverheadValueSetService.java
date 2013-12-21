/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.ao;


import java.util.List;

import ru.mail.plugins.overheads.entities.UsersOverhead;

import com.atlassian.activeobjects.tx.Transactional;


@Transactional
public interface OverheadValueSetService
{
    UsersOverhead addRecord(String userName, Long overhead, String qaName);

    void removeRecord(UsersOverhead ent);

    void removeRecords(List<UsersOverhead> entities);

    UsersOverhead getRecordByUsername(String userName);

    List<UsersOverhead> getAllRecords();
}
