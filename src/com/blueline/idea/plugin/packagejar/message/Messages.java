//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.blueline.idea.plugin.packagejar.message;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.text.MessageFormat;

public class Messages {
    private static final String WINDOW_ID = "Package Jar";


    private static final Runnable EMPTY_TASK = () -> {
    };
    private static Messages instance;
    private static ToolWindow window;
    private JTextPane textPane;


    private Messages(Project project) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);

        if (window == null) {
            textPane = new JTextPane();
            textPane.setEditable(false);

            JBScrollPane scrollPane = new JBScrollPane(textPane);
            DefaultCaret caret = (DefaultCaret) textPane.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

            textPane.setContentType("text/plain");
            textPane.setText("");

            window = manager.registerToolWindow(WINDOW_ID, scrollPane, ToolWindowAnchor.BOTTOM);
            window.show(EMPTY_TASK);
        }
    }

    public static Messages getInstance(Project project) {


        if (instance == null) {
            synchronized (Messages.class) {
                if (instance == null) {
                    instance = new Messages(project);
                }
            }
        }
        return instance;
    }

    public void clear() {
        textPane.setText("");
    }

    public void hide() {
        window.hide(EMPTY_TASK);
    }

    public synchronized Messages message(String format, Object... args) {
        if (textPane.getText().endsWith("\n") || textPane.getText().isEmpty()) {

            textPane.setText(textPane.getText() + MessageFormat.format(format, args) + "\n");
        } else {
            textPane.setText(textPane.getText() + "\n" + MessageFormat.format(format, args) + "\n");
        }

        return this;
    }

    public void show() {
        window.show(EMPTY_TASK);
    }

}
