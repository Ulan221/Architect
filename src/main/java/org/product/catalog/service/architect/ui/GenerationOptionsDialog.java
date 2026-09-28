package org.product.catalog.service.architect.ui;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.ui.DialogWrapper;

public class GenerationOptionsDialog extends DialogWrapper {
    private JCheckBox liquibaseCheckBox;
    private JCheckBox dockerCheckBox;

    public GenerationOptionsDialog() {
        super(true);
        setTitle("Architect Generation Options");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final JPanel jPanel = new JPanel(new GridBagLayout());
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        liquibaseCheckBox = new JCheckBox("Generate Liquibase files", true);
        liquibaseCheckBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dockerCheckBox = new JCheckBox("Generate Docker files", true);
        dockerCheckBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.add(liquibaseCheckBox);
        jPanel.add(dockerCheckBox);

        return jPanel;
    }

    public boolean isGenerateLiquiBase() {
        return liquibaseCheckBox.isSelected();
    }

    public boolean isGenerateDocker() {
        return dockerCheckBox.isSelected();
    }
}
