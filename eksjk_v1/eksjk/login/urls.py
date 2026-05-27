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
    path('dologin', views.DologinView.as_view(), name='dologin'),
    path('logout', views.LogoutView.as_view(), name='logout'),
    path('user', views.UserView.as_view(), name='user'),
    path('userList', views.UserListView.as_view(), name='userList'),
    path('userStatus', views.UserStatusView.as_view(), name='userStatus'),
    path('', views.CsrfView.as_view(), name='/'),
    path('noticePer', views.NoticePerView.as_view(), name='noticePer'),
    path('unit', views.UnitView.as_view(), name='unit'),
    path('unitList', views.UnitListView.as_view(), name='unitList'),
    path('unitAll', views.UnitAll.as_view(), name='unitAll'),
]

