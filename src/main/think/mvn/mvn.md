## -D -P 区别
-D代表属性

-P profile

## maven 多模块一起编译和单模块编译的区别
mvn  --offline -Dtest=org.apache.hudi.functional.TestBootstrap#testMetadataBootstrapWithUpdatesCOW test-compile surefire:test -P spark3.2,scala-2.12 -Dspark3.2 -Dscala-2.12 -Drat.skip=true -Dcheckstyle.skip=true -pl hudi-spark-datasource/hudi-spark,hudi-common   -DfailIfNoTests=false