# coding=utf-8
import csv
import json
import httplib2
import argparse
import sys

class OrderTest(object):
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
            #print jsonresp
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
    def order_submit(self,productid,effect_datetime,failure_datetime,quantity,simid,areaname,genvoucher,call_duration):
        query_params = {'userid':self.userid,'sessionid':self.sessionid,'cart':{'productid':productid,'effect_datetime':effect_datetime,'failure_datetime':failure_datetime,'quantity':quantity,'simid':simid,'areaname':areaname,'genvoucher':genvoucher,'call_duration':call_duration}}
        resp,content = self.sendssl('/uc/services/order_submit', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["order"]
                self.order = jsonresp["result"]["order"]
                return jsonresp["result"]["order"]
            return None
        else:
            return None
    def order_submit_datatraffic(self,effect_datetime,failure_datetime,simid,areaname):
        return self.order_submit(10,effect_datetime,failure_datetime,1,simid,areaname,False,None)
    def order_submit_datatraffic_batch(self,effect_datetime,failure_datetime,quantity,areaname):
        return self.order_submit(10,effect_datetime,failure_datetime,quantity,None,areaname,False,None)     
    def order_confirm(self,orderid):
        query_params = {'userid':self.userid,'sessionid':self.sessionid,'orderid':orderid}
        resp,content = self.sendssl('/uc/services/order_confirm', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["order"]
                self.order = jsonresp["result"]["order"]
                return jsonresp["result"]["order"]
            return None
        else:
            return None 
    def order_cancel(self,orderid):
        query_params = {'userid':self.userid,'sessionid':self.sessionid,'orderid':orderid,'delete':True}
        resp,content = self.sendssl('/uc/services/order_cancel', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["order"]
                self.order = jsonresp["result"]["order"]
                return jsonresp["result"]["order"]
            return None
        else:
            return None  
    def save_traffic_csv_header(self,csvfile):
        ofile = open(csvfile,'wb')
        writer = csv.writer(ofile,delimiter=',',quoting=csv.QUOTE_NONE)
        writer.writerow(['订单编号'.decode('utf-8').encode('gb2312'),'订单详情编号'.decode('utf-8').encode('gb2312'),'全球芯卡号'.decode('utf-8').encode('gb2312'),'全球芯IMSI'.decode('utf-8').encode('gb2312'),'开始日期'.decode('utf-8').encode('gb2312'),'结束日期'.decode('utf-8').encode('gb2312'),'国家地区'.decode('utf-8').encode('gb2312')])
        ofile.close()
    def save_traffic_csv(self,datatraffic,simid,csvfile):
        ofile = open(csvfile,'a+')
        writer = csv.writer(ofile,delimiter=',',quoting=csv.QUOTE_NONE)
        writer.writerow([datatraffic["orderid"],datatraffic["orderdetailid"],simid,od["effect_datetime"],od["failure_datetime"],od["areaname"].encode('gb2312')])
        ofile.close()   
    def save_order_csv(self,order,simid,csvfile):
        ofile = open(csvfile,'a+')
        writer = csv.writer(ofile,delimiter=',',quoting=csv.QUOTE_NONE)
        od = order["orderdetails"][0]
        writer.writerow([order["id"],od["id"],simid,od["effect_datetime"],od["failure_datetime"],od["areaname"].encode('gb2312')])
        ofile.close()       
    def datatraffic_gets(self, pageindex, pagesize):
        query_params = {'userid':self.adminuserid,'sessionid':self.adminsessionid,'group':1,'pageindex':pageindex,'pagesize':pagesize}
        resp,content = self.sendssl('/om/services/datatraffic_gets', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            #print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                #print jsonresp["result"]["datatraffics"]
                self.datatraffics = jsonresp["result"]["datatraffics"]
                return jsonresp["result"]["datatraffics"]
            return None
        else:
            return None 
    def cardtraffic_bind(self,orderdetailid,trafficid,simid):
        query_params = {'userid':self.userid,'sessionid':self.sessionid,'orderdetailid':orderdetailid,'trafficid':trafficid,'simid':simid}
        resp,content = self.sendssl('/uc/services/cardtraffic_bind', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            #print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["servicepackage"]
                self.servicepackage = jsonresp["result"]["servicepackage"]
                return jsonresp["result"]["servicepackage"]
            return None
        else:
            return None 
    def order_paid(self,order):
        query_params = {'userid':self.adminuserid,'sessionid':self.adminsessionid,'orderid':order["id"],'payvoucher':'OFFLINECHANNELPAY','amount':order["payable_amount"]}
        resp,content = self.sendssl('/om/services/order_paid', json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            #print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                #print jsonresp["result"]["order"]
                self.order = jsonresp["result"]["order"]
                return jsonresp["result"]["order"]
            return None
        else:
            return None             
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='DataCard Storing from a csv file')
    parser.add_argument('-host', default='www.roam-tech.com', type=str, help='uc server Host')
    parser.add_argument('-username', default='15888890702', type=str, help='uc server normal user username')
    parser.add_argument('-password', default='9998323', type=str, help='uc server normal user password')
    parser.add_argument('-starttime', default='2016-11-17 00:00:00', type=str, help='uc server order effect_datetime')
    parser.add_argument('-endtime', default='2016-11-19 23:59:59', type=str, help='uc server order failure_datetime')
    parser.add_argument('-simid', default='8944200012789047432F', type=str, help='uc server order simid')
    parser.add_argument('-quantity', default='1', type=str, help='uc server order quantity')
    parser.add_argument('-areaname', default=None, type=str, help='uc server order areaname')    
    parser.add_argument('-orderid', default=None, type=str, help='uc server cancel the order id') 
    parser.add_argument('-ifile', default='channel.csv', type=str, help='offline channel order csv file') 
    parser.add_argument('-ofile', default='order.csv', type=str, help='save datacard traffic to a csv file') 
    parser.add_argument('-ua', default='RoamPhone/Tconfigtool', type=str, help='http User-Agent header')
    reload(sys)
    sys.setdefaultencoding('utf8')
    args = parser.parse_args()
    orderTest = OrderTest(args.host, args.ua)
    error_no = orderTest.user_login(args.username,args.password)
    orderTest.admin_login()
    if error_no == 0:
        if args.orderid != None:
            orderTest.order_cancel(args.orderid)
        else:
            csvReader = csv.reader(open(args.ifile,'rb'))
            orderTest.save_traffic_csv_header(args.ofile)
            for row in csvReader:
                order = orderTest.order_submit_datatraffic_batch(row[0],row[1],row[2],row[3].decode('utf8'))
                if order != None:
                    od = order["orderdetails"][0]
                    for i in range(od["quantity"]):
                        print "订单详情ID:%d\n订单ID:%d\n开始日期:%s\n结束日期:%s\n国家地区:%s" %(od["id"],od["orderid"],od["effect_datetime"],od["failure_datetime"],od["areaname"])
                        simid=raw_input("输入卡号: ".decode("utf-8").encode("gb2312"))
                        servicepackage = orderTest.cardtraffic_bind(od["id"],None,simid)
                        if servicepackage != None:
                            orderTest.save_order_csv(order,simid,args.ofile)
                    orderTest.order_paid(order)

