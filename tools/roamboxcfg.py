import json
import httplib2
import argparse
import time

class RoamBoxCfg(object):
    def __init__(self, host):
        self.host = host
        self.client = httplib2.Http(disable_ssl_certificate_validation=True)#httplib.HTTPConnection(host, port, timeout=10)
        self.headers = {"Content-Type": "application/json; charset=utf-8"}
        self.products = []

    def send(self, service, query_params):
        uri = "http://"+self.host+service
        return self.client.request(uri,"POST", query_params, self.headers)
    def sendwithhost(self, host, service, query_params):
        uri = "http://"+host+service
        return self.client.request(uri,"POST", query_params, self.headers)
    def sendssl(self, service, query_params):
        uri = "https://"+self.host+service
        print uri
        return self.client.request(uri,"POST", query_params, self.headers)
    def roambox_login(self, username, password):
        query_params = {'jsonrpc':'2.0','method':'login','params':[username,password]}
        resp,content = self.send("/cgi-bin/luci/rpc/auth", json.JSONEncoder(ensure_ascii = True).encode(query_params))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
            else:
                print jsonresp["result"]
                self.token = jsonresp["result"]
                return jsonresp["result"]

            return None
    def get_all(self):
        try:
            print 'call get_all'
            resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'get_all','params':[]}))
            if resp.status == 200:
                jsonresp = json.loads(content)
                print jsonresp
                if jsonresp.get('code',None) != None:
                    print jsonresp["message"]
                    return None
                else:
                    print jsonresp["result"]
                    self.currentcfg = jsonresp["result"]
                    return jsonresp["result"]
                return None
            else:
                print resp
                return None
        except:
            return None

    def set_wan_line_dhcp(self):
        print 'call set_wan_line_dhcp'
        resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'set_wan','params':['line','dhcp']}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
                return None
            else:
                print jsonresp["result"]
                return jsonresp["result"]
            return None
        else:
            return None
    def set_wan_wireless_dhcp(self,ssid,password):
        print 'call set_wan_wireless_dhcp'
        resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'set_wan','params':['wireless','dhcp',ssid,password]}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
                return None
            else:
                print jsonresp["result"]
                return jsonresp["result"]
            return None
        else:
            return None 
    def set_lanphone(self,phone,userid,ssid,password,lanip,channel):
        print 'call set_lanphone'
        resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'set_lanphone','params':[phone,userid,ssid,password,lanip,channel]}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
                return None
            else:
                print jsonresp["result"]
                return jsonresp["result"]
            return None
        else:
            return None 
    def set_lan(self,ssid,password,lanip,channel):
        print 'call set_lan'
        resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'set_lan','params':[ssid,password,lanip,channel]}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
                return None
            else:
                print jsonresp["result"]
                return jsonresp["result"]
            return None
        else:
            return None 
    def set_phone(self,phone,userid):
        print 'call set_phone'
        resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'set_phone','params':[phone,userid]}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
                return None
            else:
                print jsonresp["result"]
                return jsonresp["result"]
            return None
        else:
            return None 
    def apply(self):
        print 'call apply'
        resp,content = self.send('/cgi-bin/luci/rpc/roambox?auth='+self.token, json.JSONEncoder(ensure_ascii = True).encode({'jsonrpc':'2.0','method':'apply','params':[]}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp.get('code',None) != None:
                print jsonresp["message"]
                return None
            else:
                print jsonresp["result"]
                return jsonresp["result"]
            return None
        else:
            return None             
    def get_touchinfo(self):
        try:
            print 'call get_touchinfo'
            resp,content = self.sendwithhost('www.roam-tech.com','/uc/services/touchinfo_get',json.JSONEncoder(ensure_ascii = True).encode({'devid':self.currentcfg['devid'],'host':self.currentcfg['wan_ip']}))
            if resp.status == 200:
                jsonresp = json.loads(content)
                print jsonresp
                if jsonresp.get('error_no',None) != 0:
                    print jsonresp["error_info"]
                    return None
                else:
                    print jsonresp["result"]
                    return jsonresp["result"]
                return None
            else:
                return None 
        except:
            return None
    def check_config_complete(self):
        time.sleep(4)
        i = 0
        while True:
            time.sleep(1)
            currentcfg = roambox.get_all()
            if currentcfg != None:
                i=i+1
            if i > 3:
                i=0             
                roambox.apply()
            if currentcfg != None and currentcfg["wan_isup"] == True and currentcfg.get("wan_ip",None) != None:
                break
        time.sleep(4)       
        for i in range(1,4): 
                touchinfo = roambox.get_touchinfo()
                if touchinfo != None:
                    break
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='DataCard Storing from a csv file')
    parser.add_argument('--host', default='192.168.45.1', type=str, help='RoamBox Server Host')
    parser.add_argument('--username', default='roamuser', type=str, help='RoamBox config user username')
    parser.add_argument('--password', default='roampass123', type=str, help='RoamBox config user password')
    args = parser.parse_args()
    roambox = RoamBoxCfg(args.host)
    error_no = roambox.roambox_login(args.username,args.password)
    if error_no != None:
        currentcfg = roambox.get_all()
        if currentcfg != None:
            for i in range(1,5): 
                roambox.set_wan_line_dhcp()
                roambox.check_config_complete()
                roambox.set_wan_wireless_dhcp('RoamingdataFORmobilephone','12345678')
                roambox.check_config_complete()
                roambox.set_lanphone('15658024841','582','Box12345678','123456789','192.168.45.1','1')
                roambox.apply()
                roambox.check_config_complete()
                
                roambox.set_wan_line_dhcp()
                roambox.check_config_complete()
                roambox.set_wan_wireless_dhcp('roamingdata03','roamingdata2015')
                roambox.check_config_complete()
                roambox.set_lanphone('15658024841','582','Box123456789','12345678','192.168.45.1','1')
                roambox.apply()
                roambox.check_config_complete()

            roambox.set_wan_wireless_dhcp('roamingdata03','roamingdata2015')
            roambox.check_config_complete()
            #roambox.set_wan_line_dhcp()
            #roambox.check_config_complete()
            roambox.set_wan_wireless_dhcp('RoamingdataFORmobilephone','12345678')
            roambox.check_config_complete()
            #roambox.set_wan_wireless_dhcp('roamingdata03','roamingdata2015')
            #roambox.check_config_complete()            
            roambox.set_lanphone('15658024841','582','Box123456789','12345678','192.168.45.1','1')
            roambox.apply()
            roambox.check_config_complete()