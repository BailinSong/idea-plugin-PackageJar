//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.blueline.idea.plugin.packagejar.pack.impl;

import com.blueline.idea.plugin.packagejar.message.Messages;
import com.blueline.idea.plugin.packagejar.pack.Packager;
import com.blueline.idea.plugin.packagejar.util.Util;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class AllPacker extends Packager {
    private final DataContext dataContext;
    private final String exportPath;
    private final String exportJarName;

    public AllPacker(DataContext dataContext, String exportPath, String exportJarName) {
        this.dataContext = dataContext;
        this.exportPath = exportPath;
        this.exportJarName = exportJarName;
    }

    @Override
    public void finished(boolean b, int error, int i1, @NotNull CompileContext compileContext) {
        if (error == 0) {
            this.pack();
        } else {
            Project project = CommonDataKeys.PROJECT.getData(this.dataContext);
            Messages.getInstance(project).message("compile error");
        }

    }

    @Override
    public void pack() {
        Project project = CommonDataKeys.PROJECT.getData(this.dataContext);
        Module module = LangDataKeys.MODULE.getData(this.dataContext);
        Messages.getInstance(project).clear();
        VirtualFile[] virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(this.dataContext);
        String outPutPath = Util.getOutPutPath(module);
        Messages.getInstance(project).message("outPutPath: " + outPutPath);
        String jdkPath = Util.getJDKPath(project);
        Messages.getInstance(project).message("jdkPath: " + jdkPath);
        StringBuilder command = new StringBuilder(jdkPath);
        command.append("jar");
        command.append(" cvf ");
        command.append(this.exportPath);
        command.append("/");
        command.append(this.exportJarName);

        Messages.getInstance(project).message("exportFile: " + this.exportPath+"/"+this.exportJarName);

        for (VirtualFile virtualFile : virtualFiles) {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (psiDirectory != null) {
                command.append(" -C ");
                command.append(outPutPath);
                command.append(" ");
                PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
                command.append(psiPackage.getQualifiedName().replaceAll("\\.", "/"));
                command.append("/");
            }
        }

        Messages.getInstance(project).message("command: " + command);

        try {
            Process process = Runtime.getRuntime().exec(command.toString());
            BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream(), System.getProperty("sun.jnu.encoding", Charset.defaultCharset().name())));
            Messages.getInstance(project).message("charset: " + Charset.defaultCharset().name());
            String str;
            while ((str = stream.readLine()) != null) {
                Messages.getInstance(project).message(str);
            }
        } catch (Exception var13) {
            var13.printStackTrace();
        }

    }


}
