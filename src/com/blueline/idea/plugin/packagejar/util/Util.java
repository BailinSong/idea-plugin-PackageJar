//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.blueline.idea.plugin.packagejar.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.SystemProperties;
import com.intellij.util.lang.JavaVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JdkVersionDetector;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Util {
    public Util() {
    }

    public static boolean checkForJdk(@NotNull Path homePath) {

        return (Files.exists(homePath.resolve("bin/javac")) || Files.exists(homePath.resolve("bin/javac.exe"))) && (isModularRuntime(homePath) || Files.exists(homePath.resolve("jre/lib/rt.jar")) || Files.isDirectory(homePath.resolve("classes")) || Files.exists(homePath.resolve("jre/lib/vm.jar")) || Files.exists(homePath.resolve("../Classes/classes.jar")));
    }

    public static boolean checkForJre(@NotNull Path homePath) {

        return Files.exists(homePath.resolve("bin/java")) || Files.exists(homePath.resolve("bin/java.exe"));
    }

    public static String getJDKPath(Project project) {

        Sdk projectJdk;

        if ((projectJdk = getModuleSdk(project)) == null && (projectJdk = getProjectSdk(project)) == null) {
            projectJdk = getSystemSdk();
        }

        JavaSdkType projectJdkType = (JavaSdkType) projectJdk.getSdkType();
        return projectJdkType.getBinPath(projectJdk) + File.separator;

    }

    private static Sdk getModuleSdk(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();

        for (Module module : modules) {
            Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null && sdk.getSdkType() instanceof JavaSdkType) {
                return sdk;
            }
        }
        return null;
    }

    public static String getOutPutPath(Module module) {

        URI uri = URI.create(CompilerModuleExtension.getInstance(module).getCompilerOutputUrl());
        return isWin() ? uri.getPath().substring(1) : uri.getPath();
    }

    private static Sdk getProjectSdk(Project project) {
        Sdk defaultSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (defaultSdk != null && defaultSdk.getSdkType() instanceof JavaSdkType) {
            return defaultSdk;
        }
        return null;
    }

    static Sdk getSystemSdk() {
        Path javaHome = Paths.get(SystemProperties.getJavaHome());
        if (checkForJre(javaHome) && !checkForJdk(javaHome)) {
            Path javaHomeParent = javaHome.getParent();
            if (javaHomeParent != null && checkForJre(javaHomeParent) && checkForJdk(javaHomeParent)) {
                javaHome = javaHomeParent;
            }
        }

        String versionName = JdkVersionDetector.formatVersionString(JavaVersion.current());
        return JavaSdk.getInstance().createJdk(versionName, javaHome.toAbsolutePath().toString(), !checkForJdk(javaHome));
    }

    public static String getTheSameStart(List<String> strings) {
        if (strings != null && strings.size() != 0) {
            int max = 888888;

            for (String string : strings) {
                if (string.length() < max) {
                    max = string.length();
                }
            }

            StringBuilder sb = new StringBuilder();
            HashSet<Character> set = new HashSet<>();

            for (int i = 0; i < max; ++i) {

                for (String string : strings) {
                    set.add(string.charAt(i));
                }

                if (set.size() != 1) {
                    break;
                }

                sb.append(set.iterator().next());
                set.clear();
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static boolean isExplodedModularRuntime(@NotNull Path homePath) {

        return Files.isDirectory(homePath.resolve("modules/java.base"));
    }

    public static boolean isModularRuntime(@NotNull Path homePath) {

        return Files.isRegularFile(homePath.resolve("lib/jrt-fs.jar")) || isExplodedModularRuntime(homePath);
    }

    static boolean isWin() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        return osName.startsWith("windows");
    }

    public static void iterateDirectory(Project project, HashSet<VirtualFile> directories, VirtualFile directory) {
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(directory);
        if (psiDirectory != null) {
            directories.add(psiDirectory.getVirtualFile());
            PsiDirectory[] psiDirectories = psiDirectory.getSubdirectories();

            for (PsiDirectory pd : psiDirectories) {
                iterateDirectory(project, directories, pd.getVirtualFile());
            }
        }

    }

    public static boolean matchFileNamingConventions(String fileName) {
        return fileName.matches("[^/\\\\<>*?|\"]+");
    }
}
