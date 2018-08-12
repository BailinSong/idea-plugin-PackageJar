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
import com.intellij.openapi.compiler.ex.CompilerPathsEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;

public class AllPacker extends Packager {
    private DataContext dataContext;
    private String exportPath;
    private String exportJarName;

    public AllPacker(DataContext dataContext, String exportPath, String exportJarName) {
        this.dataContext = dataContext;
        this.exportPath = exportPath;
        this.exportJarName = exportJarName;
    }

    @Override
    public void pack() {
        Project project = (Project) CommonDataKeys.PROJECT.getData(this.dataContext);
        Module module = (Module) LangDataKeys.MODULE.getData(this.dataContext);
        Messages.clear(project);
        VirtualFile[] virtualFiles = (VirtualFile[]) CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(this.dataContext);
        String outPutPath = CompilerPathsEx.getModuleOutputPath(module, false);
        String jdkPath = Util.getJDKPath(project);
        StringBuilder command = new StringBuilder(jdkPath);
        command.append("jar");
        command.append(" cvf ");
        command.append(this.exportPath);
        command.append("/");
        command.append(this.exportJarName);
        VirtualFile[] files = virtualFiles;
        int length = virtualFiles.length;

        for (int i = 0; i < length; ++i) {
            VirtualFile virtualFile = files[i];
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

        Messages.info(project, command.toString());

        try {
            Process process = Runtime.getRuntime().exec(command.toString());
            BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream(), System.getProperty("sun.jnu.encoding", Charset.defaultCharset().name())));

            String str;
            while ((str = stream.readLine()) != null) {
                Messages.info(project, str);
            }
        } catch (Exception var13) {
            var13.printStackTrace();
        }

    }

    @Override
    public void finished(boolean b, int error, int i1, CompileContext compileContext) {
        if (error == 0) {
            this.pack();
        } else {
            Project project = (Project) CommonDataKeys.PROJECT.getData(this.dataContext);
            Messages.info(project, "compile error");
        }

    }


}
