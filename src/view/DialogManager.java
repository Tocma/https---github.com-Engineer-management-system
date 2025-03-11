package view;

import javax.swing.*;

import util.LogHandler;
import util.MessageEnum;

import java.awt.*;

/**
 * アプリケーション全体のダイアログを管理するシングルトンクラス
 *
 * <p>
 * このクラスは、ユーザーとシステムの対話を担当し、統一的なダイアログ表示。
 * MessageEnumを活用して、一貫性のあるメッセージとレイアウト。
 * </p>
 *
 * <p>
 * 主要な機能：
 * <ul>
 * <li>エラー通知 - ユーザーに問題を通知し、対処を促す</li>
 * <li>確認ダイアログ - 重要な操作前にユーザーの意思を確認する</li>
 * <li>完了通知 - 操作の成功をユーザーに伝える</li>
 * <li>カスタマイズ可能なメッセージパネル - 長文や書式付きメッセージの表示</li>
 * </ul>
 * </p>
 *
 * <p>
 * ダイアログの表示は全てEvent Dispatch Thread (EDT)上で実行され、
 * 操作のログ記録と連携することで、ユーザー操作の追跡が可能になります。
 * また、メッセージパネルのカスタマイズにより、様々な表示ニーズに対応できます。
 * </p>
 *
 * <p>
 * 使用例：
 * 
 * <pre>
 * // エラーダイアログの表示
 * DialogManager.getInstance().showErrorDialog(MessageEnum.DIALOG_ERROR_FILE_ACCESS);
 *
 * // パラメータ付きエラーメッセージ
 * DialogManager.getInstance().showErrorDialog(MessageEnum.DIALOG_ERROR_DATA_LOAD, "顧客データ");
 *
 * // 確認ダイアログの表示と結果の取得
 * if (DialogManager.getInstance().showConfirmDialog(MessageEnum.DIALOG_CONFIRM_SAVE)) {
 *     // 「はい」が選択された場合の処理
 *     saveData();
 * }
 *
 * // 完了通知の表示
 * DialogManager.getInstance().showCompletionDialog(MessageEnum.DIALOG_COMPLETION_SAVE);
 * </pre>
 * </p>
 *
 * @author Nakano
 * @version 2.0.0
 * @since 2025-03-12
 */
public class DialogManager {

    /** シングルトンインスタンス */
    private static final DialogManager INSTANCE = new DialogManager();

    /** ダイアログ表示の設定 */
    private static final Dimension DEFAULT_DIALOG_SIZE = new Dimension(400, 150);
    private static final int MAX_MESSAGE_HEIGHT = 300;
    private static final Font DEFAULT_FONT = new Font("MS Gothic", Font.PLAIN, 12);

