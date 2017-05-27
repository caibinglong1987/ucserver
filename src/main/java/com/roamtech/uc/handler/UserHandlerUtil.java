package com.roamtech.uc.handler;

import com.roamtech.uc.cache.handler.SessionHandler;
import com.roamtech.uc.cache.model.Session;
import com.roamtech.uc.handler.services.ErrorCode;
import com.roamtech.uc.jainsip.rxevent.KickOutEvent;
import com.roamtech.uc.jainsip.rxevent.SendMessageEvent;
import com.roamtech.uc.model.Application;
import com.roamtech.uc.model.Phone;
import com.roamtech.uc.model.User;
import com.roamtech.uc.repository.UserRepository;
import com.roamtech.uc.util.ProfileUtils;
import com.roamtech.uc.util.RxBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by admin03 on 2016/9/22.
 */
public class UserHandlerUtil {
    private static final Logger LOG = LoggerFactory.getLogger(UserHandlerUtil.class);
    private static SessionHandler sessionHandler;
    private static UserRepository userRepo;
    public static void setRepos(SessionHandler sessionHandler,UserRepository userRepo) {
        UserHandlerUtil.sessionHandler = sessionHandler;
        UserHandlerUtil.userRepo = userRepo;
    }

    public static User findByUsernameRoam(String username) {
        return userRepo.findByUserNameAndTenantIdAndPhoneType(username, User.ROAM_TENANT_ID, Phone.TYPE_PHONE);
    }

    public static List<User> findByUsername(String username) {
        return userRepo.findByUserName(username);
    }

    public static User findByUsernameAndTenantId(String username, Long tenantId) {
        return userRepo.findByUserNameAndTenantIdAndPhoneType(username, tenantId, Phone.TYPE_PHONE);
    }

    public static User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public static User findByPhone(String phone) {
        return userRepo.findByPhoneAndTenantIdAndPhoneType(phone, User.ROAM_TENANT_ID, Phone.TYPE_PHONE);
    }

    public static User findOne(Long userId) {
        return userRepo.findOne(userId);
    }

    public static User findActiveOne(String key) {
        User user = null;
        if(ProfileUtils.isPhoneNumberValid(key)) {
            LOG.info("call findByPhone "+key);
            user = findByPhone(key);
        } else if(ProfileUtils.isEmail(key)) {
            LOG.info("call findByEMail "+key);
            user = findByEmail(key);
        } else {
            LOG.info("call findByUsernameRoam("+key+")");
            try{
                user = findByUsernameRoam(key);
                LOG.info("findByUsernameRoam("+key+")="+user);
            } catch(Exception e){
                LOG.warn("findByUsernameRoam failed",e);
            }
        }

        return user;
    }

    public static User save(User user) {
        user = userRepo.save(user);
        if (user.getTenantId() == 1) {
            RxBus.getInstance().post(new SendMessageEvent(null, user.getPhone(), null, null, user.getUserId() + "", SendMessageEvent.NEWUSER_WELCOME));
        }
        return user;
    }
    public static int changePassword(Long userId, String opassword, String npassword) {
        User userObj = findOne(userId);
        if(null == userObj) {
            LOG.info("changePassword failed for cann't find HSID ");
            return ErrorCode.ERR_ACCOUNT_NOEXIST;
        }
        if(opassword.equals(npassword)) {
            return ErrorCode.ERR_PASSWD_SAME;
        }
        if(!userObj.getPassword().equals(opassword)){//MD5Utils.generateValue(opassword))){
            return ErrorCode.ERR_PASSWD_MISMATCH;
        }
        userObj.setPassword(npassword);
        save(userObj);
        return 0;
    }
    public static int resetPassword(Long userId, String password) {
        User userObj = findOne(userId);
        if(null == userObj) {
            LOG.info("resetPassword failed for cann't find HSID ");
            return ErrorCode.ERR_ACCOUNT_NOEXIST;
        }

        userObj.setPassword(password);
        save(userObj);
        return 0;
    }
    public static int changeMobile(Long userId, String mobile) {
        User userObj = findOne(userId);
        if(null == userObj) {
            LOG.info("changeMobile failed for cann't find USER ");
            return ErrorCode.ERR_ACCOUNT_NOEXIST;
        }

        userObj.setPhone(mobile);
        save(userObj);
        return 0;
    }

    public static int changeEmail(Long userId, String email) {
        User userObj = findOne(userId);
        if(null == userObj) {
            LOG.info("changeEmail failed for cann't find USER ");
            return ErrorCode.ERR_ACCOUNT_NOEXIST;
        }

        userObj.setEmail(email);
        save(userObj);
        return 0;
    }

    public static Session login(User user, Application application) {
        Session session = new Session();
        session.setCreateTime(new Date().getTime());
        session.setUserId(user.getUserId());
        session.setSessionId(UUID.randomUUID().toString());
        session.setUsertype(user.getType());
        session.setTenantId(user.getTenantId());
        if (application != null) {
            session.setPackageName(application.getPackageName());
            session.setBundleId(application.getBundleId());
        }
        List<Session> sessions = sessionHandler.findByUserId(user.getUserId());
        if(!sessions.isEmpty()) {
            String phone = null;
            if (user.getTenantId() != 1) {
                phone = user.getTenantId() + "_" + user.getPhone();
            } else {
                phone = user.getPhone();
            }
            RxBus.getInstance().post(new KickOutEvent(phone,user.getUserId()+"",session.getSessionId()));
        }
        return sessionHandler.save(session);
        //return session;
    }

    public static void logout(Long userId,String sessionId) {
        Session session = new Session();
        session.setUserId(userId);
        session.setSessionId(sessionId);
        sessionHandler.delete(session);
    }
    public static User freeze(Long userId) {
        User userObj = findOne(userId);
        if(null == userObj) {
            LOG.info("freeze failed for cann't find USER ");
            return null;
        }

        userObj.setStatus(User.STATUS_FROZEN);;
        return save(userObj);
    }
    public static User unfreeze(Long userId) {
        User userObj = findOne(userId);
        if(null == userObj) {
            LOG.info("unfreeze failed for cann't find USER ");
            return null;
        }

        userObj.setStatus(User.STATUS_ACTIVE);;
        return save(userObj);
    }
    public static void upgradeUsers(Long userId) {
        User user = findOne(userId);
        if(null != user && user.getCreateDate() == null) {
            List<User> updateUsers = new ArrayList<User>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Iterable<User> users = userRepo.findAll();
            for(User iuser:users) {
                if(iuser.getCreateDate() == null) {
                    String date = sdf.format(iuser.getCreateTime());
                    try {
                        iuser.setCreateDate(sdf.parse(date));
                        updateUsers.add(iuser);
                    } catch (ParseException e) {
                        ;
                    }
                }
            }
            userRepo.save(updateUsers);
        }
    }
}
