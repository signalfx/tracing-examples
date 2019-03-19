from django.contrib import admin
from django.conf.urls import include, url
from rest_framework.schemas import get_schema_view


API_TITLE = 'Blog API'
API_DESCRIPTION = 'A Web API for creating, viewing , ' \
                  'editing and deleting articles.'
schema_view = get_schema_view(title=API_TITLE)

urlpatterns = [
    url('admin/', admin.site.urls),
    url(r'^', include('posts.urls')),
    url('schema/', schema_view),
    url('api-auth/', include('rest_framework.urls')),
]
