// This module import and init() method invocation will create an
// OpenTracing-compatible tracer and initiate auto-instrumentation
// of all supported libraries and frameworks.
//
// This init() call MUST occur before any desired library is imported
// elsewhere for instrumentation to take effect globally.

const tracer = require('signalfx-tracing').init();

// The service name is obtained by SIGNALFX_SERVICE_NAME environment
// variable.  If declaring within the init() call is preferred,
// a `service` option object property can be used:
//
// const tracer = require('signalfx-tracing')
//                  .init({ service: 'my-node-service' })
//
module.exports = tracer;
