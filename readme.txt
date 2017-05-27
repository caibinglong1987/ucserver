用户中心服务开发环境：
1、安装apache-maven (http://maven.apache.org/download.cgi)
2、本地仓库Repository.zip解压到某个路径下，执行scp dev@192.168.0.49:/home/dev/workspace/Repository.zip，密码123456。
3、配置maven settings.xml <localRepository>D:\Repository</localRepository>
4、下载工程代码 http://192.168.0.49:8900/svn/RDtouch/trunk/Sources/ucserver
5、命令行编译 mvn package -DskipTests

开发测试环境部署：
1、从线上环境导出数据库，并导入开发环境数据库
  a、导出数据库：mysqldump -uucdba -pucdba123 -hrdso9813pf02qniu5j6q.mysql.rds.aliyuncs.com --default-character-set=utf8 --dabase ucdbtest > ucdb.dump
  b、导入数据库:修改ucdb.dump把ucdbtest换成ucdb
       CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ucdb` /*!40100 DEFAULT CHARACTER SET utf8 */;

       USE `ucdb`;
  mysql -uucdba -pucdba123 -hrdso9813pf02qniu5j6q.mysql.rds.aliyuncs.com --default-character-set=utf8 --database ucdb < ucdb.dump
2、安装配置用户中心服务，前面编译的包ucserver/target/uc-server-1.0-assembly.tar.gz,上传到120.55.193.0:/opt/uc-server-1.0-devel
  a、修改属性配置文件conf/resource.properties:
        protocol.port=8090
		couchbase.bucket=uctest
		couchbase.namespace=uctest
		sip.port=5064
  b、修改属性配置文件conf/project-repository.properties:
        repository.jdbc.url=jdbc:mysql://rdso9813pf02qniu5j6q.mysql.rds.aliyuncs.com/ucdb?characterEncoding=UTF-8
        repository.jdbc.username=ucdba
        repository.jdbc.password=ucdba123
        repository.jdbc.schema.default=ucdb
3、启动ucserver,进入/opt/uc-server-1.0-devel目录，执行bin/start.sh
   停止服务bin/stop.sh skip
4、后续升级一般替换lib/uc-server-1.0.jar包，有新增服务修改conf/spring-config.xml
   类似<entry key="credtrip_open"><bean class="com.roamtech.uc.handler.services.CredtripOpenService" /></entry> 