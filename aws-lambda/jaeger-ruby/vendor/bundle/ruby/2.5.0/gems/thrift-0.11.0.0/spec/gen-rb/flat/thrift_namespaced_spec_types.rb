#
# Autogenerated by Thrift Compiler (0.11.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#

require 'thrift'
require 'referenced_types'


module NamespacedSpecNamespace
  class Hello
    include ::Thrift::Struct, ::Thrift::Struct_Union
    GREETING = 1

    FIELDS = {
      GREETING => {:type => ::Thrift::Types::STRING, :name => 'greeting', :default => %q"hello world"}
    }

    def struct_fields; FIELDS; end

    def validate
    end

    ::Thrift::Struct.generate_accessors self
  end

end
