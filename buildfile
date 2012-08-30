require 'build/dependencies'
require 'build/tasks'

VERSION = "#{Rally::Build::Version.version}"
GROUP = "com.rallydev.restclient"

repositories.remote << 'http://alm-build.f4tech.com:8080/nexus/content/groups/public'
repositories.release_to = 'http://alm-build:8080/nexus/content/repositories/releases'

desc 'Rally Rest Client'
define 'restclient', :group=> GROUP, :version=> VERSION do
    extend Rally::ArtifactLicenseExtension
    extend Rally::Build::Dependencies::PomGenerator

  compile.with artifact_ns.compile

  test.using :testng
  test.with artifact_ns.test

  package(:jar).meta_inf << path_to(:target, "artifacts_report.csv")

  package :javadoc
  package :sources

end

desc 'Checks open source licenses for violations'
Project.local_task(:"restclient:artifact:license:check")

desc 'Report on project artifact licenses'
Project.local_task(:"restclient:artifact:license:report")

desc "Dump the known licenses in the config.yaml license-metadata element"
Project.local_task(:"restclient:artifact:license:known:licenses")

desc "Show the global config.yaml file contents"
Project.local_task(:"restclient:artifact:license:show:global")

desc 'Show the project local oss license config.yaml file'
Project.local_task(:"restclient:artifact:license:show:local")
