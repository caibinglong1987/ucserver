package com.roamtech.uc.opensips.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import com.roamtech.uc.opensips.model.Acc;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccRepository extends CrudRepository<Acc, Long> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Acc> findByCallerOrCallee(String caller, String callee);

    @Query("from Acc a where a.Id > ?1 and a.caller != null and a.callee != null")
    List<Acc> findByIdGreaterThan(Long accId, Pageable pageable);

    @Query("from Acc a WHERE a.method = ?2 \n" +
            "AND ((callee like ?3 and calleeStatus =0 and tosip = 1) or \n" +
            "(userId= ?4 and status = 0) or\n" +
            " (callee like ?5 and calleeStatus = 0 and tosip = 1)) and a.Id > ?1 \n" +
            " ORDER BY a.Id desc")
    List<Acc> getMoreCallList(Long accId, String method, String phoneNumber, long userId, String touchPhone, Pageable pageable);

    @Query("from Acc a WHERE a.method = ?2 \n" +
            "AND ((callee like ?3 and calleeStatus =0 ) or \n" +
            "(userId= ?4 and status = 0) or\n" +
            " (callee like ?5 and calleeStatus = 0)) and a.Id < ?1 \n" +
            " ORDER BY a.Id desc")
    List<Acc> getLessCallList(Long accId, String method, String phoneNumber, long userId, String touchPhone, Pageable pageable);

    @Query("from Acc a where a.Id > ?1 and a.method = ?2 and a.sipCode !='200'and ((a.callee like ?3 and calleeStatus = 0) or (a.callee like ?4 and calleeStatus = 0 and toSip =1)) order by a.time desc")
    List<Acc> getMoreMissedCallList(Long accId, String method, String phoneNumber, String touchPhone, Pageable pageable);

    @Query("from Acc a where a.Id < ?1 and a.method = ?2 and a.sipCode !='200' and ((a.callee like ?3 and calleeStatus = 0) or (a.callee like ?4 and calleeStatus = 0 and toSip =1)) order by a.time desc")
    List<Acc> getLessMissedCallList(Long accId, String method, String phoneNumber, String touchPhone, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from (select 1 as tosip, '' as callid,callee,'' as from_tag,'' as to_tag,'' as method, '' as sip_code,\n" +
            "'' as sip_reason,0 as duration,'' as setuptime, '2016-01-01 01:01:01' as created,\n" +
            "0 as ms_duration,userid,'' as realdest,0 as direction,0 as `status`, message,0 as callee_status, id,caller,time from (\n" +
            " select userid, id,caller,callee,message,time from `acc` a where a.method= :method \n" +
            " and a.status =0 and caller != 'ucmsg'\n" +
            " and (callee= :loginPhone or (callee= :touchPhone and tosip=1)) and id > :accId GROUP BY caller\n" +
            " union all\n" +
            " select userid, id,callee,caller,message,time from `acc` a where a.method= :method \n" +
            " and a.status =0 and tosip = 1 and caller !='ucmsg' and callee!='ucmsg' and userid= :userId \n" +
            " and id > :accId GROUP BY callee \n" +
            " union ALL\n" +
            " select userid, id,callee,caller,message,time from `acc` a where a.method= :method \n" +
            " and a.status =0 and caller= :loginPhone and callee != :touchPhone and tosip=0 and id > :accId GROUP BY callee)\n" +
            " as a  GROUP BY caller ) as a ORDER BY id desc LIMIT :limitSize")
    List<Acc> getMoreMessageListGroupByCaller(@Param("accId") Long accId, @Param("loginPhone") String loginPhone, @Param("touchPhone") String touchPhone, @Param("userId") long userId, @Param("method") String method, @Param("limitSize") Integer limit);

    /*查询所有通话记录首页每组的最新纪录，可能会有同一个对方号码的多条记录，用于首页分组记录获取，需再处理*/
    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM acc WHERE id > :accId and method = 'INVITE' and ((userid = :userid and ((direction = 1 and status = 0) or (direction = 0 and callee_status = 0))) or (callee = :loginPhone and tosip = 1 and callee_status = 0)) ORDER BY id DESC) as a GROUP BY caller, callee ORDER BY id DESC")
    List<Acc> getAllCallGroupList(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询未接通话记录首页每组的最新纪录，用于首页分组记录获取*/
    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM acc WHERE id > :accId and method = 'INVITE' and sip_code != '200' and sip_code != '603' and callee_status = 0 and ((userid = :userid and direction = 0) or (callee = :loginPhone and tosip = 1)) ORDER BY id DESC) as a GROUP BY caller ORDER BY id DESC")
    List<Acc> getMissedCallGroupList(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询与对方号码之间的通话记录，比id大的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE id > :accId and method = 'INVITE' AND ((userid = :userid AND status = 0 AND callee = :phone) OR (caller = :phone AND (callee = :loginPhone OR userid = :userid) AND callee_status = 0 AND tosip = 1)) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getAllCallListGreaterThanId(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的通话记录，比id小的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE id < :accId and method = 'INVITE' AND ((userid = :userid AND status = 0 AND callee = :phone) OR (caller = :phone AND (callee = :loginPhone OR userid = :userid) AND callee_status = 0 AND tosip = 1)) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getAllCallListLessThanId(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的通话记录，最新的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE method = 'INVITE' AND ((userid = :userid AND status = 0 AND callee = :phone) OR (caller = :phone AND (callee = :loginPhone OR userid = :userid) AND callee_status = 0 AND tosip = 1)) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getAllCallList(@Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的未接通话记录，比id大的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE id > :accId and method = 'INVITE' and sip_code != '200' and sip_code != '603' AND callee_status = 0 AND caller = :phone AND tosip = 1 AND (userid = :userid OR callee = :loginPhone) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getMissedCallListGreaterThanId(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的未接通话记录，比id小的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE id < :accId and method = 'INVITE' and sip_code != '200' and sip_code != '603' AND callee_status = 0 AND caller = :phone AND tosip = 1 AND (userid = :userid OR callee = :loginPhone) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getMissedCallListLessThanId(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的未接通话记录，最新的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE method = 'INVITE' and sip_code != '200' and sip_code != '603' AND callee_status = 0 AND caller = :phone AND tosip = 1 AND (userid = :userid OR callee = :loginPhone) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getMissedCallList(@Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询所有消息记录首页每组的最新纪录，可能会有同一个对方号码的多条记录，用于首页分组记录获取，需再处理*/
    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT * FROM acc WHERE id > :accId and method = 'MESSAGE' and caller != 'ucmsg' and ((userid = :userid and ((direction = 1 and status = 0) or (direction = 0 and callee_status = 0))) or (callee = :loginPhone and tosip = 1 and callee_status = 0)) ORDER BY id DESC) as a GROUP BY caller, callee ORDER BY id DESC")
    List<Acc> getMessageGroupList(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询与对方号码之间的消息记录，比id大的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE id > :accId and method = 'MESSAGE' AND ((userid = :userid AND status = 0 AND callee = :phone) OR (caller = :phone AND (callee = :loginPhone OR userid = :userid) AND callee_status = 0 AND tosip = 1)) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getMessageListGreaterThanId(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的消息记录，比id小的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE id < :accId and method = 'MESSAGE' AND ((userid = :userid AND status = 0 AND callee = :phone) OR (caller = :phone AND (callee = :loginPhone OR userid = :userid) AND callee_status = 0 AND tosip = 1)) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getMessageListLessThanId(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询与对方号码之间的消息记录，最新的size条记录*/
    @Query(nativeQuery = true, value = "SELECT * FROM acc WHERE method = 'MESSAGE' AND ((userid = :userid AND status = 0 AND callee = :phone) OR (caller = :phone AND (callee = :loginPhone OR userid = :userid) AND callee_status = 0 AND tosip = 1)) ORDER BY id DESC LIMIT :limitSize")
    List<Acc> getMessageList(@Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone, @Param("limitSize") Integer size);

    /*查询大于id的未读通话数*/
    @Query(nativeQuery = true, value = "SELECT count(*) FROM acc WHERE id > :accId and method = 'INVITE' and sip_code != '200' and sip_code != '603' AND callee_status = 0 AND tosip = 1 AND ((userid = :userid AND direction = 0) OR callee = :loginPhone)")
    int countUnreadCall(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询大于time的未读通话数*/
    @Query(nativeQuery = true, value = "SELECT count(*) FROM acc WHERE time > :date and method = 'INVITE' and sip_code != '200' and sip_code != '603' AND callee_status = 0 AND tosip = 1 AND ((userid = :userid AND direction = 0) OR callee = :loginPhone)")
    int countUnreadCall(@Param("date") Date date, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询大于id的未读消息数*/
    @Query(nativeQuery = true, value = "SELECT count(*) FROM acc WHERE id > :accId and method = 'MESSAGE' and sip_code != '200' AND callee_status = 0 and caller != 'ucmsg' AND tosip = 1 AND ((userid = :userid AND direction = 0) OR callee = :loginPhone)")
    int countUnreadMessage(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询大于time的未读消息数*/
    @Query(nativeQuery = true, value = "SELECT count(*) FROM acc WHERE time > :date and method = 'MESSAGE' and sip_code != '200' AND callee_status = 0 and caller != 'ucmsg' AND tosip = 1 AND ((userid = :userid AND direction = 0) OR callee = :loginPhone)")
    int countUnreadMessage(@Param("date") Date date, @Param("userid") Long userId, @Param("loginPhone") String loginPhone);

    /*查询大于id的与某个号码的未读消息数*/
    @Query(nativeQuery = true, value = "SELECT count(*) FROM acc WHERE id > :accId and method = 'MESSAGE' and sip_code != '200' AND callee_status = 0 AND caller = :phone AND tosip = 1 AND (userid = :userid OR callee = :loginPhone)")
    int countUnreadMessage(@Param("accId") Long accId, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone);

    /*查询大于time的与某个号码的未读消息数*/
    @Query(nativeQuery = true, value = "SELECT count(*) FROM acc WHERE time > :date and method = 'MESSAGE' and sip_code != '200' AND callee_status = 0 AND caller = :phone AND tosip = 1 AND (userid = :userid OR callee = :loginPhone)")
    int countUnreadMessage(@Param("date") Date date, @Param("userid") Long userId, @Param("loginPhone") String loginPhone, @Param("phone") String phone);

    @Query(nativeQuery = true, value = "select 1 as tosip, '' as callid,'' as callee,'' as from_tag,'' as to_tag,\n" +
            "'' as method, '' as sip_code, '' as sip_reason,0 as duration,'' as setuptime, '2016-01-01 01:01:01' as created,\n" +
            "0 as ms_duration,userid,'' as realdest,0 as direction,0 as `status`,0 as callee_status, message, id,caller,time\n" +
            " from ( select userid, id,caller,message,time from `acc` a where a.id < :accId and a.method= :method \n" +
            "and tosip=1 and callee= :loginPhone \n" +
            "and a.status =0  GROUP BY caller \n" +
            "union (select userid, id,callee as caller,message, time from `acc` a where a.id < :accId \n" +
            "and a.method= :method and a.userid= :userId and callee != :loginPhone  and a.status =0 GROUP BY callee )) \n" +
            "as b GROUP BY caller ORDER BY id desc limit :limitSize")
    List<Acc> getLessMessageListGroupByCaller(@Param("accId") Long accId, @Param("loginPhone") String loginPhone, @Param("userId") long userId, @Param("method") String method, @Param("limitSize") Integer limit);

    @Query(nativeQuery = true, value = "select a.tosip, a.callid,a.callee,a.from_tag,a.to_tag,a.method,a.sip_code,a.sip_reason,a.duration,a.setuptime,a.created,a.ms_duration,\n" +
            "a.userid,a.realdest,a.direction,a.status,a.callee_status, message, id,caller,time from acc a where \n" +
            "            (a.method = :method and userid = :userId and ((callee =:toPhoneNumber and status=0) or (caller=:toPhoneNumber and tosip=1 and callee_status=0)) and status = 0 and id < :accId)\n" +
            "            or\n" +
            "            (a.method = :method and caller =:toPhoneNumber and callee= :phoneNumber and callee_status=0 and tosip = 1 and id < :accId) \n" +
            "order by id desc LIMIT :limitSize")
    List<Acc> getLessMessageList(@Param("accId") Long accId, @Param("method") String method, @Param("userId") long userId, @Param("phoneNumber") String phoneNumber, @Param("toPhoneNumber") String toPhoneNumber, @Param("limitSize") Integer limit);

    @Query(nativeQuery = true, value = "select a.tosip,a.callid,a.callee,a.from_tag,a.to_tag,a.method,a.sip_code,a.sip_reason,a.duration,a.setuptime,a.created,a.ms_duration,\n" +
            "a.userid,a.realdest,a.direction,a.status,a.callee_status, message, id,caller,time from acc a where \n" +
            "            (a.method = :method and userid = :userId and caller !='ucmsg' and ((callee =:toPhoneNumber and status=0) or (caller=:toPhoneNumber and tosip=1 and callee_status=0))  and id > :accId)\n" +
            "            or\n" +
            "            (a.method = :method and caller =:toPhoneNumber and callee=:phoneNumber and tosip = 1 and callee_status=0 and id > :accId) \n" +
            "order by id desc LIMIT :limitSize")
    List<Acc> getMoreMessageList(@Param("accId") Long accId, @Param("method") String method, @Param("userId") long userId, @Param("phoneNumber") String phoneNumber, @Param("toPhoneNumber") String toPhoneNumber, @Param("limitSize") Integer limit);

    @Query(nativeQuery = true, value = "select 1 as tosip, callid,callee,'' as from_tag,'' as to_tag,'' as method, '' as sip_code,\n" +
            "'' as sip_reason,0 as duration,'' as setuptime, '2016-01-01 01:01:01' as created,\n" +
            "0 as ms_duration,userid,'' as realdest,0 as direction,0 as `status`, 0 as callee_status, message, id,caller,time  from acc where (caller= :fromPhone and userid = :userId and callee= :toPhone and method= :method and `status`=0) or \n" +
            "(caller= :toPhone and callee= :fromPhone and method= :method and tosip = 1 and callee_status = 0) or (caller = :toPhone and status = 0 and userid= :userId and method= :method and tosip = 1 ) \n" +
            "order by id desc limit 1")
    Acc getOneByFromAndToAndUserId(@Param("method") String method, @Param("fromPhone") String fromPhone, @Param("toPhone") String toPhone, @Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("update Acc a set a.status = ?2 , a.calleeStatus = ?3 where a.callId = ?1")
    int updateAccByCallId(String callId, int status, int calleeStatus);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Acc findByCallId(String callId);

    @Transactional
    @Modifying
    @Query("UPDATE Acc SET STATUS = CASE WHEN ((caller = ?1 AND callee = ?2 AND STATUS = 0) or (userid = ?3 AND STATUS = 0)) THEN 1 ELSE STATUS END,\n" +
            "callee_status =  CASE WHEN (caller = ?2 AND callee = ?1 AND callee_status = 0) THEN 1 ELSE callee_status END\n" +
            "WHERE method = 'MESSAGE'")
    int updateAccByCallerAndCallee(String myPhone, String hisPhone, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Acc SET STATUS = CASE WHEN (callee = ?1 AND userid = ?2 AND STATUS = 0) THEN 1 ELSE STATUS END,\n" +
            "callee_status =  CASE WHEN (caller = ?1  and userid= ?2 AND callee_status = 0) THEN 1 ELSE callee_status END\n" +
            "WHERE method = 'MESSAGE'")
    int updateAccByCallerAndCalleeAndUserid(String hisPhone, Long myUserid);

    /*删除与对方号码的通话记录（更新status，主叫的记录）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc set status = 1 WHERE method = 'INVITE' AND userid = ?1 AND status = 0 AND callee in (?2)")
    int updateStatusByAllCallGroup(Long userId, List<String> phones);

    /*删除与对方号码的通话记录（更新callee_status，被叫的记录）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc set callee_status = 1 WHERE method = 'INVITE' AND callee_status = 0 AND tosip = 1 AND caller in (?3) and (callee = ?2 or userid = ?1)")
    int updateCalleeStatusByAllCallGroup(Long userId, String loginPhone, List<String> phones);

    /*删除与对方号码的未接通话记录（更新callee_status，被叫的记录）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc SET callee_status = 1 WHERE method = 'INVITE' AND callee_status = 0 AND tosip = 1 AND sip_code != '200' and sip_code != '603' AND caller in (?3) AND (callee = ?2 or userid = ?1)")
    int updateCalleeStatusByMissedCallGroup(Long userId, String loginPhone, List<String> phones);

    /*删除与对方号码的消息记录（更新status，发送的消息）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc set status = 1 WHERE method = 'MESSAGE' AND userid = ?1 AND status = 0 AND callee in (?2)")
    int updateStatusByMessageGroup(Long userId, List<String> phones);

    /*删除与对方号码的消息记录（更新callee_status，接收的消息）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc set callee_status = 1 WHERE method = 'MESSAGE' AND callee_status = 0 AND tosip = 1 AND caller in (?3) and (callee = ?2 or userid = ?1)")
    int updateCalleeStatusByMessageGroup(Long userId, String loginPhone, List<String> phones);

    /*根据callid删除通话或消息记录（更新status，主叫或发送）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc set status = 1 WHERE userid = ?1 AND status = 0 and direction = 1 AND callid in (?2)")
    int updateStatusByCallIds(Long userId, List<String> callIds);

    /*根据callid删除通话或消息记录（更新callee_status，被叫或接收）*/
    @Transactional
    @Modifying
    @Query("UPDATE Acc set callee_status = 1 WHERE callee_status = 0 AND tosip = 1 AND callid in (?3) and (callee = ?2 or (userid = ?1 and direction = 0))")
    int updateCalleeStatusByCallIds(Long userId, String loginPhone, List<String> callIds);

}
