package main;

import java.io.IOException;
import java.util.logging.Level;

import test.TestLogHandler;
import util.LogHandler;

/**
 * エンジニア情報管理システムのメインアプリケーションクラス
 * LogHandlerの初期化とテスト実行を管理
 *
 * <p>
 * このクラスは以下の主要な機能を実装：
 * </p>
 * <ul>
 * <li>LogHandlerの初期化と基本設定</li>
 * <li>TestLogHandlerクラスの各テストケースの個別実行</li>
 * <li>ログローテーションを含むログ機能の動作検証</li>
 * </ul>
 *
 * <p>
 * 使用例：
 * </p>
 *
 * <pre>
 * public static void main(String[] args) {
 *     Main app = new Main();
 *     try {
 *         // LogHandlerの初期化
 *         app.initializeLogger();
 *
 *         // 特定のテストケースを実行
 *         app.runTest("testBasicLogging");
 *
 *         // ログローテーションのテスト
 *         app.testLogRotation();
 *     } catch (Exception e) {
 *         System.err.println("アプリケーションエラー: " + e.getMessage());
 *     }
 * }
 * </pre>
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-01-24
 */
public class Main {
    /** LogHandlerインスタンス */
    private LogHandler logHandler;

    /** テストケース実行用のインスタンス */
    private TestLogHandler testLogHandler;

    /** ログローテーション検証用の定数 */
    private static final int ROTATION_TEST_ITERATIONS = 1000;
    private static final String TEST_MESSAGE = "ログローテーション検証用メッセージ";

    /**
     * メインメソッド
     * アプリケーションのエントリーポイントとして、LogHandlerの初期化とテスト実行
     *
     * @param args コマンドライン引数（現在は使用していません）なし
     */
    public static void main(String[] args) {
        Main app = new Main();
        try {
            app.initializeLogger();
            app.runAllTests();
        } catch (Exception e) {
            System.err.println("アプリケーションエラー: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * コンストラクタ
     * LogHandlerとTestLogHandlerのインスタンスを初期化
     */
    public Main() {
        this.logHandler = LogHandler.getInstance();
        this.testLogHandler = new TestLogHandler();
    }

    /**
     * LogHandlerを初期化
     * デフォルトのログディレクトリを使用してログ機能を設定
     *
     * @throws IOException ログの初期化に失敗した場合
     */
    public void initializeLogger() throws IOException {
        logHandler.initialize("C:/logs");
        logHandler.log(Level.INFO, "アプリケーションを開始します");
    }

    /**
     * すべてのテストケースを実行
     * 各テストケースの実行結果をログに記録
     */
    public void runAllTests() {
        try {
            runTest("testBasicLogging");
            runTest("testErrorLogging");
            runTest("testNullInputHandling");
            testLogRotation();

            logHandler.log(Level.INFO, "全テストケースの実行が完了しました");
        } catch (Exception e) {
            logHandler.logError("テスト実行中にエラーが発生しました", e);
        }
    }

    /**
     * 指定されたテストケースを実行
     * テストの実行結果をログに記録
     *
     * @param testName 実行するテストケースの名前
     * @throws Exception テストの実行に失敗した場合
     */
    public void runTest(String testName) throws Exception {
        logHandler.log(Level.INFO, String.format("テストケース '%s' を開始します", testName));

        try {
            switch (testName) {
                case "testBasicLogging":
                    testLogHandler.testBasicLogging();
                    break;
                case "testErrorLogging":
                    testLogHandler.testErrorLogging();
                    break;
                case "testNullInputHandling":
                    testLogHandler.testNullInputHandling();
                    break;
                default:
                    throw new IllegalArgumentException("未知のテストケース: " + testName);
            }

            logHandler.log(Level.INFO, String.format("テストケース '%s' が成功しました", testName));
        } catch (Exception e) {
            logHandler.logError(String.format("テストケース '%s' が失敗しました", testName), e);
            throw e;
        }
    }

    /**
     * ログローテーション機能のテストを実行
     * 大量のログメッセージを出力してログファイルのローテーションをトリガー
     *
     * <p>
     * このテストでは以下を検証：
     * </p>
     * <ul>
     * <li>ログファイルサイズの上限（10MB）到達時のローテーション</li>
     * <li>ローテーション後の新規ログファイル作成</li>
     * <li>ログメッセージの継続的な書き込み</li>
     * </ul>
     */
    public void testLogRotation() {
        logHandler.log(Level.INFO, "ログローテーションテストを開始します");

        try {
            for (int i = 0; i < ROTATION_TEST_ITERATIONS; i++) {
                String message = String.format("%s - iteration %d", TEST_MESSAGE, i);
                // 大きなメッセージを作成してログファイルサイズを増加させる
                StringBuilder largeMessage = new StringBuilder(message);
                for (int j = 0; j < 1000; j++) {
                    largeMessage.append(" ").append(j);
                }
                logHandler.log(Level.INFO, largeMessage.toString());
            }

            logHandler.log(Level.INFO, "ログローテーションテストが完了しました");
        } catch (Exception e) {
            logHandler.logError("ログローテーションテスト中にエラーが発生しました", e);
        }
    }

    /**
     * アプリケーションのクリーンアップ
     * LogHandlerのリソースを解放
     */
    public void cleanup() {
        logHandler.log(Level.INFO, "アプリケーションを終了します");
        logHandler.cleanup();
    }
}