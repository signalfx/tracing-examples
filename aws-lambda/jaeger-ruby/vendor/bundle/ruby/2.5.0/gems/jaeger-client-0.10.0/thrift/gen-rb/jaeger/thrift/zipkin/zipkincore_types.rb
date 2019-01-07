#
# Autogenerated by Thrift Compiler (0.10.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#

require 'thrift'

module Jaeger
  module Thrift
    module Zipkin
      module AnnotationType
        BOOL = 0
        BYTES = 1
        I16 = 2
        I32 = 3
        I64 = 4
        DOUBLE = 5
        STRING = 6
        VALUE_MAP = {0 => "BOOL", 1 => "BYTES", 2 => "I16", 3 => "I32", 4 => "I64", 5 => "DOUBLE", 6 => "STRING"}
        VALID_VALUES = Set.new([BOOL, BYTES, I16, I32, I64, DOUBLE, STRING]).freeze
      end

      # Indicates the network context of a service recording an annotation with two
# exceptions.
# 
# When a BinaryAnnotation, and key is CLIENT_ADDR or SERVER_ADDR,
# the endpoint indicates the source or destination of an RPC. This exception
# allows zipkin to display network context of uninstrumented services, or
# clients such as web browsers.
      class Endpoint
        include ::Thrift::Struct, ::Thrift::Struct_Union
        IPV4 = 1
        PORT = 2
        SERVICE_NAME = 3

        FIELDS = {
          # IPv4 host address packed into 4 bytes.
# 
# Ex for the ip 1.2.3.4, it would be (1 << 24) | (2 << 16) | (3 << 8) | 4
          IPV4 => {:type => ::Thrift::Types::I32, :name => 'ipv4'},
          # IPv4 port
# 
# Note: this is to be treated as an unsigned integer, so watch for negatives.
# 
# Conventionally, when the port isn't known, port = 0.
          PORT => {:type => ::Thrift::Types::I16, :name => 'port'},
          # Service name in lowercase, such as "memcache" or "zipkin-web"
# 
# Conventionally, when the service name isn't known, service_name = "unknown".
          SERVICE_NAME => {:type => ::Thrift::Types::STRING, :name => 'service_name'}
        }

        def struct_fields; FIELDS; end

        def validate
        end

        ::Thrift::Struct.generate_accessors self
      end

      # An annotation is similar to a log statement. It includes a host field which
# allows these events to be attributed properly, and also aggregatable.
      class Annotation
        include ::Thrift::Struct, ::Thrift::Struct_Union
        TIMESTAMP = 1
        VALUE = 2
        HOST = 3

        FIELDS = {
          # Microseconds from epoch.
# 
# This value should use the most precise value possible. For example,
# gettimeofday or syncing nanoTime against a tick of currentTimeMillis.
          TIMESTAMP => {:type => ::Thrift::Types::I64, :name => 'timestamp'},
          VALUE => {:type => ::Thrift::Types::STRING, :name => 'value'},
          # Always the host that recorded the event. By specifying the host you allow
# rollup of all events (such as client requests to a service) by IP address.
          HOST => {:type => ::Thrift::Types::STRUCT, :name => 'host', :class => ::Jaeger::Thrift::Zipkin::Endpoint, :optional => true}
        }

        def struct_fields; FIELDS; end

        def validate
        end

        ::Thrift::Struct.generate_accessors self
      end

      # Binary annotations are tags applied to a Span to give it context. For
# example, a binary annotation of "http.uri" could the path to a resource in a
# RPC call.
# 
# Binary annotations of type STRING are always queryable, though more a
# historical implementation detail than a structural concern.
# 
# Binary annotations can repeat, and vary on the host. Similar to Annotation,
# the host indicates who logged the event. This allows you to tell the
# difference between the client and server side of the same key. For example,
# the key "http.uri" might be different on the client and server side due to
# rewriting, like "/api/v1/myresource" vs "/myresource. Via the host field,
# you can see the different points of view, which often help in debugging.
      class BinaryAnnotation
        include ::Thrift::Struct, ::Thrift::Struct_Union
        KEY = 1
        VALUE = 2
        ANNOTATION_TYPE = 3
        HOST = 4

        FIELDS = {
          KEY => {:type => ::Thrift::Types::STRING, :name => 'key'},
          VALUE => {:type => ::Thrift::Types::STRING, :name => 'value', :binary => true},
          ANNOTATION_TYPE => {:type => ::Thrift::Types::I32, :name => 'annotation_type', :enum_class => ::Jaeger::Thrift::Zipkin::AnnotationType},
          # The host that recorded tag, which allows you to differentiate between
# multiple tags with the same key. There are two exceptions to this.
# 
# When the key is CLIENT_ADDR or SERVER_ADDR, host indicates the source or
# destination of an RPC. This exception allows zipkin to display network
# context of uninstrumented services, or clients such as web browsers.
          HOST => {:type => ::Thrift::Types::STRUCT, :name => 'host', :class => ::Jaeger::Thrift::Zipkin::Endpoint, :optional => true}
        }

        def struct_fields; FIELDS; end

        def validate
          unless @annotation_type.nil? || ::Jaeger::Thrift::Zipkin::AnnotationType::VALID_VALUES.include?(@annotation_type)
            raise ::Thrift::ProtocolException.new(::Thrift::ProtocolException::UNKNOWN, 'Invalid value of field annotation_type!')
          end
        end

        ::Thrift::Struct.generate_accessors self
      end

      # A trace is a series of spans (often RPC calls) which form a latency tree.
