#
# Autogenerated by Thrift Compiler (0.10.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#

require 'thrift'
require 'jaeger/thrift/agent/agent_types'

module Jaeger
  module Thrift
    module Agent
      module Agent
        class Client
          include ::Thrift::Client

          def emitZipkinBatch(spans)
            send_emitZipkinBatch(spans)
          end

          def send_emitZipkinBatch(spans)
            send_oneway_message('emitZipkinBatch', EmitZipkinBatch_args, :spans => spans)
          end
          def emitBatch(batch)
            send_emitBatch(batch)
          end

          def send_emitBatch(batch)
            send_oneway_message('emitBatch', EmitBatch_args, :batch => batch)
          end
        end

        class Processor
          include ::Thrift::Processor

          def process_emitZipkinBatch(seqid, iprot, oprot)
            args = read_args(iprot, EmitZipkinBatch_args)
            @handler.emitZipkinBatch(args.spans)
            return
          end

          def process_emitBatch(seqid, iprot, oprot)
            args = read_args(iprot, EmitBatch_args)
            @handler.emitBatch(args.batch)
            return
          end

        end

        # HELPER FUNCTIONS AND STRUCTURES

        class EmitZipkinBatch_args
          include ::Thrift::Struct, ::Thrift::Struct_Union
          SPANS = 1

          FIELDS = {
            SPANS => {:type => ::Thrift::Types::LIST, :name => 'spans', :element => {:type => ::Thrift::Types::STRUCT, :class => ::Jaeger::Thrift::Zipkin::Span}}
          }

          def struct_fields; FIELDS; end

          def validate
          end

          ::Thrift::Struct.generate_accessors self
        end

        class EmitZipkinBatch_result
          include ::Thrift::Struct, ::Thrift::Struct_Union

          FIELDS = {

          }

          def struct_fields; FIELDS; end

          def validate
          end

          ::Thrift::Struct.generate_accessors self
        end

        class EmitBatch_args
          include ::Thrift::Struct, ::Thrift::Struct_Union
          BATCH = 1

          FIELDS = {
            BATCH => {:type => ::Thrift::Types::STRUCT, :name => 'batch', :class => ::Jaeger::Thrift::Batch}
          }

          def struct_fields; FIELDS; end

          def validate
          end

          ::Thrift::Struct.generate_accessors self
        end

        class EmitBatch_result
          include ::Thrift::Struct, ::Thrift::Struct_Union

          FIELDS = {

          }

          def struct_fields; FIELDS; end

          def validate
          end

          ::Thrift::Struct.generate_accessors self
        end

      end

    end
  end
end
