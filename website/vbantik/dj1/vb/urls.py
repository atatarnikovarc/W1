from django.conf.urls import url

from . import views

app_name = 'vb'
urlpatterns = [
    url(r'^$', views.index, name='index'),
]