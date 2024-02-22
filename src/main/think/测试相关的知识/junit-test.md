## maven-surefire-plugin
https://blog.csdn.net/Message_lx/article/details/125858026
如果你执行过mvn test或者执行其他maven命令时跑了测试用例，你就已经用过maven-surefire-plugin了。 maven-surefire-plugin是maven里执行测试用例的插件，不显示配置就会用默认配置。 这个插件的surefire:test命令会默认绑定maven执行的test阶段。
## JUnit 5


### 参数化测试
@ParameterizedTest
@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
