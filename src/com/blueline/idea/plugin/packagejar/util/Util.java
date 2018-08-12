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
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Util {
    public Util() {
    }

    public static boolean matchFileNamingConventions(String fileName) {
        return fileName.matches("[^/\\\\<>*?|\"]+");
    }

    public static void iterateDirectory(Project project, HashSet<VirtualFile> directories, VirtualFile directory) {
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(directory);
        if (directory != null) {
            directories.add(psiDirectory.getVirtualFile());
            PsiDirectory[] psiDirectories = psiDirectory.getSubdirectories();
            PsiDirectory[] arr$ = psiDirectories;
            int len$ = psiDirectories.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                PsiDirectory pd = arr$[i$];
                iterateDirectory(project, directories, pd.getVirtualFile());
            }
        }

    }

    public static String getTheSameStart(List<String> strings) {
        if (strings != null && strings.size() != 0) {
            int max = 888888;
            Iterator stringIterator = strings.iterator();

            while (stringIterator.hasNext()) {
                String string = (String) stringIterator.next();
                if (string.length() < max) {
                    max = string.length();
                }
            }

            StringBuilder sb = new StringBuilder();
            HashSet set = new HashSet();

            for (int i = 0; i < max; ++i) {
                Iterator iterator = strings.iterator();

                while (iterator.hasNext()) {
                    String string = (String) iterator.next();
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

    private static int getMinorVersion(String vs) {
        int dashIndex = vs.lastIndexOf(95);
        if (dashIndex >= 0) {
            StringBuilder builder = new StringBuilder();

            for (int idx = dashIndex + 1; idx < vs.length(); ++idx) {
                char ch = vs.charAt(idx);
                if (!Character.isDigit(ch)) {
                    break;
                }

                builder.append(ch);
            }

            if (builder.length() > 0) {
                try {
                    return Integer.parseInt(builder.toString());
                } catch (NumberFormatException var5) {
                    ;
                }
            }
        }

        return 0;
    }

    public static String getJDKPath(Project project) {
        JavaSdkVersion sdkVersion = null;
        Sdk projectJdk = null;
        int sdkMinorVersion = 0;
        Set<Sdk> candidates = new HashSet();
        Sdk defaultSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (defaultSdk != null && defaultSdk.getSdkType() instanceof JavaSdkType) {
            candidates.add(defaultSdk);
        }

        Module[] modules = ModuleManager.getInstance(project).getModules();
        int length = modules.length;

        for (int i = 0; i < length; ++i) {
            Module module = modules[i];
            Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null && sdk.getSdkType() instanceof JavaSdkType) {
                candidates.add(sdk);
            }
        }

        JavaSdk javaSdkType = JavaSdk.getInstance();
        Iterator sdkIterator = candidates.iterator();

        while (true) {
            while (true) {
                Sdk candidate;
                String vs;
                JavaSdkVersion candidateVersion;
                do {
                    do {
                        if (!sdkIterator.hasNext()) {
                            Sdk internalJdk = JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk();
                            if (projectJdk == null || sdkVersion == null || !sdkVersion.isAtLeast(JavaSdkVersion.JDK_1_6)) {
                                projectJdk = internalJdk;
                            }

                            JavaSdkType projectJdkType = (JavaSdkType) projectJdk.getSdkType();
                            return projectJdkType.getBinPath(projectJdk) + File.separator;


                        }

                        candidate = (Sdk) sdkIterator.next();
                        vs = candidate.getVersionString();
                    } while (vs == null);

                    candidateVersion = javaSdkType.getVersion(vs);
                } while (candidateVersion == null);

                int candidateMinorVersion = getMinorVersion(vs);
                if (projectJdk == null) {
                    sdkVersion = candidateVersion;
                    sdkMinorVersion = candidateMinorVersion;
                    projectJdk = candidate;
                } else {
                    int result = candidateVersion.compareTo(sdkVersion);
                    if (result > 0 || result == 0 && candidateMinorVersion > sdkMinorVersion) {
                        sdkVersion = candidateVersion;
                        sdkMinorVersion = candidateMinorVersion;
                        projectJdk = candidate;
                    }
                }
            }
        }
    }
}
