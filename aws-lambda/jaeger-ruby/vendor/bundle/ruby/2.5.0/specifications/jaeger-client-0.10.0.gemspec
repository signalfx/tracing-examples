# -*- encoding: utf-8 -*-
# stub: jaeger-client 0.10.0 ruby lib

Gem::Specification.new do |s|
  s.name = "jaeger-client".freeze
  s.version = "0.10.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib".freeze]
  s.authors = ["SaleMove TechMovers".freeze]
  s.date = "2018-12-25"
  s.description = "".freeze
  s.email = ["techmovers@salemove.com".freeze]
  s.homepage = "https://github.com/salemove/jaeger-client-ruby".freeze
  s.licenses = ["MIT".freeze]
  s.rubygems_version = "2.7.3".freeze
  s.summary = "OpenTracing Tracer implementation for Jaeger in Ruby".freeze

  s.installed_by_version = "2.7.3" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<bundler>.freeze, ["~> 1.14"])
      s.add_development_dependency(%q<rake>.freeze, ["~> 10.0"])
      s.add_development_dependency(%q<rspec>.freeze, ["~> 3.0"])
      s.add_development_dependency(%q<rubocop>.freeze, ["~> 0.54.0"])
      s.add_development_dependency(%q<rubocop-rspec>.freeze, ["~> 1.24.0"])
      s.add_development_dependency(%q<timecop>.freeze, ["~> 0.9"])
      s.add_runtime_dependency(%q<opentracing>.freeze, ["~> 0.3"])
      s.add_runtime_dependency(%q<thrift>.freeze, [">= 0"])
    else
      s.add_dependency(%q<bundler>.freeze, ["~> 1.14"])
      s.add_dependency(%q<rake>.freeze, ["~> 10.0"])
      s.add_dependency(%q<rspec>.freeze, ["~> 3.0"])
      s.add_dependency(%q<rubocop>.freeze, ["~> 0.54.0"])
      s.add_dependency(%q<rubocop-rspec>.freeze, ["~> 1.24.0"])
      s.add_dependency(%q<timecop>.freeze, ["~> 0.9"])
      s.add_dependency(%q<opentracing>.freeze, ["~> 0.3"])
      s.add_dependency(%q<thrift>.freeze, [">= 0"])
    end
  else
    s.add_dependency(%q<bundler>.freeze, ["~> 1.14"])
    s.add_dependency(%q<rake>.freeze, ["~> 10.0"])
    s.add_dependency(%q<rspec>.freeze, ["~> 3.0"])
    s.add_dependency(%q<rubocop>.freeze, ["~> 0.54.0"])
    s.add_dependency(%q<rubocop-rspec>.freeze, ["~> 1.24.0"])
    s.add_dependency(%q<timecop>.freeze, ["~> 0.9"])
    s.add_dependency(%q<opentracing>.freeze, ["~> 0.3"])
    s.add_dependency(%q<thrift>.freeze, [">= 0"])
  end
end
