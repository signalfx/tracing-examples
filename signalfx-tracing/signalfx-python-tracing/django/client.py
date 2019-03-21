# IMPORTANT additions to a client file:
# from signalfx_tracing import auto_instrument, create_tracer, trace
# from signalfx_tracing.libraries import requests_config
#
# requests_config.propagate = True
# tracer = create_tracer(service_name='<APP_NAME>')
# auto_instrument(tracer)
#
# #Then at the end of all function calls to be traced:
# tracer.close()
#
# These are noted below with additional explanations.


import logging
import requests
from argparse import ArgumentParser
from requests.auth import HTTPBasicAuth
from signalfx_tracing import auto_instrument, create_tracer, trace
# Important: Needed to propagate traces
from signalfx_tracing.libraries import requests_config


# Create and configure logger
logging.basicConfig(level='DEBUG',
                    format='%(asctime)s %(message)s',)


# create an instance of the SignalFx tracer
tracer = create_tracer(service_name='BlogClient')

# Setting requests_config.propagate to 'True'  ensures
# that the relational information between spans is maintained
# and forwarded.
requests_config.propagate = True

# Configure auto-instrumentation of the tracing instance
auto_instrument(tracer)


class BlogClient:
    def __init__(self, url, username, password):
        self.url = url
        self.session = requests.Session()
        self.session.auth = HTTPBasicAuth(username, password)

    @trace
    def create_blog(self, new_title, new_description, new_body):
        response = requests.post(self.url,
                                 json=dict(title=new_title,
                                           description=new_description,
                                           body=new_body),
                                 auth=self.session.auth, )
        success = (response.status_code == 201)
        logging.info('INFO - Created: {}'.format(success))
        return response.json().get('id')

    @trace
    def list_blogs(self):
        response = requests.get(self.url)
        success = (response.status_code == 200)
        logging.info('INFO - Viewed: {}'.format(success))
        return response.json()

    @trace
    def get_blog(self, blog_id):
        url_to_read = self.url.format(blog_id)
        response = requests.get(url_to_read)
        success = (response.status_code == 200)
        logging.info('INFO - Read: {}'.format(success))
        return response.json()

    @trace
    def update_blog(self, blog_id, new_title):
        url_to_update = self.url + '{}/'.format(blog_id)
        response = requests.patch(url_to_update,
                                  json=dict(title=new_title),
                                  auth=self.session.auth, )
        success = (response.status_code == 200)
        logging.info('INFO - Updated: {}'.format(success))
        return response.json()

    @trace
    def delete_blog(self, blog_id):
        url_to_delete = self.url + '{}/'.format(blog_id)
        response = requests.delete(url_to_delete,
                                   auth=self.session.auth, )
        success = (response.status_code == 204)
        logging.info('INFO - Deleted status: {}'.format(success))


def connection(url, username, password):
    client = BlogClient(url, username, password)
    blog_id = client.create_blog('BlogName', 'A new blog post', 'my content..')
    print("\nViewing blogs:\n{}\n".format(client.list_blogs()))
    print("\nReading blog:\n{}\n".format(client.get_blog(blog_id)))
    print("\nUpdated blog:\n{}\n".format(client.update_blog(blog_id,
                                         'A new blog title')))
    client.delete_blog(blog_id)

    print("\n" + 20 * "*-")
    print("Interaction complete!")
    print("*-" * 20 + "\n")


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('-u', '--username', dest='username',
                        type=str, required=True,
                        help='Username created for blog access.')
    parser.add_argument('-p', '--password', dest='password',
                        type=str, required=True,
                        help='Password created for blog access.')
    parser.add_argument('-P', '--port', dest='port', type=int,
                        required=True, help='Port for running BlogServer.')
    args = parser.parse_args()

    server_url = 'http://localhost:{}/articles/'.format(args.port)

    connection(server_url, args.username, args.password)

    # Manually closing the tracer enables the requester to
    # initiate flushes of the traces. Without this,
    # the traces may not show up.
    tracer.close()
