# Rocker

## 使用

添加项目`build.gradle`依赖

```
dependencies {
	compile 'com.github.nekocharm:Rocker:2.0.0
}
```

因为我解决不了遮蔽其他控件的问题，所以2.0.0的Rocker继承View，如果想使用SurfaceView则使用

```
dependencies {
	compile 'com.github.nekocharm:Rocker:1.0.1
}
```

在布局文件中使用

```xml
<com.sakurax.rocker.Rocker
    android:id="@+id/rocker"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

## 参数

|功能| 属性名      | 类型 | 默认值 |
| ---- | ---------- | ---- | ------ |
|底半径| area_radius | dimension | 75 |
|摇杆半径| rocker_radius | dimension | 25 |
|底透明度| area_alpha | integer | 30 |
|摇杆透明度| rocker_alpha | integer | 150 |
|底背景| area_background | color\|reference | 蓝色 |
|摇杆背景| rocker_background | color\|reference | 蓝色 |
