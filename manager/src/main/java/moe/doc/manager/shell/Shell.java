package moe.doc.manager.shell;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;

import rikka.rish.Rish;
import rikka.rish.RishConfig;
import rikka.doc.doc;
import rikka.doc.docApiConstants;

public class Shell extends Rish {

    @Override
    public void requestPermission(Runnable onGrantedRunnable) {
        if (doc.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            onGrantedRunnable.run();
        } else if (doc.shouldShowRequestPermissionRationale()) {
            System.err.println("Permission denied");
            System.err.flush();
            System.exit(1);
        } else {
            doc.addRequestPermissionResultListener(new doc.OnRequestPermissionResultListener() {
                @Override
                public void onRequestPermissionResult(int requestCode, int grantResult) {
                    doc.removeRequestPermissionResultListener(this);

                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        onGrantedRunnable.run();
                    } else {
                        System.err.println("Permission denied");
                        System.err.flush();
                        System.exit(1);
                    }
                }
            });
            doc.requestPermission(0);
        }
    }

    public static void main(String[] args, String packageName, IBinder binder, Handler handler) {
        RishConfig.init(binder, docApiConstants.BINDER_DESCRIPTOR, 30000);
        doc.onBinderReceived(binder, packageName);
        doc.addBinderReceivedListenerSticky(() -> {
            int version = doc.getVersion();
            if (version < 12) {
                System.err.println("Rish requires server 12 (running " + version + ")");
                System.err.flush();
                System.exit(1);
            }
            new Shell().start(args);
        });
    }
}
