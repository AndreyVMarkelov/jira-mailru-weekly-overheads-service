/*
 * Created by Dmitry Miroshnichenko 09-01-2013. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.ao;


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import ru.mail.plugins.overheads.entities.OverheadRoles;

import com.atlassian.activeobjects.external.ActiveObjects;


public final class OverheadRolesServiceImpl implements OverheadRolesService
{
    private final ActiveObjects ao;

    public OverheadRolesServiceImpl(ActiveObjects ao)
    {
        this.ao = checkNotNull(ao);
    }

    @Override
    public OverheadRoles addRecord(Long masterId, Long detailId)
    {
        final OverheadRoles record = ao.create(OverheadRoles.class);

        record.setMasterRole(masterId);
        record.setDetailRole(detailId);
        record.save();

        return record;
    }

    @Override
    public List<OverheadRoles> getAllRecords()
    {
        return newArrayList(ao.find(OverheadRoles.class));
    }

    @Override
    public List<OverheadRoles> getRecordsByMaster(Long master)
    {
        return newArrayList(ao.find(OverheadRoles.class, "MASTER_ROLE = ?",
            master));
    }

    @Override
    public void removeRecord(OverheadRoles ent)
    {
        ao.delete(ent);
    }

    @Override
    public void removeRecords(List<OverheadRoles> entities)
    {
        for (OverheadRoles entity : entities)
        {
            removeRecord(entity);
        }
    }

}
