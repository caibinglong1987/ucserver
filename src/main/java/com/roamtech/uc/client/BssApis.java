package com.roamtech.uc.client;

import java.util.List;

import com.roamtech.uc.model.DataCard;
import com.roamtech.uc.model.DataCardTraffic;

public interface BssApis {
	void loadData();
	void registerDataCards();
	String registerDataCards(List<DataCard> datacards);
	void purchaseSurfPass(List<DataCardTraffic> dcts);
	void removePurchaseSurfPass(List<DataCardTraffic> dcts);
	// /st/json/api/GetAuthTokenAPI
	String GetAuthTokenAPI(String userName, String password);
	// (/st/json/api/PingAPI)
	String PingAPI(String echo);
	// (/st/json/api/batch/async/RegisterUsersBatchAsyncAPI)
	String RegisterUsersBatchAsyncAPI(List<BssUser> users);
	// (/st/json/api/batch/async/UpdateUsersBatchAsyncAPI)
	String UpdateUsersBatchAsyncAPI(List<BssUser> users);
	// (/st/json/api/GetUsersAPI)
	List<BssUserSummary> GetUsersAPI(String imsi,String msisdn,Integer offset,Integer limit);
	//GetUsersBatchAPI (/st/json/api/batch/GetUsersBatchAPI)
	List<BssUser> GetUsersBatchAPI(List<String> identifiers);
	// (/st/json/api/batch/async/SuspendUsersBatchAsyncAPI)
	String SuspendUsersBatchAsyncAPI(List<String> identifiers);
	// (/st/json/api/batch/async/ResumeUsersBatchAsyncAPI)
	String ResumeUsersBatchAsyncAPI(List<String> identifiers);
	// (/st/json/api/batch/GetUserPurchasesBatchAPI)
	List<UserPurchaseData> GetUserPurchasesBatchAPI(List<String> identifiers);
	// (/st/json/api/batch/GetUserVolumeDataBatchAPI)
	List<UserVolumeDataResponse> GetUserVolumeDataBatchAPI(List<UserVolumeDataRequest> identifiers);
	// (/st/json/api/batch/GetUserSessionLogsBatchAPI)
	List<UserSessionLogData> GetUserSessionLogsBatchAPI(List<String> identifiers);
	// (/st/json/api/batch/GetTransactionsBatchAPI)
	List<TransactionData> GetTransactionsBatchAPI(Boolean detailedTransactionLog, List<String> identifiers);
	// (/st/json/api/batch/async/RemoveUserPurchasesBatchAsyncAPI)
	String RemoveUserPurchasesBatchAsyncAPI(List<RemoveUserPurchaseRequest> identifiers);
	// (/st/json/api/batch/async/UpdateUserPurchasesBatchAsyncAPI)
	String UpdateUserPurchasesBatchAsyncAPI(List<UpdateUserPurchaseRequest> identifiers);
	// (/st/json/api/GetSurfPassesAPI)
	List<ExtraPass> GetExtraPassesAPI(String userIdentifier);
	// (/st/json/api/batch/async/PurchaseSurfPassesBatchAsyncAPI)
	String PurchaseExtraPassesBatchAsyncAPI(List<PurchaseExtraPassRequest> surfPassReqs);
	// (/st/json/api/async/GetAsyncBatchJobStatusAPI)
	BatchJobStatus GetAsyncBatchJobStatusAPI(String batchJobToken,Integer offset,Integer limit,List<String> identifiers);

	void testHKApis();
}
