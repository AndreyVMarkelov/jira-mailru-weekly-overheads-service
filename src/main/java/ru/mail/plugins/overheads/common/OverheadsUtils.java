/*
 * Created by Dmitry Miroshnichenko 30-11-2012. Copyright Mail.Ru Group 2012.
 * All rights reserved.
 */
package ru.mail.plugins.overheads.common;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.ComponentManager;
import com.atlassian.plugin.PluginAccessor;


public class OverheadsUtils
{
    private static final String CONSTANTS_MANAGER_CLASS_NAME = "ru.mail.jira.plugins.settings.IMailRuConstantsManager";
    private static final String CONSTANTS_MANAGER_METHOD_GET_CONSTANTS_BY_KEY_NAME = "getConstantByKey";
    private static Object constantsManagerInstance;
    
//    public static final Logger log = LoggerFactory.getLogger(OverheadsUtils.class);
    
    public static String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":"
            + req.getServerPort() + req.getContextPath());
    }
    
    private static Object getConstantsManagerClass()
    {
        if (constantsManagerInstance == null)
        {
            PluginAccessor pluginAccessor = ComponentManager.getInstance().getPluginAccessor();
            Class<?> mailRuConstantsManagerClass;
            try
            {
                mailRuConstantsManagerClass = pluginAccessor.getClassLoader().loadClass(CONSTANTS_MANAGER_CLASS_NAME);
            }
            catch (ClassNotFoundException e)
            {
//                log.info("Utils::getConstantsManagerClass - ClassNotfoundException " + CONSTANTS_MANAGER_CLASS_NAME
//                    + " not found. It is possible that plugin is turned off");
                return null;
            }
            constantsManagerInstance = ComponentManager.getOSGiComponentInstanceOfType(mailRuConstantsManagerClass);
            if (constantsManagerInstance == null)
            {
//                log.info("Utils::getConstantsManagerClass - Class " + CONSTANTS_MANAGER_CLASS_NAME
//                    + ". Method getOSGiComponentInstanceOfType failed to load component");
            }
        }
        return constantsManagerInstance;
    }

    public static String getConstantValue(String constantKey)
    {
        String value = getExternalStoredConstant(constantKey, CONSTANTS_MANAGER_METHOD_GET_CONSTANTS_BY_KEY_NAME, "getExternalStoredConstant");
        if (value == null)
        {
//            log.error("Utils::getConstantValue - null was returned during getting constant value with key = " + constantKey);
        }

        return value;
    }

    private static String getExternalStoredConstant(String constantKey, String externalMethodName, String internalMethodName)
    {
        Object constantsManager = getConstantsManagerClass();

        if (constantsManager == null)
        {
//            log.error("Utils::getStoredConstant - Constants Manager wasn't loaded properly");
            return null;
        }

        Method[] methods = constantsManager.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (externalMethodName.equals(methods[i].getName()))
            {
                String result = null;
                try
                {
                    result = (String) methods[i].invoke(constantsManager, constantKey);
                }
                catch (IllegalArgumentException e)
                {
//                    log.error(getErrorMessage(internalMethodName, externalMethodName, "IllegalArgumentException", constantsManager.getClass()
//                        .getName()));
                }
                catch (IllegalAccessException e)
                {
//                    log.error(getErrorMessage(internalMethodName, externalMethodName, "IllegalAccessException", constantsManager.getClass().getName()));
                }
                catch (InvocationTargetException e)
                {
//                    log.error(getErrorMessage(internalMethodName, externalMethodName, "InvocationTargetException", constantsManager.getClass()
//                        .getName()));
                    constantsManager = null; // set instance to null it's
                                             // possible that class was disabled
                }

                return result;
            }
        }
        return null;
    }

    private static String getErrorMessage(String internalMethodName, String externalMethodName, String exception, String className)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Utils::");
        sb.append(internalMethodName);
        sb.append(" - Class ");
        sb.append(className);
        sb.append(". ");
        sb.append(exception);
        sb.append(" occured invoking ");
        sb.append(externalMethodName);
        sb.append(" method ");

        return sb.toString();
    }


    public static boolean isValidStr(String str)
    {
        return (str != null && str.length() > 0);
    }

    public static boolean isEmpty(Collection<?> collection)
    {
        return collection == null || collection.size() <= 0;
    }

    public static boolean isEmpty(Object[] array)
    {
        return array == null || array.length <= 0;
    }

    public OverheadsUtils()
    {
    }
}
