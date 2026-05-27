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
# from django.conf import settings
# from django.conf.urls.static import static
from django.urls import path
from . import views

urlpatterns = [
    path('cSessionKey/', views.CSessionKeyView.as_view(), name='cSessionKey'),
    path('bdDector/', views.BdDectorView.as_view(), name='bdDector'),
    path('oneCaseByBLP/', views.oneCaseByBLPView.as_view(), name='oneCaseByBLP'),
    path('caseByBLP/', views.CaseByBLPView.as_view(), name='caseByBLP'),
    # 登录
    path('doLogin/', views.doLoginView, name="doLogin"),
    # 个人信息的存储
    path('selfInfoStore/', views.selfInfoStoreView, name="selfInfoStore"),
    # 个人信息的查询
    path('selectSlefInfo/', views.selectSlefInfoView, name="selectSlefInfo"),
    # 添加宝宝
    path('addBaby/', views.addBabyView, name="addBaby"),
    # 编辑宝宝
    path('editBaby/', views.editBabyView, name="editBaby"),
    # 删除宝宝
    path('deletBaby/', views.deletBabyView, name="deletBaby"),
    # 查询宝宝（单个）
    path('selectBaby/', views.selectBabyView, name="selectBaby"),
    # 查询宝宝（全部）
    path('selectBabyAll/', views.selectBabyAllView, name="selectBabyAll"),
    # 查询全部医生
    path('selectDoctor/', views.selectDoctorView, name="selectDoctor"),
    # 图片上传
    # path('upload/', views.upload_image, name='upload_image'),                   # 方式一
    # path('upload_image/', views.uploadImageView, name='upload_image'),          # 方式二*
    # 查询历史评测
    path('selectHistroy/', views.selectHistroyView, name="selectHistroy"),
    # 再次评测身高
    path('againReview/', views.againReviewView, name="againReview")
]

# if settings.DEBUG:
#     urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)