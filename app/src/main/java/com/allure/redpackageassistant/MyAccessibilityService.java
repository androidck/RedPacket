package com.allure.redpackageassistant;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

/**
 * 监听辅助工具类
 */
public class MyAccessibilityService extends AccessibilityService {

    //主页
    private final String LAUNCHER = "com.tencent.mm.ui.LauncherUI";
    //红包页面UI
    private final String LUCKY_MONEY_RECEIVER = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";
    //红包详情
    private final String LUCKY_MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";

    /**
     * 用于判断是否点击过红包了
     */
    private boolean isOpenRP;

    private boolean isOpenDetail = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //当有事件触发的时候调用
        int eventType = event.getEventType();
        if (event.getClassName() != null) {
            Log.e("packageName", event.getClassName().toString());
        }
        switch (eventType) {
            //监听通知栏
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                for (CharSequence text : texts) {
                    String context = text.toString();
                    if (!TextUtils.isEmpty(context)) {
                        //判断是否有[微信红包]
                        if (context.contains("[微信红包]")) {
                            //打开聊天页面
                            openWeChatPage(event);
                            //isOpenRP = false;
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = null;
                if (event.getClassName() != null)
                    className = event.getClassName().toString();
                if (LAUNCHER.equals(className)) {
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    //开始查找红包
                    findRedPacket(rootNode);
                }
                if (LUCKY_MONEY_RECEIVER.equals(className)) {
                    Log.e("dasdasd", className);
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    //开始抢红包
                    openRedPacket(rootNode);
                }
                break;
        }
    }

    //开始抢红包
    private void openRedPacket(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null) {
                    continue;
                }
                inputClick("com.tencent.mm:id/cyf");
//                if ("android.widget.Button".equals(node.getClassName())) {
//                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    //isOpenDetail = true;
//                }
                openRedPacket(node);
            }
        }
    }

    //开始查找红包
    private void findRedPacket(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null) {
                    continue;
                }
                CharSequence text = node.getText();
                if (text != null && text.toString().equals("微信红包")) {
                    //往上去找可点击的父布局
                    AccessibilityNodeInfo parent = node.getParent();
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            isOpenRP = true;
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
                findRedPacket(node);
                //判断是否已经打开过那个最新的红包了，是的话就跳出for循环，不是的话继续遍历
//                if (isOpenRP) {
//                    break;
//                } else {
//
//                }
            }
        }
    }

    //打开红包页面
    private void openWeChatPage(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            //打开对应聊天页面
            PendingIntent intent = notification.contentIntent;
            try {
                intent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {
        //服务中断时操作
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //服务端开的链接时操作
    }

    //模拟点击
    private void inputClick(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo!=null){
            List<AccessibilityNodeInfo> list=nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item:list){
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
