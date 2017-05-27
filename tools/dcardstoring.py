import csv
import json
import stackless as SL
import httplib2
import argparse
import os
import fileinput
import datetime

class HttpSender(object):
    def __init__(self, host, port, service, user_agent):
        self.host = host
        self.port = port
        self.service = service
        self.client = httplib2.Http()#httplib.HTTPConnection(host, port, timeout=10)
        self.headers = {"Content-Type": "application/json; charset=utf-8","User-Agent": user_agent}
    
    def send1(self, query_params):
        try:
            resp,content = self.client.request("http://"+self.host+":"+str(self.port)+self.service,"POST", query_params, self.headers)
            if resp.status == 200:
                jsonresp = json.loads(content)
                if jsonresp["error_no"] != 0:
                    print jsonresp["error_info"]
                else:
                    print jsonresp["result"]["datacard"]
                return jsonresp
        except Exception, e:
            print e
    def send(self, service, query_params):
        uri = "http://"+self.host+":"+str(self.port)+service
        print uri
        return self.client.request(uri,"POST", query_params, self.headers)
    def close(self):
        pass

class DataCardStorage(object):
    def __init__(self, csvfile, sender, type):
        self.csvfile = csvfile
        self.file_extention = csvfile.split('.')[1]
        self.sender = sender
        self.process_chan = SL.channel()
        self.send_chan = SL.channel()
        self.sender = sender
        self.type = type
        SL.tasklet(self.process_line_loop)(self.process_chan, self.send_chan)
        SL.tasklet(self.send_line_loop)(self.send_chan)
        SL.schedule()

    def process_line(self, line, cout):
        print line
        if self.file_extention == 'csv':
            iccid = line[0]
            imsi = line[1]
            ki = None
        else:           
            imsi = line[0]
            iccid = line[1]
            iccid = datetime.datetime.now().strftime("%m%d%H%M")+iccid[8:]
            ki = line[2]
            if len(line) >= 6:
                imsi_hk = line[4]
                ki_hk = line[5]
            else:
                imsi_hk = None
                ki_hk = None
        if imsi.isdigit():
            self.result = {'iccid': iccid,'imsi': imsi, 'ki': ki, 'imsi_hk': imsi_hk, 'ki_hk': ki_hk,'userid': self.userid, 'sessionid': self.sessionid, 'type': self.type}         
            cout.send(json.JSONEncoder(ensure_ascii = True).encode(self.result))  

    def process_line_loop(self, cin, cout):
        while 1:
            line = cin.receive()
            if line == 'EOF':
                cout.send('EOF')
                break
            self.process_line(line, cout)
            
    def send_line_loop(self, cin):
        while 1:
            line = cin.receive()
            if line == 'EOF':
                break
            self.sender.send1(line)
    def admin_login(self, username, password):
        query_params = {'username':username,'password':password}
        #self.client.request("POST", "/uc/services/login", query_params, self.headers)
        resp,content = self.sender.send("/uc/services/login", json.JSONEncoder(ensure_ascii = True).encode(query_params))
        #response = self.client.getresponse()
        #print resp
        print content
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
    def process_file(self, username, password):
        error_no = self.admin_login(username, password)
        if error_no == 0:
            if self.file_extention == 'csv':
                csvReader = csv.reader(open(self.csvfile,'rb'))
                for row in csvReader:
                    self.process_chan.send(row)
            else:
                for line in fileinput.input(self.csvfile): 
                    row = line.split('\t')
                    if len(row) >= 2:
                        self.process_chan.send(row)
        
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='DataCard Storing from a csv file')
    parser.add_argument('--host', default='www.roam-tech.com', type=str, help='DataCard Storing Server Host')
    parser.add_argument('--port', default=80, type=int, help='DataCard Storing Server Port')
    parser.add_argument('--csv', default='datacard-batch1.csv', type=str, help='DataCard Storing input csv file')
    parser.add_argument('--service', default='/om/services/datacard_set', type=str, help='DataCard Storing service path')
    parser.add_argument('--username', default='roamadmin', type=str, help='uc server admin username')
    parser.add_argument('--password', default='Roamingdata2016', type=str, help='uc server admin password')
    parser.add_argument('--useragent', default='RoamPhone/Tconfigtool', type=str, help='http User-Agent header')
    parser.add_argument('--type', default=3, type=str, help='data card type')
    args = parser.parse_args()
    
    http_sender = HttpSender(args.host, args.port, args.service, args.useragent)
    datacard_storage = DataCardStorage(args.csv, http_sender, args.type)
    datacard_storage.process_file(args.username,args.password)
    http_sender.close()