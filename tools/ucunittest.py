import json
import httplib2
import argparse

class UCUnitTest(object):
    def __init__(self, host):
        self.host = host
        self.client = httplib2.Http(disable_ssl_certificate_validation=True)#httplib.HTTPConnection(host, port, timeout=10)
        self.headers = {"Content-Type": "application/json; charset=utf-8"}
        self.products = []

    def send(self, service, query_params):
        uri = "http://"+self.host+service
        return self.client.request(uri,"POST", query_params, self.headers)

    def sendssl(self, service, query_params):
        uri = "https://"+self.host+service
        print uri
        return self.client.request(uri,"POST", query_params, self.headers)
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
    def get_categories(self):
        resp,content = self.send('/uc/services/prdcategory_gets', json.JSONEncoder(ensure_ascii = True).encode({'userid':self.userid,'sessionid':self.sessionid}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["prdcategorys"]
                self.prdcategorys = jsonresp["result"]["prdcategorys"]
                return jsonresp["result"]["prdcategorys"]
            return None
        else:
            return None

    def get_products(self, categoryid):
        resp,content = self.send('/uc/services/product_gets', json.JSONEncoder(ensure_ascii = True).encode({'userid':self.userid,'sessionid':self.sessionid,'categoryid':categoryid}))
        if resp.status == 200:
            jsonresp = json.loads(content)
            print jsonresp
            if jsonresp["error_no"] != 0:
                print jsonresp["error_info"]
                return None
            else:
                print jsonresp["result"]["products"]
                for prd in jsonresp["result"]["products"]:
                    self.products.append(prd)
                return jsonresp["result"]["products"]
            return None
        else:
            return None
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='DataCard Storing from a csv file')
    parser.add_argument('--host', default='www.roam-tech.com', type=str, help='DataCard Storing Server Host')
    parser.add_argument('--username', default='zhengsj', type=str, help='uc server normal user username')
    parser.add_argument('--password', default='123456', type=str, help='uc server normal user password')
    args = parser.parse_args()
    uc_unittest = UCUnitTest(args.host)
    error_no = uc_unittest.user_login(args.username,args.password)
    if error_no == 0:
        prdcategories = uc_unittest.get_categories()
        if prdcategories != None:
            for category in prdcategories:
                products = uc_unittest.get_products(category["id"])
