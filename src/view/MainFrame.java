package view;

import util.LogHandler;
import javax.swing.*;
import java.awt.*;

/**
 * アプリケーションのメインウィンドウを管理するクラス
 * パネルの切り替えとメニューバーの管理
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-02-10
 */
public class MainFrame extends AbstractFrame {

    /** 現在表示中のパネル */
    private JPanel currentPanel;

    /** メインコンテンツを配置するパネル */
    private final JPanel contentPanel;

    public MainFrame() {
        super();
        this.contentPanel = new JPanel(new BorderLayout());
        frame.add(contentPanel);
    }

    @Override
    protected void customizeFrame() {
        frame.setTitle("エンジニア人材管理");
        initializeMenuBar();
        LogHandler.getInstance().log(java.util.logging.Level.INFO, "メインフレームを初期化しました");
    }

    /**
     * メニューバーを初期化
     */
    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // 表示メニュー
        JMenu viewMenu = new JMenu("表示");
        JMenuItem refreshItem = new JMenuItem("更新");
        refreshItem.addActionListener(e -> refreshView());
        viewMenu.add(refreshItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);
    }

    /**
     * パネルを切り替えて表示
     *
     * @param panel 表示するパネル
     */
    public void showPanel(JPanel panel) {
        if (panel == null) {
            LogHandler.getInstance().log(java.util.logging.Level.WARNING, "表示するパネルがnullです");
            return;
        }

        if (currentPanel != null) {
            contentPanel.remove(currentPanel);
        }

        currentPanel = panel;
        contentPanel.add(currentPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        LogHandler.getInstance().log(
                java.util.logging.Level.INFO,
                String.format("パネルを切り替えました: %s", panel.getClass().getSimpleName()));
    }

    /**
     * 現在のビューを更新
     */
    public void refreshView() {
        contentPanel.revalidate();
        contentPanel.repaint();
        LogHandler.getInstance().log(java.util.logging.Level.INFO, "ビューを更新しました");
    }

    /**
     * 現在表示中のパネルを取得
     *
     * @return 現在のパネル
     */
    public JPanel getCurrentPanel() {
        return currentPanel;
    }
}