# -*- coding: utf-8 -*-

from django.http import HttpResponse
from django.template import loader

import logging

logger = logging.getLogger(__name__)


def index(request):
    template = loader.get_template('wt/index.html')
    logger.info('index asdfasdвdsafsdafsdfffffffffffffffffffff')
    response = HttpResponse(template.render(request))
    return response


def index_ru(request):
    template = loader.get_template('wt/index-ru.html')
    return HttpResponse(template.render(request))