package controller;

import util.LogHandler;
import view.MainFrame;

/**
 * アプリケーションのメインコントローラー
 * 画面遷移とイベント処理を統括
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-02-10
 */
public class MainController {

    private final ScreenTransitionController screenController;

    /**
     * コンストラクタ
     *
     * @param mainFrame メインフレーム
     */
    public MainController(MainFrame mainFrame) {
        this.screenController = new ScreenTransitionController(mainFrame);
        LogHandler.getInstance().log(java.util.logging.Level.INFO, "メインコントローラーを初期化しました");
    }

    /**
     * アプリケーションの初期化
     */
    public void initialize() {
        try {
            screenController.showPanel("LIST");
            LogHandler.getInstance().log(java.util.logging.Level.INFO, "アプリケーションを初期化しました");
        } catch (Exception e) {
            LogHandler.getInstance().logError("アプリケーションの初期化に失敗しました", e);
        }
    }

    /**
     * イベントを処理
     *
     * @param event イベント種別
     * @param data  イベントデータ
     */
    public void handleEvent(String event, Object data) {
        try {
            switch (event) {
                case "REFRESH_VIEW" -> screenController.refreshView();
                case "CHANGE_PANEL" -> screenController.showPanel((String) data);
                default -> throw new IllegalArgumentException("未定義のイベント: " + event);
            }
            LogHandler.getInstance().log(
                    java.util.logging.Level.INFO,
                    String.format("イベントを処理しました: %s", event));
        } catch (Exception e) {
            LogHandler.getInstance().logError("イベント処理に失敗しました", e);
        }
    }
}