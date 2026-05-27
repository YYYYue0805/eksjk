"""wjwsjk URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.urls import path
from . import views

urlpatterns = [
    path('notice/', views.NoticeView.as_view(), name='notice'),
    path('noticeListView/', views.NoticeListView.as_view(), name='noticeListView'),
    path('opinionView/', views.OpinionView.as_view(), name='opinionView'),
    path('opinionListView/', views.OpinionListView.as_view(), name='opinionListView'),
    path('replyOpinionView/', views.ReplyOpinionView.as_view(), name='replyOpinionView'),
    path('closeNotice/', views.CloseNoticeView.as_view(), name='closeNotice'),
    path('image', views.ImageView.as_view(), name='image'),
]
