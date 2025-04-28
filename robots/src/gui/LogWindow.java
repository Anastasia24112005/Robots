package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + "log_window_config.properties";

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
        loadState();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    public void saveState() {
        Properties properties = new Properties();
        properties.setProperty("x", String.valueOf(getX()));
        properties.setProperty("y", String.valueOf(getY()));
        properties.setProperty("width", String.valueOf(getWidth()));
        properties.setProperty("height", String.valueOf(getHeight()));
        properties.setProperty("maximized", String.valueOf(isMaximum()));

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadState() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            int x = Integer.parseInt(properties.getProperty("x", String.valueOf(getX())));
            int y = Integer.parseInt(properties.getProperty("y", String.valueOf(getY())));
            int width = Integer.parseInt(properties.getProperty("width", String.valueOf(getWidth())));
            int height = Integer.parseInt(properties.getProperty("height", String.valueOf(getHeight())));
            boolean maximized = Boolean.parseBoolean(properties.getProperty("maximized", String.valueOf(isMaximum())));

            setBounds(x, y, width, height);
            if (maximized) {
                try {
                    setMaximum(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.out.println("Configuration file not found. Using default settings.");
        }
    }
}