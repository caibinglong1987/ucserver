package com.roamtech.uc.jainsip;

import com.roamtech.uc.client.rxevent.PurchaseDataTrafficEvent;
import com.roamtech.uc.jainsip.rxevent.KickOutEvent;
import com.roamtech.uc.jainsip.rxevent.SendMessageEvent;
import com.roamtech.uc.model.MsgPublish;
import com.roamtech.uc.repository.MsgPublishRepository;
import com.roamtech.uc.util.RxBus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import javax.sip.*;
import java.text.ParseException;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin03 on 2016/8/27.
 */
@Component("ucSystemMsgClient")
public class UCSystemMsgClient implements MessageProcessor, InitializingBean {
    private static final Logger LOG = LoggerFactory
            .getLogger(UCSystemMsgClient.class);
    private SipLayer sipLayer;
    private String username;
    private String displayName="络漫科技";
    private String sipIp;
    private Integer port;
    private String proxy;
    private String welcome="欢迎你使用漫话，我们将竭诚为你服务。我们作为全球互联网+移动通信领域的创新公司，致力于打造一张全新的全球互联网-移动通信网络。同时依托络漫全球通信云平台、络漫专利智能通讯硬件，为你带来全新的全球免费漫游通讯体验及综合性的全方位出境服务。如果在使用过程中有任何问题或建议，记得给我们反馈哦。";
    private String publicproxy;
    @Autowired
    MsgPublishRepository msgRepo;
    public void processMessage(String sender, String message) {
        LOG.info("From " +
                sender + ": " + message);
    }

    public void processError(String errorMessage) {
        LOG.warn("ERROR: " +
                errorMessage);
    }

    public void processInfo(String infoMessage) {
        LOG.info(
                infoMessage);
    }
    private String buildSipUri(String username,String domain,String userid) {
        return "sip:" + username + "@" + domain + ";to=" + username + ";userid=" + userid;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        sipLayer = new SipLayer(username, sipIp, port);
        sipLayer.setMessageProcessor(this);
        LOG.info(displayName);
        LOG.info(welcome);
        Observable<SendMessageEvent> smeSubject = RxBus.getInstance().register(SendMessageEvent.class);
        smeSubject.observeOn(Schedulers.io()).delay(3, TimeUnit.SECONDS).subscribe(new Action1<SendMessageEvent>() {
            @Override
            public void call(SendMessageEvent sme) {
                try {
                    List<MsgPublish> msgs = msgRepo.findByType(sme.getType());
                    for(MsgPublish msg:msgs) {
                        String from = StringUtils.isBlank(sme.getFrom()) ? msg.getCaller() : sme.getFrom();
                        String callername = StringUtils.isBlank(sme.getDisplayname()) ? msg.getCallerName():sme.getDisplayname();
                        String message = StringUtils.isNotBlank(sme.getMessage()) ? sme.getMessage() : msg.getMessage();
                        getSipLayer().sendMessage(callername,
                                buildSipUri(from,publicproxy,sme.getUserid()),
                                buildSipUri(sme.getTo(),proxy,sme.getUserid()),
                                message);
                    }
                } catch (ParseException e) {
                    LOG.warn("ParseException",e);
                } catch (InvalidArgumentException e) {
                    LOG.warn("InvalidArgumentException",e);
                } catch (SipException e) {
                    LOG.warn("SipException",e);
                }
            }
        });
        Observable<KickOutEvent> koeSubject = RxBus.getInstance().register(KickOutEvent.class);
        koeSubject.observeOn(Schedulers.io()).subscribe(new Action1<KickOutEvent>() {
            @Override
            public void call(KickOutEvent koe) {
                try {
                    getSipLayer().sendMessage(koe.getDisplayname(),
                            buildSipUri(koe.getFrom(),publicproxy,koe.getUserid()),
                            buildSipUri(koe.getTo(),proxy,koe.getUserid()),
                            koe.getMessage());
                } catch (ParseException e) {
                    LOG.warn("ParseException",e);
                } catch (InvalidArgumentException e) {
                    LOG.warn("InvalidArgumentException",e);
                } catch (SipException e) {
                    LOG.warn("SipException",e);
                }
            }
        });
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSipIp() {
        return sipIp;
    }

    public void setSipIp(String sipIp) {
        this.sipIp = sipIp;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setSipLayer(SipLayer sipLayer) {
        this.sipLayer = sipLayer;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public SipLayer getSipLayer() {
        return sipLayer;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }


    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public String getPublicproxy() {
        return publicproxy;
    }

    public void setPublicproxy(String publicproxy) {
        this.publicproxy = publicproxy;
    }

    /*
    public UCSystemMsgClient(String username, String ip, Integer port) {
        try {
            sipLayer = new SipLayer(username, ip, port);
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
        } catch (TransportNotSupportedException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ObjectInUseException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        sipLayer.setMessageProcessor(this);
    }
    public static void main(String[] args) {
        UCSystemMsgClient client = new UCSystemMsgClient("roamtech", "192.168.0.4", 5060);
        try {
            client.sipLayer.sendMessage("络漫科技","sip:roamtech@120.55.192.228;to=roamtech;userid=1","sip:13967124998@120.55.192.228;to=roamtech;userid=1","欢迎你使用漫话，我们将竭诚为你服务。我们作为全球互联网+移动通信领域的创新公司，致力于打造一张全新的全球互联网-移动通信网络。同时依托络漫全球通信云平台、络漫专利智能通讯硬件，为你带来全新的全球免费漫游通讯体验及综合性的全方位出境服务。如果在使用过程中有任何问题或建议，记得给我们反馈哦。");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (SipException e) {
            e.printStackTrace();
        }
    }
    */
}