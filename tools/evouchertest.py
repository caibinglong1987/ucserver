# coding=utf-8
import json
import httplib2
import argparse

class EvoucherGenerator(object):
    def __init__(self, host, user_agent):
        self.host = host
        self.client = httplib2.Http(disable_ssl_certificate_validation=True)#httplib.HTTPConnection(host, port, timeout=10)
        self.headers = {"Content-Type": "application/json; charset=utf-8","User-Agent": user_agent}
        self.evouchersns = []

    def send(self, service, query_params):
        uri = "http://"+self.host+service
        print uri
        return self.client.request(uri,"POST", query_params, self.headers)

    def sendssl(self, service, query_params):
        uri = "https://"+self.host+service
        print uri
        return self.client.request(uri,"POST", query_params, self.headers)
    def admin_login(self):
        query_params = {'username':'roamadmin','password':'Roamingdata2016'}
        resp,content = self.sendssl("/uc/services/login", json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
            else:
                print str(jsonresp["userid"])+","+jsonresp["sessionid"]
                self.adminuserid = jsonresp["userid"]
                self.adminsessionid = jsonresp["sessionid"]
            return jsonresp["error_no"]     
    def user_login(self, username, password):
        query_params = {'username':username,'password':password}
        resp,content = self.sendssl("/uc/services/login", json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
            else:
                print str(jsonresp["userid"])+","+jsonresp["sessionid"]
                self.userid = jsonresp["userid"]
                self.sessionid = jsonresp["sessionid"]
            return jsonresp["error_no"]
    def evoucher_create_talktime(self):
        query_params = {'userid':self.adminuserid,'sessionid':self.adminsessionid,'evoucher':{'name':'通话时长券','description':'价值40分钟通话时长电子券','effect_datetime':'2016-10-26 00:00:00','failure_datetime':'2016-12-31 23:59:59','money':'40','type':2,'background':'images/201610/evoucher_imgs/kq_voice.png','logo':'images/201610/evoucher_imgs/kq_other.png','location':'evoucher'},'productids':[3]}
        resp,content = self.sendssl('/om/services/evoucher_create', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["evoucher"]
                self.evoucher = jsonresp["result"]["evoucher"]
                return jsonresp["result"]["evoucher"]
            return None
        else:
            return None
    def evoucher_create_voicenumber(self):
        query_params = {'userid':self.adminuserid,'sessionid':self.adminsessionid,'evoucher':{'name':'专属云号码券','description':'价值5天专属云号码使用权','effect_datetime':'2016-10-26 00:00:00','failure_datetime':'2016-12-31 23:59:59','money':'5','type':2,'background':'images/201610/evoucher_imgs/kq_other.png','logo':'images/201610/evoucher_imgs/kq_other.png','location':'evoucher'},'productids':[6]}
        resp,content = self.sendssl('/om/services/evoucher_create', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["evoucher"]
                self.evoucher = jsonresp["result"]["evoucher"]
                return jsonresp["result"]["evoucher"]
            return None
        else:
            return None
    def evoucher_create_coupon(self):
        query_params = {'userid':self.adminuserid,'sessionid':self.adminsessionid,'evoucher':{'name':'代金券','description':'价值50元代金券','effect_datetime':'2016-12-29 00:00:00','failure_datetime':'2017-01-31 23:59:59','money':'50.0','type':3,'background':'images/201610/evoucher_imgs/kq_other.png','logo':'images/201610/evoucher_imgs/kq_other.png','location':'onlinecoupon'}}
        resp,content = self.sendssl('/om/services/evoucher_create', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["evoucher"]
                self.evoucher = jsonresp["result"]["evoucher"]
                return jsonresp["result"]["evoucher"]
            return None
        else:
            return None         
    def evouchersn_generate(self, evoucherid, quantity):
        resp,content = self.sendssl('/om/services/evouchersn_generate', json.JSONEncoder(ensure_ascii = True).encode({'userid':self.adminuserid,'sessionid':self.adminsessionid,'evoucherid':evoucherid,'quantity':quantity}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["evoucher_sns"]
                for sn in jsonresp["result"]["evoucher_sns"]:
                    self.evouchersns.append(sn)
                return jsonresp["result"]["evoucher_sns"]
            return None
        else:
            return None
    def evouchersn_exchange(self, evoucher_sn):
        resp,content = self.sendssl('/uc/services/evouchersn_exchange', json.JSONEncoder(ensure_ascii = True).encode({'userid':self.userid,'sessionid':self.sessionid,'evoucher_sn':evoucher_sn}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["evoucher"]
                return jsonresp["result"]["evoucher"]
            return None
        else:
            return None 
    def evoucher_gets(self):
        resp,content = self.sendssl('/uc/services/evoucher_gets', json.JSONEncoder(ensure_ascii = True).encode({'userid':self.userid,'sessionid':self.sessionid}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["evouchers"]
                return jsonresp["result"]["evouchers"]
            return None
        else:
            return None             
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='DataCard Storing from a csv file')
    parser.add_argument('--host', default='www.roam-tech.com', type=str, help='DataCard Storing Server Host')
    parser.add_argument('--username', default='zhengsj', type=str, help='uc server normal user username')
    parser.add_argument('--password', default='1234567', type=str, help='uc server normal user password')
    parser.add_argument('--voucher', default='talktime', type=str, help='uc server normal user password')
    parser.add_argument('--useragent', default='RoamPhone/Tconfigtool', type=str, help='http User-Agent header')
    args = parser.parse_args()
    evGen = EvoucherGenerator(args.host,args.useragent)
    error_no = evGen.admin_login()
    if error_no == 0:
        #evoucher = evGen.evoucher_create_talktime() if args.voucher=='talktime' else evGen.evoucher_create_voicenumber()
        evoucher = evGen.evoucher_create_coupon()
        if evoucher != None:
            sns = evGen.evouchersn_generate(evoucher["id"],1);
            if sns != None:
                error_no = evGen.user_login(args.username,args.password)
                if error_no == 0:
                    evGen.evouchersn_exchange(sns[0])
                    evGen.evoucher_gets()
        
        '''sns = evGen.evouchersn_generate('8',10);
        evGen.evouchersn_generate('18',10);
        evGen.evouchersn_generate('20',10);'''
        '''if sns != None:
            error_no = evGen.user_login(args.username,args.password)
            if error_no == 0:
                evGen.evouchersn_exchange(sns[0])
                evGen.evoucher_gets()'''                   
