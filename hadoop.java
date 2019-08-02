hadoop

ZKFC是NameNode的守护进程，控制NameNode什么时候死，NameNode挂掉之后ZKFC会通知另一台NameNode切换；
	ZKFC要和NameNode在同一台机上，哪台机器起NameNode，哪台就起ZKFC


zookeeper实际应用中，存储一些配置信息，定时动态更新

调优  开启小任务模式   本地运行与提交yarn平台运行

实际部署中 secondarynamenode和namenode部署在不同的机器上，secondarynamenode负责合并edits和fsimage的信息，需要和namenode同样大小的内存空间

java时间戳比linux时间戳多三位，精确到毫秒,linux精确到秒



【
	reduce端join操作：
		过程和普通maptask,reducetask过程相同

		弊端：map过程并没有对数据进行合并，而是将所有数据发给reduce端，在reduce端的shuffle(分组)阶段进行合并
			 而map通过网络将数据发送给reduce时，如果数据量天大，网络的传输性能就会降低，会拖累整个的计算速度
			 另一方面，将所有表的数据放松到reduce端进行Join操作，会造成某个reducetask处理的数据过大，造成数据倾斜

】
1

【
	map端join操作（即map端实现多表联表查询）：
		适用于关联表中有小表的情况；
		大表（数据量比较多的表）：按照普通的maptask过程进行处理
		小表（数据量少的表）：将小表数据放入分布式缓存中     、、（分布式缓存：即分布式内存，集群中每台主机贡献一部分内存出来，整合在一起就叫分布式内存）
						   之所以放小表是因为，如果小表数据量太大，内存中有可能放不下（DistributedCache
						   分布式缓存的小表数据，所有的大表都是可以看到的，当实现一个join操作的时候，大表将分布式缓存中的小表数据，读取到本地，并在本地生成一个Map集合，将数据放入集合中

		map端join操作在map阶段就已经将多表数据进行合并，因此没有reducer,也不需要数据的传输了

		计算任务一启动的时候，小表的数据就应该在分布式缓存中

】



【
	o1.compareTo(o2);
就是返回正数的话，当前对象（调用compareTo方法的对象o1）要排在比较对象（compareTo传参对象o2）后面，
返回负数的话，放在前面



】


【
	分区相当于给文件打上分区标签，reduce阶段根据分区标签拉取数据

】


【
	分区以及reducetask的数量设置：
		分区：相同key的数据发送到同一个reduce里面去(物以类聚人以群分，相同的数据去到同一个reduce)
		分组：相同Key的数据发送到同一个集合中去
		默认的分区类：hashpartitioner
		自定义分区类：继承partitioner

】


【
	mapreduce的排序以及序列化：
		要对谁排序，就把谁当成k2
		序列化：实现writable接口就可以实现序列化
		排序和序列化：实现writablecomparable就可以实现排序和序列化
】

【
	map端的join与reduce端的Join：
		reduce端的join：跟普通的MapReduce相同
		map端的join：使用分布式缓存文件，将小表存入分布式缓存，每一个maptask都可以获取这个缓存

】


【
	左外连接：
		以左表为主，先把左表的所有数据进行输出，如果右表有匹配的数据就输出，没有匹配的数据，右表就补null;
】



【hive】
【
	查看库信息：
		describe database [extended] score;
		desc database score;
	查看表信息：
		desc stu1;
		desc formatted stu1;

】


【
	外部表的数据一般是通过其他方式加载进来的

	hive可以将hdfs的一个文件映射成一张表，它们之间靠的就是hive的元数据

	对于外部表来说，表和表数据只是映射关系，表没了，数据还在。这种表模型非常安全；
】


【
	where 和 having的不同点:
		1、where针对表中的列发挥作用，查询数据；having针对查询结果中的列发挥作用，筛选数据；
		2、where后面不能写分组函数，而having后面可以使用分组函数；
		3、having只用于group by分组统计语句；
】


【
	#下面两组的查询结果都是相同的#
	select s.s_id,stu.s_name,s.s_score from score s,student stu where s.s_id = stu.s_id;
	select s.s_id,stu.s_name,s.s_score from score s join student stu on s.s_id = stu.s_id;（测试：后边用on,前边join不能少）

	select * from course c join teacher t on c.t_id = t.t_id;
	select * from course c,teacher t where c.t_id = t.t_id;
】

【
	hive的底层数据存储都是使用的HDFS，数据的统计计算都是使用MapReduce

	hive的元数据： 表与HDFS数据之间的映射关系  默认存储在derby,一般我们会改成mysql或orecal
	hive中元数据包括表的名字，表的列和分区及其属性，表的属性（是否为外部表等），表的数据所在目录等

】


【
	 hive 与 hadoop 的关系：
	 	HIve利用HDFS存储数据，利用MapReduce查询计算分析数据
】


【  beeline连接：
	在core-site.xml中加上以下配置：     
	
	<property>
        <name>hadoop.proxyuser.root.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.root.groups</name>
        <value>*</value>
    </property>



在hdfs-site.xml 中加入以下配置
		<property>
                <name>dfs.webhdfs.enabled</name>
                <value>true</value>
        </property>


bin/hive --service hiveserver2  前台启动；     nohup bin/hive  --service hiveserver2 2>&1  &   后台启动；

bin/beeline
!connect jdbc:hive2://node03.hadoop.com:10000
】


【
	创建内部表，文件名会加个.db  如果是location的话，就没有
】


【
	partitioned by 分区  相当于分文件夹（如：year，month，day）

	clustered by 分桶   （相当于分文件，如按ID分桶的话，将ID相同的数据写入同一个文件中）

】



【flume】
【
	关于flume面试的问题：
	1、flume的采集频率    你们线上的环境如何配置控制文件的大小  基于两个配置 一个是时间的长短  一个是基于文件的大小
		如果flume是收集数据放到到消息队列kafka当中去，就是做实时处理，处理频率比较快大概每三秒钟左右数据灌入一次到kfaka
		如果flume是采集数据放到hdfs上面去做离线处理：尽量的控制文件的大小在128M左右一个比较合适。一般时间长短控制在15分钟到半个小时左右，文件的大小控制在128M左右一个
	2、如何保证flume的持续的高可用：如果flume没有工作了，从数据源来看，数据没有被收集。
								从另外一方面数据的目的地来看：如果flume没有工作，数据的目的地那里没有增加数据
								写一个shell脚本，定时的去查看一下数据源以及数据目的地，看看数据有没有被收集
									
									配置flume的failover  也可以
									
flume如何实现收集mysql的数据？？？
																		
数据的脱敏									
】


【
	jar包的作用域和作用范围：
		procided:开发的时候要，打包的时候不要
		test:测试的时候要，打包的时候不要
		compile:开发的时候要，打包的时候也要
		默认：开发的时候要，打包的时候也要
】


【

	derby  
		没启动hive元数据都会初始化在启动的当前目录，下次再其他地方启动的时候，之前的初始化的元数据不会在当前目录
】


【flume】
【
	Flume是Cloudera提供的一个高可用的，高可靠的，分布式的海量日志采集、聚合和传输的软件。
	Flume的核心是把数据从数据源(source)收集过来，再将收集到的数据送到指定的目的地(sink)。为了保证输送的过程一定成功，在送到目的地(sink)之前，会先缓存数据(channel),待数据真正到达目的地(sink)后，flume在删除自己缓存的数据。
】


【
	tail -f filename 和 tail -F filename qubie
	如果监控的file删除，-F可以监控到，-f监控不到，文件恢复-F继续监控
】




