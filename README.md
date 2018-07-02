# SmartCar
BigData / IoT / Hadoop / CentOS
>> Flume, Storm, Esper, HDFS, Hbase, Kafka ,Redis


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
```
1. 수집
크롤링, NLP, API, 로그집계, DB집계 등의 원천 데이터를 수집하는 기술
빠른 수집을 위한 선형처리, 분산처리가 요구
실시간 수집을 위한 CEP, ESP
Flume, Storm, Esper

2. 적재
분산 저장소에 적재하는 기술
HDFS, Hbase, Kafka ,Redis
1. HDFS(Hadoop Distributed File System)  :  대용량 파일 영구 저장을 목적으로 한다.
2. NoSQL(HBase, MongoDB, Casandra 등)  :  대규모 메시징 데이터를 영구 저장하기 위한 목적으로 사용된다.
3. Inmemory Caching(Redis, Memcached, Infinispan 등) :  대규모 메세지 처리 결과를 고속으로 저장하기 위해 사용된다.
4. MoM(Kafka, RabibitMQ, ActiveMQ 등) : 대규모 메세징 데이터를 임시 저장하기 위한 목적으로 사용 


3. 처리/탐색
정형화/ 정규화하는 기술, 탐색할 수 있는 형태로 처리, 쿼리, 시각화 등
처리.탐색 작업이 끝난 데이터셋들은 DW(Data Warehouse)로 측정가능한 구조로 만들어져 분석을 편리하게 할 수 있게 한다.
정기적인 작업은 WorkFlow로 자동화 시킨다.
Hue, Hive, Spark SQL
Spark --> 하둡 에코시스템에 20개 이상의 다양한 소프트웨어들이 있지만,
스파크 하나에 다른 소프트웨어를 커버 할 많은 기능이 있다. (어쩌면, 스파크로 모두 처리하는 것이 목적일수도) 

4. 분석
활동 영역에 따라 통계, 데이터 마이닝, 텍스트 마이닝등 다양
머신러닝 기술을 활용한 Clustering, Classification, Regressiuon, Recommendation 등
Impala, Zepplin, Mahout, Sqoop

5. 응용

```


































```
빅데이터 인력 수요
분석보다는 수집, 엔지니어!...
제~일 중요한건 방대한 데이터를 가진 회사가 최고...
취업 할 때도 데이터를 많이 모으고 있을 법한 서비스를 운영하는 회사를 고르자

```




