# SmartCar
BigData / IoT / Hadoop / CentOS 6.9 / Java 1.7 / x86 / RAM 16GB
> Flume, Storm, Kafka, Esper, HDFS, Hbase, Redis, Zookeeper, Oozie, Sqoop, Impala, Zepplin, Mahout, Cloudera를 설치, 설정, 개발합니다.  


### 1. 빅데이터 개념
```
* 대규모, 고속의 다양한 데이터를 분석하여 Insight와 Value를 주는 기술
```

#### 1.1.  3V(Volume, Variety, Velocity) - 규모, 다양성, 속도
```
* 규모 : 대량의 데이터
* 다양성 : 다양한 형태의 데이터
* 속도 : 변화, 유통의 속도가 빠른 데이터(+실시간)

cf) 6V : 대규모(Volume), 빠르게(Velocity) 발생하고 있는 다양한(Variety) 데이터를
수용하고 정확한 분석을 통하여 신뢰성(Veracity) 을 확보하고 시각화(Visualization) 하여 
새로운 가치(Value)를 창출하는 기술
```

#### 1.2.   빅데이터 구현 기술
## 1. 수집
```
크롤링, NLP, API, 로그집계, DB집계 등의 원천 데이터를 수집하는 기술
빠른 수집을 위한 선형처리, 분산처리가 요구
실시간 수집을 위한 CEP, ESP
Flume, Storm, Esper
```
# 1.1 Flume, Kafka
```
데이터가 여러개 분산되어 있을때 한번에 집중화, filter 등
통신 프로토콜, 메시지 포맷, 발생 주기, 데이터 크기에 따른 해결책을 제시.
Source#N -> Channel#N -> Sink#N 의 구조로 되어있다. (N : N가지 종류의 소스 처리)
Source(실시간 파일 끝 읽기, 파일 읽기 등...)
Sink(카프라로 연결~?, HDFS에 저장~? 등....)

활용방안
1. 플룸>카프카>하둡(days Status Log Collet)
이 프로젝트에서 반복적으로 발생하는 상태정보를 일 단위로 수집해서 하둡에 적재하는 역할을 수행할 것이다.
(플룸의 메인 목적 : 하둡으로 데이터를 공급 & 로그파일 같은 비관계적 데이터소스)
2. 플룸>카프카>스톰>레디스(realtime Driving Log Collect)
(카프카의 메인 목적 : 분산 pub-sub 메세징 시스템 & 높은 신뢰성/확장성)
플룸(source>chanel>sink) > 카프카(producer>버퍼링>토픽consumer) > 스톰
이를 통해 과속으로 판단된 차량정보를 Redis에 저장할것이다. (플룸을 통해 다른 시스템과 유연하게 이동하는데 사용되는 사례)
```
## 2. 적재
```
분산 저장소에 적재하는 기술
HDFS, Hbase, Kafka ,Redis
1. HDFS(Hadoop Distributed File System)  :  대용량 파일 영구 저장을 목적으로 한다.
2. NoSQL(HBase, MongoDB, Casandra 등)  :  대규모 메시징 데이터를 영구 저장하기 위한 목적으로 사용된다.
3. Inmemory Caching(Redis, Memcached, Infinispan 등) :  대규모 메세지 처리 결과를 고속으로 저장하기 위해 사용된다.
4. MoM(Kafka, RabibitMQ, ActiveMQ 등) : 대규모 메세징 데이터를 임시 저장하기 위한 목적으로 사용 
```
# 2.1 Hadoop(HDFS)
```
대용량 데이터의 분산 (저장 + 처리)를 도와 주는 빅데이터 기술의 핵심 소프트웨어.
하둡은 수집, 적재, 처리, 분석 전 영역과 연결되어 
대표 기능 
대용량 데이터의 분산 저장 : 분산 파일 시스템, HDFS
분산 저장된 대용량 데이터 처리(분석)  : 분산 병렬 처리 기술, MapReduce
client > NameNode, ResourceManager > DataNode, NodeManager(Container,ApplicationMaster)

활용방안
차량의 일별 Status datas를 파티션하여(나눠) HDFS에 적재할 것임.
HIVE로 기간별로 다양한 집계분석을 할 것임.
분석 결과를 Data Warehouse에 저장하여, 재사용, 고급분석으로 쓸 것임.
```

# 2.2 Zookeeper
```
하둡, 카프카, 스톰 Hbase 등의 분산 처리 시스템의 관리를 돕는다.
주 기능: 서버간 공유되는 정보를 이용해, 부하 분산, 순서 제어 등의 관리를 해준다.

활용 방안
하둡, Hbase, kafka, storm 내부에서 내부 데이터와 환결 설정들을 동기화 하는 용도로 사용.
```

## 3. 처리/탐색
```
정형화/ 정규화하는 기술, 탐색할 수 있는 형태로 처리, 쿼리, 시각화 등
처리.탐색 작업이 끝난 데이터셋들은 DW(Data Warehouse)로 측정가능한 구조로 만들어져 분석을 편리하게 할 수 있게 한다.
정기적인 작업은 WorkFlow로 자동화 시킨다.
Hue, Hive, Spark SQL
Spark --> 하둡 에코시스템에 20개 이상의 다양한 소프트웨어들이 있지만,
스파크 하나에 다른 소프트웨어를 커버 할 많은 기능이 있다. (어쩌면, 스파크로 모두 처리하는 것이 목적일수도) 
```
## 4. 분석
```
활동 영역에 따라 통계, 데이터 마이닝, 텍스트 마이닝등 다양
머신러닝 기술을 활용한 Clustering, Classification, Regressiuon, Recommendation 등
Impala, Zepplin, Mahout, Sqoop
```
## 5. 응용

```


































```
빅데이터 인력 수요
분석보다는 수집, 엔지니어!...
제~일 중요한건 방대한 데이터를 가진 회사가 최고...
취업 할 때도 데이터를 많이 모으고 있을 법한 서비스를 운영하는 회사를 고르자
1. 20가지의 많은 소프트웨어를 사용하는 이유
--> 각각 다 다양한 고유의 기능들이 있다. 
버퍼링, 메세지큐 등 개발과정에서 필요한 기능이 생기고 그때 계속해서 추가하게 될 것이다.

2. 이 많은 소프트웨어들을 도대체 어떻게 선정해야할까?
--> 우선 다양한 프로젝트 경험이 필요할 것이고, 구축하려는 상황에 따라
통상적으로(?) 세트처럼 같이 사용되는 것들이 있기도 하다.

3. 통합된 환경은 없을까?
--> 스파크가 많은 기능을 담고 있지만, 각 프로그램들이 이 빅데이터 환경을 구성하기위해
한 회사에서 만들거나 한 것이 아니기 때문에(게다가 대부분이 오픈소스 프로젝트들이다.)
통합된 프로그램은 없다. 
--> 그래도... Django, Spring, Expressjs 몇몇이라도 통합한 플랫폼은 없을까...?
LG CNS의 BigPack 등... 그래도 오픈소스가 무료라...?;;
```




