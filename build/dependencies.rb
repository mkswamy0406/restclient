SLF4J = "org.slf4j:slf4j-api:jar:1.6.1"

JACKSON = group(
    "jackson-core",
    "jackson-databind",
    "jackson-annotations",
    :under => "com.fasterxml.jackson.core", :version => "2.0.6"
)

HTTP_CLIENT = group(
    "httpcore",
    "httpclient",
    "httpclient-cache",
    :under => "org.apache.httpcomponents", :version => "4.1.2"
)

HAMCREST = group(
    "hamcrest-all",
    "hamcrest-core",
    "hamcrest-library",
    :under => "org.hamcrest", :version => "1.1"
)

TESTING = [
    "org.mockito:mockito-core:jar:1.8.3",
    "org.objenesis:objenesis:jar:1.0",
    "org.testng:testng:jar:6.1.1",
    "com.beust:jcommander:jar:1.12",
    "org.beanshell:bsh:jar:2.0b4",
    HAMCREST
]

FINAGLE = [
    "io.netty:netty:jar:3.5.5.Final",
    "com.twitter:finagle-test:jar:6.0.3",
    "com.twitter:util-collection:jar:6.0.4",
    "com.twitter:util-core:jar:6.0.4",
    "com.twitter:util-hashing:jar:6.0.4",
    "com.twitter:util-jvm:jar:6.0.4",
    "com.twitter:util-logging:jar:6.0.4",
    "com.twitter:util-codec:jar:6.0.4",
    "com.twitter:finagle-http:jar:6.0.3",
    "com.twitter:finagle-core:jar:6.0.3",
    "org.scala-lang:scala-library:jar:2.9.1"
]

artifact_ns do |ns|

  ns.ns(:compile).use HTTP_CLIENT,
                      JACKSON,
                      FINAGLE,
                      "commons-logging:commons-logging:jar:1.1.1",
                      SLF4J

  ns.ns(:test).use TESTING
end