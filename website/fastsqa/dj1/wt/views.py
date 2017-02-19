from django.http import HttpResponse
from django.template import loader


def index(request):
    template = loader.get_template('wt/index.html')
    return HttpResponse(template.render(request))


def index_ru(request):
    template = loader.get_template('wt/index-ru.html')
    return HttpResponse(template.render(request))