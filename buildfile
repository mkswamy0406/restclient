require 'build/dependencies'
require 'build/tasks'

VERSION = "#{Rally::Build::Version.version}"
GROUP = "com.rallydev.restclient"

repositories.remote << 'http://alm-build.f4tech.com:8080/nexus/content/groups/public'
repositories.release_to = 'http://alm-build:8080/nexus/content/repositories/releases'

desc 'Rally Rest Client'
define 'restclient', :group=> GROUP, :version=> VERSION do

  extend Rally::Build::Dependencies::PomGenerator

  compile.with artifact_ns.compile

  test.using :testng
  test.with artifact_ns.test

  package :jar
  package :javadoc
  package :sources

end
