package com.roamtech.uc.jainsip;

/**
 * Created by admin03 on 2016/8/26.
 */
public interface MessageProcessor
{
    public void processMessage(String sender, String message);
    public void processError(String errorMessage);
    public void processInfo(String infoMessage);
}
