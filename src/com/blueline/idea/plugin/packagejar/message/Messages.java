//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.blueline.idea.plugin.packagejar.message;

import com.intellij.compiler.impl.ProblemsViewPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.MessageView;
import com.intellij.ui.content.MessageView.SERVICE;

public class Messages {
    private static final String ID = "packing";

    public Messages() {
    }

    private static ProblemsViewPanel getInstance(Project project) {
        MessageView messageView = SERVICE.getInstance(project);
        ProblemsViewPanel packMessages = null;
        Content[] contents = messageView.getContentManager().getContents();
        int length = contents.length;

        for (int i = 0; i < length; ++i) {
            Content content = contents[i];
            if ("packing".equals(content.getTabName())) {
                packMessages = (ProblemsViewPanel) content.getComponent();
                break;
            }
        }

        if (packMessages != null) {
            return packMessages;
        } else {
            packMessages = new ProblemsViewPanel(project);
            Content content = com.intellij.ui.content.ContentFactory.SERVICE.getInstance().createContent(packMessages, "packing", true);
            messageView.getContentManager().addContent(content);
            messageView.getContentManager().setSelectedContent(content);
            return packMessages;
        }
    }

    public static void clear(Project project) {
        MessageView messageView = SERVICE.getInstance(project);
        Content[] contents = messageView.getContentManager().getContents();
        int length = contents.length;

        for (int i = 0; i < length; ++i) {
            Content content = contents[i];
            if ("packing".equals(content.getTabName())) {
                ProblemsViewPanel viewPanel = (ProblemsViewPanel) content.getComponent();
                viewPanel.close();
                break;
            }
        }

    }

    public static void info(Project project, String string) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
        if (toolWindow != null) {
            toolWindow.activate((Runnable) null, false);
        }

        getInstance(project).addMessage(3, new String[]{string}, (VirtualFile) null, -1, -1, (Object) null);
    }
}
