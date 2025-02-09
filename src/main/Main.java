package main;

import controller.MainController;
import test.TestCoreSystem;
import test.TestCoreSystem.TestResult;
import util.LogHandler;
import view.MainFrame;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.logging.Level;

/**
 * アプリケーションのエントリーポイント
 * システムの初期化、起動、およびテスト実行を管理
 *
 * <p>
 * このクラスの機能：
 * - システムの初期化と起動
 * - ログシステムの設定
 * - システムテストの実行と結果レポート
 * </p>
 *
 * <p>
 * コマンドライン引数：
 * --test : テストモードで起動
 * --verbose : 詳細なログ出力を有効化
 * </p>
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-02-10
 */
public class Main {

    /** ログディレクトリパス */
    private static final String LOG_DIR = "src/logs";

    /** テストモードフラグ */
    private static boolean isTestMode = false;

    /** 詳細ログモードフラグ */
    private static boolean isVerboseMode = false;

    /**
     * メインメソッド
     * コマンドライン引数を解析し、適切なモードでアプリケーションを起動
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        try {
            parseArguments(args);
            initializeLogger();

            if (isTestMode) {
                runSystemTests();
            } else {
                SwingUtilities.invokeLater(Main::initializeApplication);
            }
        } catch (Exception e) {
            System.err.println("アプリケーションの起動に失敗しました: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * コマンドライン引数を解析
     * 実行モードとログレベルを設定
     *
     * @param args コマンドライン引数
     */
    private static void parseArguments(String[] args) {
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "--test" -> isTestMode = true;
                case "--verbose" -> isVerboseMode = true;
                default -> System.out.println("未知の引数を無視します: " + arg);
            }
        }
    }

    /**
     * システムテストを実行
     * テスト結果を収集してレポートを生成
     */
    private static void runSystemTests() {
        try {
            System.out.println("システムテストを開始します...\n");
            List<TestResult> results = TestCoreSystem.runAllTests();
            generateTestReport(results);

            // テスト結果に基づいて終了コードを設定
            boolean allTestsPassed = results.stream().allMatch(TestResult::isSuccess);
            System.exit(allTestsPassed ? 0 : 1);

        } catch (Exception e) {
            LogHandler.getInstance().logError("テスト実行中にエラーが発生しました", e);
            System.exit(2);
        }
    }

    /**
     * テスト結果レポートを生成して表示
     *
     * @param results テスト結果のリスト
     */
    private static void generateTestReport(List<TestResult> results) {
        System.out.println("=== テスト実行結果レポート ===");
        System.out.println("実行時刻: " + new java.util.Date());
        System.out.println("総テスト数: " + results.size());

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < results.size(); i++) {
            TestResult result = results.get(i);
            if (result.isSuccess()) {
                successCount++;
            } else {
                failureCount++;
            }

            System.out.printf("\nテストケース %d:\n", i + 1);
            System.out.println("結果: " + (result.isSuccess() ? "成功" : "失敗"));
            System.out.println("メッセージ: " + result.getMessage());

            if (!result.isSuccess() && result.getException() != null) {
                System.out.println("エラー詳細:");
                result.getException().printStackTrace(System.out);
            }
        }

        System.out.println("\n=== サマリー ===");
        System.out.println("成功: " + successCount);
        System.out.println("失敗: " + failureCount);
        System.out.println("成功率: " +
                String.format("%.1f%%", (double) successCount / results.size() * 100));
        System.out.println("==================");
    }

    /**
     * ログシステムを初期化
     * テストモードと詳細モードに応じてログレベルを設定
     */
    private static void initializeLogger() throws Exception {
        LogHandler logger = LogHandler.getInstance();
        logger.initialize(LOG_DIR);

        if (isVerboseMode) {
            logger.log(Level.CONFIG, "詳細ログモードが有効化されました");
        }

        if (isTestMode) {
            logger.log(Level.INFO, "テストモードでアプリケーションを起動します");
        }
    }

    /**
     * アプリケーションを初期化
     * 通常モードでの起動時に実行
     */
    private static void initializeApplication() {
        try {
            MainFrame mainFrame = new MainFrame();
            MainController mainController = new MainController(mainFrame);

            mainController.initialize();
            mainFrame.setVisible(true);

            LogHandler.getInstance().log(Level.INFO, "アプリケーションを起動しました");
        } catch (Exception e) {
            LogHandler.getInstance().logError("アプリケーションの初期化に失敗しました", e);
            System.exit(1);
        }
    }
}