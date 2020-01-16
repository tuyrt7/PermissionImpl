## 效果图  

![一直弹窗申请，直到授权](https://upload-images.jianshu.io/upload_images/4430423-b58fe635864386cb.gif)
![申请后被拒绝，弹窗提示，可以取消](https://upload-images.jianshu.io/upload_images/4430423-74f98e1a336d11ba.gif)  

## 加入项目步骤：  

1. 根目录下build.gradle添加  

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        //or 审核通过后可去掉
        //maven { url 'https://dl.bintray.com/tuyrt007/tuyrt/' }
    }
}

```  

2. 主工程build.gradle the dependency  

```
dependencies {
    implementation 'com.github.tuyrt7:PermissionImpl:v1.0.2'
    
    //or
    //implementation 'com.tuyrt:permissionimpl:1.0.0'
}
```  


## permission模块：动态权限申请
1. 链式调用、轻量不依赖其他库  
2. 可以activity/fragment申请（内部创建fragment继承自androidx.fragment.app.fragment）  
3. 动态设置是否弹窗提示（内部默认显示）   
4. 也可以自己处理拒绝权限后回调，在.requestPermission(PermissionListener)，自己在回调中处理提示  
5. 当勾选不再提示后，默认显示弹窗进入设置页面开启（未监听设置中是否开启权限）  
6. 如果有必须取得的权限，可以设置.isRejectNoCancelDialog(true):监听弹窗取消按钮后再次弹出窗口，直到获得权限  
7. 适配8.0的系统弹窗，应用内安装的特殊权限

## 使用方式

```
 //声明权限组
 String[] per = new String[]{
         Manifest.permission.CAMERA,
         Manifest.permission.CALL_PHONE
 };
//调用，都是默认值
 PermissionImpl.newPermission()
                .fragment(this)
                //.activity(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .permission(Permission.REQUEST_INSTALL_PACKAGES)
                //.permission(per)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)//add 权限
                .isRejectDialog(true)//显示拒绝弹窗
                .isRejectNoCancelDialog(false)//取消后是否继续弹窗
                .isRejectWithNeverDialog(true)////显示拒绝弹窗
                .isEnterAppSetting(true)//进入应用设置页得的方式（false进入系统权限设置，适配各大厂商sdk--未测试，测试过自己华为mate，发现设置true比较方便）
                .requestPermission(new AdapterPermissionListener(){
                    @Override
                    public void onGranted() {
                        Log.d("Fragment", "获取所有的权限");
                        Toast.makeText(getContext(), "Fragment 获取所有的权限", Toast.LENGTH_SHORT).show();
                    }
                });
                
  //简单使用
   PermissionImpl.newPermission()
                .activity(this)
                .permission(per)
                .requestPermission(new AdapterPermissionListener() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(PermissionActivity.this, "所有权限都已授权", Toast.LENGTH_SHORT).show();
                    }
                });
```

##### 有问题，欢迎指正。联系邮箱：tuyrt7@163.com