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
    path('caseList/', views.CaseListView.as_view(), name='caseList'),
    path('patient/', views.PatientView.as_view(), name='patient'),
    path('case/', views.CaseView.as_view(), name='case'),
    path('patientNum/', views.PatientNumView.as_view(), name='patientNum'),
    path('patientNumJq/', views.PatientNumJqView.as_view(), name='patientNumJq'),
    path('image', views.ImageView.as_view(), name='image'),
    path('followList/', views.FollowListView.as_view(), name='followList'),
    path('follow/', views.FollowView.as_view(), name='follow'),
    path('followListNo/', views.FollowListNoView.as_view(), name='followListNo'),
    path('masFollow/', views.MasFollowView.as_view(), name='masFollow'),
    path('loadFile', views.loadFile.as_view(), name='loadFile'),
    path('loadFile/<filename>', views.loadFile.as_view(), name='loadFile'),
    path('getSDS', views.heightPercentSD.as_view(), name='heightPercentSD'),
    path('xfollow/', views.XFollowView.as_view(), name='xfollow'),
    path('cfollow/', views.CFollowView.as_view(), name='cfollow'),
    path('efollow/', views.EPatientView.as_view(), name='efollow'),
    path('statisticPosi/', views.StatisticPosi.as_view(), name='statisticPosi'),
    path('staBl/', views.StaBl.as_view(), name='staBl'),
    path('modifyMB/', views.ModifyDbView.as_view(), name='modifyMB'),
    path('ePatientNew/', views.EPatientNewView.as_view(), name='ePatientNew'),
    path('downZipPl', views.DownZipPl.as_view(), name='downZipPl'),
    path('downZipPl/<filename>', views.DownZipPl.as_view(), name='downZipPl'),
    path('loadFilemas', views.loadFilemas.as_view(), name='loadFilemas'),
    path('loadFilemas/<filename>', views.loadFilemas.as_view(), name='loadFilemas'),
    # 操作日志列表查询
    path('modifylogList/', views.ModifylodListView.as_view(), name='modifylogList'),
    # 同步数据操作
    path('tbsj/', views.TbsffyEltmView.as_view(), name='tbsj'),

]




