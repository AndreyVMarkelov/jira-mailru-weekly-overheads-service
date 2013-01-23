/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.ao;


import java.util.List;

import ru.mail.plugins.overheads.entities.OverheadRoles;

import com.atlassian.activeobjects.tx.Transactional;


@Transactional
public interface OverheadRolesService
{
    OverheadRoles addRecord(Long master, Long detail);

    void removeRecord(OverheadRoles ent);

    void removeRecords(List<OverheadRoles> entities);

    List<OverheadRoles> getAllRecords();

    List<OverheadRoles> getRecordsByMaster(Long master);
}