# 
# The root span is where trace_id = id and parent_id = Nil. The root span is
# usually the longest interval in the trace, starting with a SERVER_RECV
# annotation and ending with a SERVER_SEND.
      class Span
        include ::Thrift::Struct, ::Thrift::Struct_Union
        TRACE_ID = 1
        NAME = 3
        ID = 4
        PARENT_ID = 5
        ANNOTATIONS = 6
        BINARY_ANNOTATIONS = 8
        DEBUG = 9
        TIMESTAMP = 10
        DURATION = 11

        FIELDS = {
          TRACE_ID => {:type => ::Thrift::Types::I64, :name => 'trace_id'},
          # Span name in lowercase, rpc method for example
# 
# Conventionally, when the span name isn't known, name = "unknown".
          NAME => {:type => ::Thrift::Types::STRING, :name => 'name'},
          ID => {:type => ::Thrift::Types::I64, :name => 'id'},
          PARENT_ID => {:type => ::Thrift::Types::I64, :name => 'parent_id', :optional => true},
          ANNOTATIONS => {:type => ::Thrift::Types::LIST, :name => 'annotations', :element => {:type => ::Thrift::Types::STRUCT, :class => ::Jaeger::Thrift::Zipkin::Annotation}},
          BINARY_ANNOTATIONS => {:type => ::Thrift::Types::LIST, :name => 'binary_annotations', :element => {:type => ::Thrift::Types::STRUCT, :class => ::Jaeger::Thrift::Zipkin::BinaryAnnotation}},
          DEBUG => {:type => ::Thrift::Types::BOOL, :name => 'debug', :default => false, :optional => true},
          # Microseconds from epoch of the creation of this span.
# 
# This value should be set directly by instrumentation, using the most
# precise value possible. For example, gettimeofday or syncing nanoTime
# against a tick of currentTimeMillis.
# 
# For compatibilty with instrumentation that precede this field, collectors
# or span stores can derive this via Annotation.timestamp.
# For example, SERVER_RECV.timestamp or CLIENT_SEND.timestamp.
# 
# This field is optional for compatibility with old data: first-party span
# stores are expected to support this at time of introduction.
          TIMESTAMP => {:type => ::Thrift::Types::I64, :name => 'timestamp', :optional => true},
          # Measurement of duration in microseconds, used to support queries.
# 
# This value should be set directly, where possible. Doing so encourages
# precise measurement decoupled from problems of clocks, such as skew or NTP
# updates causing time to move backwards.
# 
# For compatibilty with instrumentation that precede this field, collectors
# or span stores can derive this by subtracting Annotation.timestamp.
# For example, SERVER_SEND.timestamp - SERVER_RECV.timestamp.
# 
# If this field is persisted as unset, zipkin will continue to work, except
# duration query support will be implementation-specific. Similarly, setting
# this field non-atomically is implementation-specific.
# 
# This field is i64 vs i32 to support spans longer than 35 minutes.
          DURATION => {:type => ::Thrift::Types::I64, :name => 'duration', :optional => true}
        }

        def struct_fields; FIELDS; end

        def validate
        end

        ::Thrift::Struct.generate_accessors self
      end

      class Response
        include ::Thrift::Struct, ::Thrift::Struct_Union
        OK = 1

        FIELDS = {
          OK => {:type => ::Thrift::Types::BOOL, :name => 'ok'}
        }

        def struct_fields; FIELDS; end

        def validate
          raise ::Thrift::ProtocolException.new(::Thrift::ProtocolException::UNKNOWN, 'Required field ok is unset!') if @ok.nil?
        end

        ::Thrift::Struct.generate_accessors self
      end

    end
  end
end
