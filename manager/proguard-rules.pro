-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	public static void check*(...);
	public static void throw*(...);
}

-assumenosideeffects class java.util.Objects{
    ** requireNonNull(...);
}

-keepnames class moe.doc.api.BinderContainer

# Missing class android.app.IProcessObserver$Stub
# Missing class android.app.IUidObserver$Stub
-keepclassmembers class rikka.hidden.compat.adapter.ProcessObserverAdapter {
    <methods>;
}

-keepclassmembers class rikka.hidden.compat.adapter.UidObserverAdapter {
    <methods>;
}

# Entrance of doc service
-keep class rikka.doc.server.docService {
    public static void main(java.lang.String[]);
}

# Entrance of user service starter
-keep class moe.doc.starter.ServiceStarter {
    public static void main(java.lang.String[]);
}

# Entrance of shell
-keep class moe.doc.manager.shell.Shell {
    public static void main(java.lang.String[], java.lang.String, android.os.IBinder, android.os.Handler);
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
}

-assumenosideeffects class moe.doc.manager.utils.Logger {
    public *** d(...);
}

#noinspection ShrinkerUnresolvedReference
-assumenosideeffects class rikka.doc.server.util.Logger {
    public *** d(...);
}

-allowaccessmodification
-repackageclasses rikka.doc
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
