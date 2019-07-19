'use strict';

const tracer = require('./tracer');

const controller = require('./controller');
const Joi = require('@hapi/joi');


module.exports = [
  {
    method: 'GET',
    path: '/contactKeeper/contacts',
    handler: async (request, response) => {
      try {
        // Get active scope to enable setting tags on span
        // (and logs if needed)
        const span = tracer.scope().active();
        span.setTag('list', true);
        const res = await controller.listContacts((result)=>{});
        return response.response(res);
      } catch (err) {
        console.error(err);
      }
    },
  },
  {
    method: 'GET',
    path: '/contactKeeper/contacts/{fName}',
    handler: async (request, response) => {
      try {
        const fName = request.params.fName;
        const lName = request.query.lName;
        const res = await controller.getContact(fName, lName,
            (error, result)=>{});
        if (res.length > 0) {
          return response.response(res);
        } else {
          let msg;
          if (lName === '__ALL__') {
            msg = `'${fName}' was not found in your ContactKeeper.`;
          } else {
            msg = `'${fName} ${lName}' was not found
in your ContactKeeper.`;
          }
          return response.response({message: msg});
        }
      } catch (err) {
        return {err: err.detail};
      }
    },
  },
  {
    method: 'POST',
    path: '/contactKeeper/addContact',
    handler: async (request, response) => {
      try {
        const fName = request.payload.firstName;
        const lName = request.payload.lastName;
        const email = request.payload.email;

        const res = await controller.addContact(fName, lName, email,
            (error, result)=>{});
        if (res > 0) {
          const msg = `You have successfully added '${fName} ${lName}'
Email: '${email}' to your ContactKeeper.`;
          return response.response({message: msg});
        } else {
          response.response({err: `Addition was unsuccessful.`});
        }
      } catch (err) {
        return {err: err.detail};
      }
    },
    options: {
      validate: {
        payload: {
          firstName: Joi.string().min(1).max(15).required(),
          lastName: Joi.string().min(1).max(15).required(),
          email: Joi.string().email({minDomainSegments: 2}).required(),
        },
      },
    },
  },
  {
    method: 'PUT',
    path: '/contactKeeper/contacts/{id}',
    handler: async (request, response) => {
      try {
        const span = tracer.scope().active();

        const id = request.params.id;
        const email = request.payload.email;

        const res = await controller.updateByID(id, email, (error, result)=>{});
        let msg;
        if (res > 0) {
          span.setTag('update', id);
          msg = `You have successfully updated contact
with id: '${id}' with email '${email}'.`;
          return response.response({message: msg});
        } else {
          span.setTag('update', 'id not found');
          msg = `Contact with id: '${id}' was not found in your ContactKeeper.`;
          return response.response({message: msg});
        }
      } catch (err) {
        const span = tracer.scope().active();
        span.log({'error': error.detail});
        return {err: err.detail};
      }
    },
  },
  {
    method: 'PUT',
    path: '/contactKeeper/contacts/',
    handler: async (request, response) => {
      try {
        const fName = request.query.fName;
        const lName = request.query.lName;
        const email = request.payload.email;

        const res = await controller.updateEmail(fName, lName, email,
            (error, result)=>{});
        let msg;
        if (res == '__DUPLICATE__') {
          msg = `DUPLICATE: Multiple entries found.
Run 'get ${fName} ${lName}' and then 'updateByID'
with the ID of the specific contact instead`;
          return response.response({err: msg});
        } else if (res > 0) {
          msg = `You have successfully updated '${fName} ${lName}'
with email: '${email}'.`;
          return response.response({message: msg});
        } else {
          msg = `'${fName} ${lName}' was not found in your ContactKeeper.`;
          return response.response({message: msg});
        }
        return response.response(res);
      } catch (err) {
        const span = tracer.scope().active();
        span.log({'error': error.detail});
        return {err: err.detail};
      }
    },
  },
  {
    method: 'DELETE',
    path: '/contactKeeper/contacts/{id}',
    handler: async (request, response) => {
      try {
        const id = request.params.id;

        const res = await controller.deleteByID(id, (error, result)=>{});
        let msg;
        if (res > 0) {
          msg = `You have successfully deleted contact with id: '${id}'
from your ContactKeeper.`;
          return response.response({message: msg});
        } else {
          msg = `Contact with id: '${id}' was not found in your ContactKeeper.`;
          return response.response({message: msg});
        }
      } catch (err) {
        return {err: err.detail};
      }
    },
  },
  {
    method: 'DELETE',
    path: '/contactKeeper/contacts/',
    handler: async (request, response) => {
      try {
        const fName = request.query.fName;
        const lName = request.query.lName;
        const res = await controller.deleteContact(fName, lName,
            (error, result)=>{});
        let msg;
        if (res == '__DUPLICATE__') {
          msg = `DUPLICATE: Multiple entries found.
Run 'get ${fName} ${lName}' and 'deletByID'
with the ID of the specific contact instead`;
          return response.response({err: msg});
        } else if (res > 0) {
          msg = `You have successfully deleted '${fName} ${lName}'
from your ContactKeeper.`;
          return response.response({message: msg});
        } else {
          msg = `'${fName} ${lName}' was not found in your ContactKeeper.`;
          return response.response({message: msg});
        }
      } catch (err) {
        return {err: err.detail};
      }
    },
  },
];
