package controller;

import util.LogHandler;
import view.ListPanel;
import view.MainFrame;
import javax.swing.JPanel;

/**
 * 画面遷移を制御するコントローラークラス
 * MainFrameを介して各画面の表示を管理
 *
 * @author Nakano
 * @version 2.0.0
 * @since 2025-03-12
 */
public class ScreenTransitionController {

    private final MainFrame mainFrame;
    private final ListPanel listPanel;

    public ScreenTransitionController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.listPanel = new ListPanel();
        LogHandler.getInstance().log(java.util.logging.Level.INFO, "画面遷移コントローラーを初期化しました");
    }

    /**
     * 指定されたパネルタイプの画面を表示
     *
     * @param panelType 表示するパネルタイプ
     */
    public void showPanel(String panelType) {
        try {
            JPanel panel = switch (panelType) {
                case "LIST" -> listPanel;
                default -> throw new IllegalArgumentException("未定義のパネルタイプ: " + panelType);
            };
            mainFrame.showPanel(panel);
            LogHandler.getInstance().log(
                    java.util.logging.Level.INFO,
                    String.format("画面を切り替えました: %s", panelType));
        } catch (Exception e) {
            LogHandler.getInstance().logError("画面切り替えに失敗しました", e);
        }
    }

    /**
     * 現在のビューを更新
     */
    public void refreshView() {
        mainFrame.refreshView();
    }

    /**
     * 現在表示中のパネルを取得
     *
     * @return 現在のパネル
     */
    public JPanel getCurrentPanel() {
        return mainFrame.getCurrentPanel();
    }
}