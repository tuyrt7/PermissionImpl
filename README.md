## 加入项目：

gradle依赖

```
// 根目录 gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

// 模块 gradle
dependencies {
	implementation 'com.github.tuyrt7:PermissionImpl:1.0.2'
}

```  

## permission模块：动态权限申请
1.链式调用  
2.可以activity/fragment申请（内部创建fragment继承自androidx.fragment.app.fragment）  
3.动态设置是否弹窗提示（内部默认显示）  
4.也可以自己处理拒绝权限后回调，在.requestPermission(PermissionListener)，自己在回调中处理提示  
5.当勾选不再提示后，默认显示弹窗进入设置页面开启（未监听设置中是否开启权限）  
6.如果有必须取得的权限，可以设置.isRejectNoCancelDialog(true):监听弹窗取消按钮后再次弹出窗口，直到获得权限  
7.适配8.0的系统弹窗，应用内安装的特殊权限
8.拒绝弹窗的内部逻辑已经处理完成，只需要通过函数 isRejectDialog（）、isRejectNoCancelDialog（）、传入对应的配置就行
9.拒绝弹窗的内容，用显著颜色标识如图 gif/xx.jpg

## 使用方式

```
 //声明权限组
 String[] per = new String[]{
         Manifest.permission.CAMERA,
         Manifest.permission.CALL_PHONE
 };

 PermissionImpl.init(this) //FragmentActivity/Fragment
                .permission(Permission.SYSTEM_ALERT_WINDOW,Permission.WRITE_EXTERNAL_STORAGE)//添加权限
                //.permission(per)
                .isRejectDialog(true)//显示拒绝弹窗
                .isRejectNoCancelDialog(false)//显示拒绝弹窗,点击取消后,是否继续弹窗，表示不获取权限不往下执行（默认true，一般用于核心功能的关键权限，不授权不给往下执行）
                .isRejectWithNeverDialog(true)//显示拒绝(永久禁止)弹窗（默认true，此弹窗提示用户永久禁止权限后，需要进入设置页手动通过权限）
                .isEnterAppSetting(true)//进入应用设置页（默认true，进入系统权限设置，适配各大厂商sdk--测试过自己华为mate10，vivoY27，A33开发板，魅族pro6s）
                .dialogTextColor(R.color.colorPrimary)//弹窗内容的文字颜色
                .requestPermission(new AdapterPermissionListener() {
                    //同意所有权限
                    @Override
                    public void onGranted() {
                        Toast.makeText(PermissionActivity.this, "所有权限都已授权", Toast.LENGTH_SHORT).show();
                    }

                    //拒绝权限
                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        super.onDenied(deniedPermission);
                    }
                    //拒绝特殊权限（应用上层弹窗or安装应用）
                    @Override
                    public void onSpecialDenied(List<String> deniedPermission) {
                        super.onSpecialDenied(deniedPermission);
                    }
                    //拒绝授权（勾选不在提示）
                    @Override
                    public void onShouldShowRationale(List<String> deniedPermission) {
                        super.onShouldShowRationale(deniedPermission);
                    }
                });
                
  //简单使用
   PermissionImpl.init(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .requestPermission(new AdapterPermissionListener() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(PermissionActivity.this, "所有权限都已授权", Toast.LENGTH_SHORT).show();
                    }
                });
```

##### 有问题，可以提交issue交流。