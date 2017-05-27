package com.roamtech.uc.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by admin03 on 2016/8/17.
 */
public class RxBus {
    private static RxBus instance;
    private Map<Class<?>,Vector<Subject>> subjectMap = new HashMap<Class<?>,Vector<Subject>>();

    private RxBus() {
    }

    public static synchronized RxBus getInstance() {
        if (null == instance) {
            instance = new RxBus();
        }
        return instance;
    }

    public synchronized <T> Observable<T> register(Class<?> type) {
        Subject<T, T> subject = PublishSubject.create();
        Vector<Subject> listeners = subjectMap.get(type);
        if(listeners == null) {
            listeners = new Vector<Subject>();
        }
        listeners.add(subject);
        subjectMap.put(type,listeners);
        return subject;
    }

    public synchronized void unregister(Class<?> type,Subject subject) {
        Vector<Subject> listeners = subjectMap.get(type);
        if(listeners != null) {
            listeners.remove(subject);
        }
    }

    public void post(Object content) {
        synchronized (this) {
            Vector<Subject> listeners;
            if(content instanceof List) {
                listeners = subjectMap.get(((List)content).get(0).getClass());
            } else {
                listeners = subjectMap.get(content.getClass());
            }
            if(listeners != null) {
                for (Subject subject : listeners) {
                    if (subject != null) {
                        subject.onNext(content);
                    }
                }
            }
        }
    }

}
