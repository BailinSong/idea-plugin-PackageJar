package com.blueline.idea.plugin.packagejar.ui;

import com.blueline.idea.plugin.packagejar.pack.Packager;
import com.blueline.idea.plugin.packagejar.util.Util;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.util.Consumer;
import com.blueline.idea.plugin.packagejar.pack.impl.AllPacker;
import com.blueline.idea.plugin.packagejar.pack.impl.EachPacker;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Settings extends JDialog {
    private static File tempFile = new File(System.getProperty("java.io.tmpdir") + "idea-plugin-package-path.properties");
    private DataContext dataContext;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField exportDirectoryField;
    private JButton selectPathButton;
    private JTextField exportJarNameField;
    private JCheckBox namedByPackageCheckBox;
    private JCheckBox exportEachChildrenCheckBox;
    private JCheckBox fastModeCheckBox;

    public Settings(DataContext dataContext) {
        this.dataContext = dataContext;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
        this.namedByPackageCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNamedByPackageCheckBoxChange();
            }
        });
        this.selectPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectPathButtonAction();
            }
        });
        Project project = (Project) CommonDataKeys.PROJECT.getData(this.dataContext);
        Module module = (Module) LangDataKeys.MODULE.getData(this.dataContext);
        VirtualFile[] virtualFiles = (VirtualFile[]) CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(this.dataContext);
        List<String> names = new ArrayList();
        VirtualFile[] files = virtualFiles;
        int length = virtualFiles.length;

        for (int i = 0; i < length; ++i) {
            VirtualFile file = files[i];
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(file);
            if (psiDirectory != null) {
                PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
                names.add(psiPackage.getQualifiedName());
            }
        }

        String jarName = Util.getTheSameStart(names);
        if (jarName.equals("")) {
            jarName = module.getName();
        }

        if (jarName.endsWith(".")) {
            jarName = jarName.substring(0, jarName.lastIndexOf("."));
        }

        this.exportJarNameField.setText(jarName);
        System.out.println(tempFile.getPath());

        try {
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }

            Properties properties = new Properties();
            InputStream in = new FileInputStream(tempFile);
            properties.load(in);
            Object exportPath = properties.get("export_path");
            if (exportPath != null) {
                this.exportDirectoryField.setText(exportPath.toString());
            }
        } catch (IOException var12) {
            var12.printStackTrace();
        }

    }

    private void onNamedByPackageCheckBoxChange() {
        if (this.namedByPackageCheckBox.isSelected()) {
            this.exportJarNameField.setEnabled(false);
        } else {
            this.exportJarNameField.setEnabled(true);
        }

    }

    private void onSelectPathButtonAction() {
        Project project = (Project) CommonDataKeys.PROJECT.getData(this.dataContext);
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        FileChooserConsumerImpl chooserConsumer = new FileChooserConsumerImpl(this.exportDirectoryField);
        FileChooser.chooseFile(descriptor, project, (VirtualFile) null, chooserConsumer);
    }

    private void onCancel() {
        this.dispose();
    }

    private void onOK() {
        Project project = (Project) CommonDataKeys.PROJECT.getData(this.dataContext);
        Module module = (Module) LangDataKeys.MODULE.getData(this.dataContext);
        String exportJarName = this.exportJarNameField.getText();
        exportJarName = exportJarName.trim();
        exportJarName = exportJarName + ".jar";
        if (Util.matchFileNamingConventions(exportJarName) && (!exportJarName.equals("") || this.namedByPackageCheckBox.isSelected())) {
            String exportJarPath = this.exportDirectoryField.getText().trim();
            File _temp0 = new File(exportJarPath);
            if (!_temp0.exists()) {
                Messages.showErrorDialog(project, "the selected output path is not exists", "");
            } else {

                Packager packager = (exportEachChildrenCheckBox.isSelected() ? new EachPacker(this.dataContext, exportJarPath) : new AllPacker(this.dataContext, exportJarPath, exportJarName));


                if (this.fastModeCheckBox.isSelected()) {
                    CompilerManager.getInstance(project).make(module, packager);
                } else {
                    CompilerManager.getInstance(project).compile(module, packager);
                }


                this.dispose();
            }
        } else {
            Messages.showErrorDialog(project, "please set a name of the output jar", "");
        }
    }

    private class FileChooserConsumerImpl implements Consumer<VirtualFile> {
        private JTextField ouPutDirectoryField;

        public FileChooserConsumerImpl(JTextField jTextField) {
            this.ouPutDirectoryField = jTextField;
        }

        @Override
        public void consume(VirtualFile virtualFile) {
            this.ouPutDirectoryField.setText(virtualFile.getPath());

            try {
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }

                Properties properties = new Properties();
                FileOutputStream out = new FileOutputStream(tempFile);
                properties.setProperty("export_path", virtualFile.getPath());
                properties.store(out, "The New properties file");
                out.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }
    }
}
