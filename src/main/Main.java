package main;

import controller.MainController;
import test.TestCoreSystem;
import test.TestCoreSystem.TestResult;
import util.LogHandler;
import util.MessageEnum;
import view.MainFrame;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;

/**
 * エンジニア人材管理システムのエントリーポイント
 * システムの初期化、実行、リソース管理、終了処理を担当
 *
 * <p>
 * このクラスは、アプリケーションのライフサイクル全体を管理：
 * <ul>
 * <li>ログシステムの初期化と設定</li>
 * <li>UIコンポーネントの初期化と表示</li>
 * <li>コントローラの初期化と実行</li>
 * <li>コマンドライン引数の解析</li>
 * <li>テストモードの制御</li>
 * <li>例外処理とエラーハンドリング</li>
 * <li>リソースの適切な解放</li>
 * </ul>
 * </p>
 *
 * <p>
 * コマンドライン引数：
 * <ul>
 * <li>--test : テストモードで起動（単体テストの実行）</li>
 * <li>--verbose : 詳細なログ出力を有効化</li>
 * </ul>
 * </p>
 *
 * <p>
 * 終了処理：
 * <ul>
 * <li>適切なシャットダウンフックによる安全な終了</li>
 * <li>未保存データの保護</li>
 * <li>ログファイルのクローズ</li>
 * </ul>
 * </p>
 *
 * @author Nakano
 * @version 2.0.0
 * @since 2025-03-12
 */
public class Main {

    /** ログディレクトリパス */
    private static final String LOG_DIR = "src/logs";

    /** 実行テストモードフラグ */
    private static boolean isTestMode = false;
    /** 詳細テストモードフラグ */
    private static boolean isVerboseMode = false;

    /** シャットダウンフック登録済みフラグ */
    private static boolean shutdownHookRegistered = false;

    /**
     * アプリケーションのエントリーポイント
     * コマンドライン引数を解析し、適切なモードで起動
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        try {
            // コマンドライン引数の解析
            parseArguments(args);

            // ログシステムの初期化（最優先）
            initializeLogger();

            // シャットダウンフックの登録（初回のみ）
            registerShutdownHook();

            // テストモードまたは通常モードで起動
            if (isTestMode) {
                runSystemTests();
            } else {
                SwingUtilities.invokeLater(Main::initializeApplication);
            }

        } catch (Exception e) {
            handleFatalError(e);
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
     * ログシステムを初期化
     * ログハンドラの設定とログディレクトリの作成
     *
     * @throws IOException 初期化に失敗した場合
     */
    private static void initializeLogger() throws IOException {
        LogHandler logger = LogHandler.getInstance();
        logger.initialize(LOG_DIR);

        if (isVerboseMode) {
            logger.log(Level.INFO, "詳細ログモードが有効化されました");
        }

        if (isTestMode) {
            logger.log(MessageEnum.LOG_INFO_SYSTEM_START, "テストモード");
        } else {
            logger.log(MessageEnum.LOG_INFO_SYSTEM_START);
        }
    }

    /**
     * シャットダウンフックを登録
     * アプリケーション終了時のクリーンアップ処理
     */
    private static void registerShutdownHook() {
        if (!shutdownHookRegistered) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_SHUTDOWN);
                cleanup();
            }));
            shutdownHookRegistered = true;
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "シャットダウンフック");
        }
    }

    /**
     * アプリケーションを初期化
     * GUIコンポーネントとコントローラの初期化
     */
    private static void initializeApplication() {
        try {
            // メインフレームの初期化
            MainFrame mainFrame = new MainFrame();

            // コントローラの初期化
            MainController mainController = new MainController(mainFrame);
            mainController.initialize();

            // メインフレームの表示
            mainFrame.setVisible(true);

            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "アプリケーション");

        } catch (Exception e) {
            handleFatalError(e);
        }
    }

    /**
     * システムテストを実行
     * テストケースの実行と結果のレポート生成
     */
    private static void runSystemTests() {
        try {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "テスト実行");

            System.out.println("システムテストを開始します...\n");

            // テストの実行
            List<TestResult> results = TestCoreSystem.runAllTests();

            // テスト結果の生成と表示
            generateTestReport(results);

            // 終了コードの設定
            boolean allTestsPassed = results.stream()
                    .allMatch(TestResult::isSuccess);

            LogHandler.getInstance().log(
                    allTestsPassed
                            ? MessageEnum.LOG_INFO_SYSTEM_START
                            : MessageEnum.LOG_ERROR_VALIDATION_FAILED,
                    "テスト完了");

            System.exit(allTestsPassed ? 0 : 1);

        } catch (Exception e) {
            LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "テスト実行中");
            System.exit(2);
        }
    }

    /**
     * テスト結果レポートを生成
     *
     * @param results テスト結果のリスト
     */
    private static void generateTestReport(List<TestResult> results) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("\n=== テスト実行結果レポート ===");
        System.out.printf("実行時刻: %s%n", now.format(formatter));
        System.out.printf("総テスト数: %d%n%n", results.size());

        int successCount = 0;

        for (int i = 0; i < results.size(); i++) {
            TestResult result = results.get(i);
            if (result.isSuccess()) {
                successCount++;
            }

            System.out.printf("テストケース %d:%n", i + 1);
            System.out.printf("結果: %s%n", result.isSuccess() ? "成功" : "失敗");
            System.out.printf("メッセージ: %s%n%n", result.getMessage());

            if (!result.isSuccess() && result.getException() != null) {
                result.getException().printStackTrace(System.out);
            }
        }

        System.out.println("=== サマリー ===");
        System.out.printf("成功: %d%n", successCount);
        System.out.printf("失敗: %d%n", results.size() - successCount);
        System.out.printf("成功率: %.1f%%%n",
                (double) successCount / results.size() * 100);
    }

    /**
     * 致命的なエラーを処理
     * エラーのログ記録とクリーンアップを行います
     *
     * @param e 発生した例外
     */
    private static void handleFatalError(Exception e) {
        // エラーログの記録
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "システム起動中");
        } else {
            System.err.println("システム起動中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }

        // 終了処理
        cleanup();
        System.exit(1);
    }

    /**
     * クリーンアップ処理
     * リソースの解放
     */
    private static void cleanup() {
        try {
            if (LogHandler.getInstance().isInitialized()) {
                LogHandler.getInstance().cleanup();
            }
        } catch (Exception e) {
            System.err.println("クリーンアップ処理に失敗しました: " + e.getMessage());
        }
    }
}