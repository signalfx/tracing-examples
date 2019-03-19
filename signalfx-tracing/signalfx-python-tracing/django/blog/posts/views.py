from django.contrib.auth.models import User
from rest_framework import permissions
from rest_framework.viewsets import ModelViewSet, ReadOnlyModelViewSet


from .models import Article
from .serializers import ArticleSerializer, AuthorSerializer
from .permissions import IsAuthorOrReadOnly


class ArticleViewSet(ModelViewSet):
    queryset = Article.objects.all()
    serializer_class = ArticleSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly,
                          IsAuthorOrReadOnly, )

    def perform_create(self, serializer):
        serializer.save(author=self.request.user)


class AuthorViewSet(ReadOnlyModelViewSet):
    queryset = User.objects.get_queryset().order_by('id')
    serializer_class = AuthorSerializer
