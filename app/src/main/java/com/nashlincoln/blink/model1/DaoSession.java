package com.nashlincoln.blink.model1;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig attributeTypeDaoConfig;
    private final DaoConfig attributeDaoConfig;
    private final DaoConfig deviceTypeDaoConfig;
    private final DaoConfig deviceDaoConfig;

    private final AttributeTypeDao attributeTypeDao;
    private final AttributeDao attributeDao;
    private final DeviceTypeDao deviceTypeDao;
    private final DeviceDao deviceDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        attributeTypeDaoConfig = daoConfigMap.get(AttributeTypeDao.class).clone();
        attributeTypeDaoConfig.initIdentityScope(type);

        attributeDaoConfig = daoConfigMap.get(AttributeDao.class).clone();
        attributeDaoConfig.initIdentityScope(type);

        deviceTypeDaoConfig = daoConfigMap.get(DeviceTypeDao.class).clone();
        deviceTypeDaoConfig.initIdentityScope(type);

        deviceDaoConfig = daoConfigMap.get(DeviceDao.class).clone();
        deviceDaoConfig.initIdentityScope(type);

        attributeTypeDao = new AttributeTypeDao(attributeTypeDaoConfig, this);
        attributeDao = new AttributeDao(attributeDaoConfig, this);
        deviceTypeDao = new DeviceTypeDao(deviceTypeDaoConfig, this);
        deviceDao = new DeviceDao(deviceDaoConfig, this);

        registerDao(AttributeType.class, attributeTypeDao);
        registerDao(Attribute.class, attributeDao);
        registerDao(DeviceType.class, deviceTypeDao);
        registerDao(Device.class, deviceDao);
    }
    
    public void clear() {
        attributeTypeDaoConfig.getIdentityScope().clear();
        attributeDaoConfig.getIdentityScope().clear();
        deviceTypeDaoConfig.getIdentityScope().clear();
        deviceDaoConfig.getIdentityScope().clear();
    }

    public AttributeTypeDao getAttributeTypeDao() {
        return attributeTypeDao;
    }

    public AttributeDao getAttributeDao() {
        return attributeDao;
    }

    public DeviceTypeDao getDeviceTypeDao() {
        return deviceTypeDao;
    }

    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

}