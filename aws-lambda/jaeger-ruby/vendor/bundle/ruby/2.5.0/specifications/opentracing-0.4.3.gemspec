# -*- encoding: utf-8 -*-
# stub: opentracing 0.4.3 ruby lib

Gem::Specification.new do |s|
  s.name = "opentracing".freeze
  s.version = "0.4.3"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib".freeze]
  s.authors = ["ngauthier".freeze, "bcronin".freeze, "bensigelman".freeze]
  s.bindir = "exe".freeze
  s.date = "2018-10-24"
  s.email = ["info@opentracing.io".freeze]
  s.homepage = "https://github.com/opentracing/opentracing-ruby".freeze
  s.licenses = ["Apache-2.0".freeze]
  s.rubygems_version = "2.7.3".freeze
  s.summary = "OpenTracing Ruby Platform API".freeze

  s.installed_by_version = "2.7.3" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<minitest>.freeze, ["~> 5.0"])
      s.add_development_dependency(%q<rake>.freeze, ["~> 10.0"])
      s.add_development_dependency(%q<rubocop>.freeze, ["~> 0.54.0"])
      s.add_development_dependency(%q<simplecov>.freeze, ["~> 0.16.0"])
      s.add_development_dependency(%q<simplecov-console>.freeze, ["~> 0.4.0"])
    else
      s.add_dependency(%q<minitest>.freeze, ["~> 5.0"])
      s.add_dependency(%q<rake>.freeze, ["~> 10.0"])
      s.add_dependency(%q<rubocop>.freeze, ["~> 0.54.0"])
      s.add_dependency(%q<simplecov>.freeze, ["~> 0.16.0"])
      s.add_dependency(%q<simplecov-console>.freeze, ["~> 0.4.0"])
    end
  else
    s.add_dependency(%q<minitest>.freeze, ["~> 5.0"])
    s.add_dependency(%q<rake>.freeze, ["~> 10.0"])
    s.add_dependency(%q<rubocop>.freeze, ["~> 0.54.0"])
    s.add_dependency(%q<simplecov>.freeze, ["~> 0.16.0"])
    s.add_dependency(%q<simplecov-console>.freeze, ["~> 0.4.0"])
  end
end
