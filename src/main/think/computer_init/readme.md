# SwitchyOmega
https://www.cnblogs.com/qiumingcheng/p/11707163.html

# github代理

git config --global -l

//设置全局代理
//http
git config --global https.proxy http://127.0.0.1:1080
//https
git config --global https.proxy https://127.0.0.1:1080
//使用socks5代理的 例如ss，ssr 1080是windows下ss的默认代理端口,mac下不同，或者有自定义的，根据自己的改
git config --global http.proxy socks5://127.0.0.1:1080
git config --global https.proxy socks5://127.0.0.1:1080

//只对github.com使用代理，其他仓库不走代理
git config --global http.https://github.com.proxy socks5://127.0.0.1:8888
git config --global https.https://github.com.proxy socks5://127.0.0.1:8888
//取消github代理
git config --global --unset http.https://github.com.proxy
git config --global --unset https.https://github.com.proxy

//取消全局代理
git config --global --unset http.proxy
git config --global --unset https.proxy

# git merge 与push
Can we squash the commits locally and force push the branch, there are 10 commits on the branch and it's hard to review in total for the details.

# maven使用socket代理 
https://stackoverflow.com/questions/34267443/maven-to-use-socks-proxy-for-specific-repo
ssh -D 9999 yourname@your.gateway.com
-DsocksProxyHost=127.0.0.1 -DsocksProxyPort=9999
mvn clean install -DsocksProxyHost=127.0.0.1 -DsocksProxyPort=10808


Also, you can alternatively export this into your environment:

export MAVEN_OPTS="-DsocksProxyHost=127.0.0.1 -DsocksProxyPort=8085"

# apache-rat-plugin
-Drat.skip=true 参数 ，跳过license检查
# checkstyle
-Dcheckstyle.skip=true

# maven-checkstyle-plugin
# 没有修改的文件不被编译
 Changes detected - recompiling the module!
 
 https://stackoverflow.com/questions/1625492/execute-maven-plugin-goal-on-child-modules-but-not-on-parent

# 获取本地mavne仓库地址
https://stackoverflow.com/questions/5916157/how-to-get-the-maven-local-repo-location

# project.basedir	
The directory that the current project resides in.

https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Available_Variables

# maven 打印变量
```
   <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-antrun-plugin</artifactId>
        <version>3.0.0</version>
        <executions>
          <execution>
            <phase>validate</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
              <target>
                <echo>Displaying value of system variables</echo>
                <echo> ${project.build.sourceDirectory}</echo>
              </target>
            </configuration>
          </execution>
        </executions>
      </plugin>
```
mvn validate 

# 阿里云 maven  pom
```
	<!-- 配置阿里云仓库 -->
	<repositories>
		<repository>
			<id>aliyun-repos</id>
			<url>https://maven.aliyun.com/repository/public</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>
	<pluginRepositories>
		<pluginRepository>
			<id>aliyun-repos</id>
			<url>https://maven.aliyun.com/repository/public</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</pluginRepository>
	</pluginRepositories>
```
# ubuntu slow
cat /proc/sys/vm/swappiness
sudo sysctl vm.swappiness=20
sudo gedit /etc/sysctl.conf
vm.swappiness=20


# ssh proxy
sshpass -p 'Qq5375631' ssh -D 8888  -N -f  -C root@8.219.174.169


mvn clean install -DskipTests -Dscala-2.12