    /**
     * プライベートコンストラクタ
     * シングルトンパターンを実現するため、外部からのインスタンス化を防ぐ
     */
    private DialogManager() {
        // LogHandlerが初期化されている場合のみログ出力
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "DialogManager");
        }
    }

    /**
     * シングルトンインスタンスを取得
     *
     * @return DialogManagerの唯一のインスタンス
     */
    public static DialogManager getInstance() {
        return INSTANCE;
    }

    /**
     * エラーダイアログを表示（MessageEnumを使用）
     * 問題や警告をユーザーに通知
     *
     * @param messageEnum エラーメッセージの列挙定数
     * @param args        メッセージのフォーマットパラメータ
     * @throws IllegalArgumentException メッセージ列挙定数がnullの場合
     */
    public void showErrorDialog(MessageEnum messageEnum, Object... args) {
        validateMessageEnum(messageEnum);
        String formattedMessage = messageEnum.format(args);

        // ログ出力（LogHandlerが初期化されている場合のみ）
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "現在画面", "エラーダイアログ");
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                createMessagePanel(formattedMessage),
                MessageEnum.DIALOG_ERROR_TITLE.getMessage(),
                JOptionPane.ERROR_MESSAGE));
    }

    /**
     * エラーダイアログを表示（従来のString引数）
     * 後方互換性のために維持
     *
     * @param message エラーメッセージ
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    public void showErrorDialog(String message) {
        validateMessage(message);

        // ログ出力（LogHandlerが初期化されている場合のみ）
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "現在画面", "エラーダイアログ");
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                createMessagePanel(message),
                MessageEnum.DIALOG_ERROR_TITLE.getMessage(),
                JOptionPane.ERROR_MESSAGE));
    }

    /**
     * 確認ダイアログを表示（MessageEnumを使用）
     * ユーザーに操作の確認を要求
     *
     * @param messageEnum 確認メッセージの列挙定数
     * @param args        メッセージのフォーマットパラメータ
     * @return ユーザーが「はい」を選択した場合true
     * @throws IllegalArgumentException メッセージ列挙定数がnullの場合
     */
    public boolean showConfirmDialog(MessageEnum messageEnum, Object... args) {
        validateMessageEnum(messageEnum);
        String formattedMessage = messageEnum.format(args);

        // ログ出力（LogHandlerが初期化されている場合のみ）
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "現在画面", "確認ダイアログ");
        }

        int option = JOptionPane.showOptionDialog(
                null,
                createMessagePanel(formattedMessage),
                MessageEnum.DIALOG_CONFIRM_TITLE.getMessage(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] { "はい", "いいえ" },
                "はい");

        boolean result = option == JOptionPane.YES_OPTION;

        // 結果のログ出力
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "確認ダイアログ応答", result ? "はい" : "いいえ");
        }

        return result;
    }

    /**
     * 確認ダイアログを表示（従来のString引数）
     * 後方互換性のために維持
     *
     * @param message 確認メッセージ
     * @return ユーザーが「はい」を選択した場合true
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    public boolean showConfirmDialog(String message) {
        validateMessage(message);

        // ログ出力（LogHandlerが初期化されている場合のみ）
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "現在画面", "確認ダイアログ");
        }

        int option = JOptionPane.showOptionDialog(
                null,
                createMessagePanel(message),
                MessageEnum.DIALOG_CONFIRM_TITLE.getMessage(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] { "はい", "いいえ" },
                "はい");

        boolean result = option == JOptionPane.YES_OPTION;

        // 結果のログ出力
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "確認ダイアログ応答", result ? "はい" : "いいえ");
        }

        return result;
    }

    /**
     * 完了通知ダイアログを表示（MessageEnumを使用）
     * 処理の成功をユーザーに通知
     *
     * @param messageEnum 完了メッセージの列挙定数
     * @param args        メッセージのフォーマットパラメータ
     * @throws IllegalArgumentException メッセージ列挙定数がnullの場合
     */
    public void showCompletionDialog(MessageEnum messageEnum, Object... args) {
        validateMessageEnum(messageEnum);
        String formattedMessage = messageEnum.format(args);

        // ログ出力（LogHandlerが初期化されている場合のみ）
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "現在画面", "完了ダイアログ");
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                createMessagePanel(formattedMessage),
                MessageEnum.DIALOG_COMPLETION_TITLE.getMessage(),
                JOptionPane.INFORMATION_MESSAGE));
    }

    /**
     * 完了通知ダイアログを表示（従来のString引数）
     * 後方互換性のために維持
     *
     * @param message 完了メッセージ
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    public void showCompletionDialog(String message) {
        validateMessage(message);

        // ログ出力（LogHandlerが初期化されている場合のみ）
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SCREEN_TRANSITION,
                    "現在画面", "完了ダイアログ");
        }

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                createMessagePanel(message),
                MessageEnum.DIALOG_COMPLETION_TITLE.getMessage(),
                JOptionPane.INFORMATION_MESSAGE));
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
        panel.setPreferredSize(DEFAULT_DIALOG_SIZE);

        JTextArea textArea = new JTextArea(message);
        configureTextArea(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(
                DEFAULT_DIALOG_SIZE.width - 20,
                Math.min(textArea.getPreferredSize().height + 30, MAX_MESSAGE_HEIGHT)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * テキストエリアの設定
     * フォント、行折り返し、背景色などを設定
     *
     * @param textArea 設定対象のテキストエリア
     */
    private void configureTextArea(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(DEFAULT_FONT);
        textArea.setBackground(UIManager.getColor("Panel.background"));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * MessageEnum列挙定数の妥当性を検証
     *
     * @param messageEnum 検証対象のメッセージ列挙定数
     * @throws IllegalArgumentException メッセージ列挙定数がnullの場合
     */
    private void validateMessageEnum(MessageEnum messageEnum) {
        if (messageEnum == null) {
            throw new IllegalArgumentException("メッセージ列挙定数がnullです");
        }
    }

    /**
     * メッセージ文字列の妥当性を検証
     *
     * @param message 検証対象のメッセージ
     * @throws IllegalArgumentException メッセージがnullまたは空の場合
     */
    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("メッセージがnullまたは空です");
        }
    }
}