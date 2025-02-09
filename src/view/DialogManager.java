package view;

import javax.swing.*;

import util.LogHandler;

import java.awt.*;
/** 現在使用していません */
//import java.util.Objects;

/**
 * ダイアログを管理するシングルトンクラス
 * アプリケーション全体で統一されたダイアログ表示
 *
 * <p>
 * 主な機能：
 * <ul>
 * <li>エラーダイアログの表示</li>
 * <li>確認ダイアログの表示</li>
 * <li>完了通知ダイアログの表示</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用例：
 *
 * <pre>
 * DialogManager dialog = DialogManager.getInstance();
 * dialog.showErrorDialog("エラーが発生しました");
 * if (dialog.showConfirmDialog("処理を実行しますか?")) {
 *     // 確認OKの処理
 * }
 * </pre>
 * </p>
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-01-24
 */
public class DialogManager {

    /** シングルトンインスタンス */
    private static final DialogManager INSTANCE = new DialogManager();

    /** デフォルトのダイアログタイトル */
    private static final String DEFAULT_ERROR_TITLE = "エラー";
    private static final String DEFAULT_CONFIRM_TITLE = "確認";
    private static final String DEFAULT_COMPLETE_TITLE = "完了";

    /**
     * プライベートコンストラクタ
     * シングルトンパターンのため、外部からのインスタンス化を防ぐ
     */
    private DialogManager() {
    }

    /**
     * DialogManagerのインスタンスを取得
     *
     * @return DialogManagerの唯一のインスタンス
     */
    public static DialogManager getInstance() {
        return INSTANCE;
    }

    /**
     * エラーダイアログを表示
     *
     * @param message エラーメッセージ
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    public void showErrorDialog(String message) {
        validateMessage(message);
        LogHandler.getInstance().log(java.util.logging.Level.SEVERE, message);
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                createMessagePanel(message),
                DEFAULT_ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE));
    }

    /**
     * 確認ダイアログを表示
     *
     * @param message 確認メッセージ
     * @return ユーザーが「はい」を選択した場合true
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    public boolean showConfirmDialog(String message) {
        validateMessage(message);
        int option = JOptionPane.showOptionDialog(
                null,
                createMessagePanel(message),
                DEFAULT_CONFIRM_TITLE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] { "はい", "いいえ" },
                "はい");
        return option == JOptionPane.YES_OPTION;
    }

    /**
     * 完了通知ダイアログを表示
     *
     * @param message 完了メッセージ
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    public void showCompletionDialog(String message) {
        validateMessage(message);
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                createMessagePanel(message),
                DEFAULT_COMPLETE_TITLE,
                JOptionPane.INFORMATION_MESSAGE));
    }

    /**
     * メッセージの妥当性を検証
     *
     * @param message 検証対象のメッセージ
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }

    /**
     * メッセージパネルを作成
     * スクロール可能なテキストエリアを含むパネルを生成
     *
     * @param message 表示するメッセージ
     * @return 作成されたメッセージパネル
     */
    private JPanel createMessagePanel(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(UIManager.getColor("Panel.background"));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}