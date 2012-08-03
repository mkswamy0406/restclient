$:.unshift File.expand_path(File.join(File.dirname(__FILE__)))
$:.unshift File.expand_path(File.join(File.dirname(__FILE__), '..', 'lib'))

require 'build-common/buildr/dependencies/pom_generator'
require 'build-common/buildr/dependencies/license/artifact_licenses_task'
require 'build-common/buildr/test/testng'
require 'build-common/util/environment'
require 'build-common/util/version'

