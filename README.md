## permission模块：动态权限申请
1.链式调用  
2.可以activity/fragment申请（内部创建fragment继承自androidx.fragment.app.fragment）  
3.动态设置是否弹窗提示（内部默认显示）  
4.也可以自己处理拒绝权限后回调，在.requestPermission(PermissionListener)，自己在回调中处理提示  
5.当勾选不再提示后，默认显示弹窗进入设置页面开启（未监听设置中是否开启权限）  
6.如果有必须取得的权限，可以设置.isRejectNoCancelDialog(true):监听弹窗取消按钮后再次弹出窗口，直到获得权限  


## 使用方式

```
 PermissionImpl.newPermission()
                .fragment(this)
                //.activity(this)
                .permission(per)
                .requestPermission(new AdapterPermissionListener(){
                    @Override
                    public void onGranted() {
                        Log.d("Fragment", "获取所有的权限");
                        Toast.makeText(getContext(), "Fragment 获取所有的权限", Toast.LENGTH_SHORT).show();
                    }
                });
```
