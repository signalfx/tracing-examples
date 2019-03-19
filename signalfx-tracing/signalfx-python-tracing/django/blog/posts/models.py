from django.contrib.auth.models import User
from django.db import models


class Author(User):
    def __str__(self):
        return self.name


class Article(models.Model):
    created = models.DateTimeField(auto_now_add=True)
    title = models.CharField(max_length=120)
    description = models.TextField()
    body = models.TextField()
    author = models.ForeignKey('auth.User',
                               related_name='articles',
                               on_delete=models.CASCADE)
    last_updated = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ('created',)

    def __str__(self):
        return self.title
