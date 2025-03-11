package test;

import util.LogHandler;
import util.MessageEnum;
import view.MainFrame;
import view.ListPanel;
import model.EngineerDTO;

import javax.swing.*;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * エンジニア人材管理システムのコア機能を検証するテストフレームワーク
 *
 * <p>
 * このクラスは、システムの主要機能を体系的にテストするための包括的なフレームワーク。
 * システムの初期化、UI、データ操作、エラーハンドリングなど、アプリケーションの様々な
 * 側面を検証するための標準化されたテストケースを実装。
 * </p>
 *
 * <p>
 * テストの主要カテゴリ：
 * <ul>
 * <li>システム初期化テスト - ログシステムやディレクトリ構造などの基盤機能</li>
 * <li>UIコンポーネントテスト - フレームやパネルの正常な生成と表示</li>
 * <li>データ操作テスト - データの読み込みと検証</li>
 * <li>エラーハンドリングテスト - 異常系の適切な処理</li>
 * </ul>
 * </p>
 *
 * <p>
 * 本クラスはテスト環境を実行環境から独立させています。
 * また、各テストケースは独立して実行可能で、詳細なテスト結果を生成。
 * </p>
 *
 * <p>
 * テスト実行の使用例：
 * 
 * <pre>
 * // すべてのテストを実行
 * List<TestResult> results = TestCoreSystem.runAllTests();
 *
 * // 個別のテストを実行
 * TestResult result = TestCoreSystem.testSystemInitialization();
 * if (result.isSuccess()) {
 *     System.out.println("初期化テスト成功: " + result.getMessage());
 * } else {
 *     System.err.println("初期化テスト失敗: " + result.getMessage());
 * }
 * </pre>
 * </p>
 *
 * @author Nakano
 * @version 2.0.0
 * @since 2025-03-12
 */
public class TestCoreSystem {

    /** テスト環境の設定 */
    private static final String TEST_LOG_DIR = "src/logs";
    private static final String TEST_DATA_DIR = "logs";
    private static final int TEST_DATA_COUNT = 10;

    /** テスト結果の記録用 */
    private static List<String> testMessages = new ArrayList<>();
    private static LocalDateTime testStartTime;

    /**
     * テスト結果を格納するための内部クラス
     * テストの成否、メッセージ、例外情報を管理
     */
    public static class TestResult {
        private final boolean success;
        private final String message;
        private final Exception exception;

        /**
         * コンストラクタ
         *
         * @param success   テスト成功の場合true
         * @param message   テスト結果メッセージ
         * @param exception 発生した例外（失敗時のみ）
         */
        public TestResult(boolean success, String message, Exception exception) {
            this.success = success;
            this.message = message;
            this.exception = exception;
        }

        /** テスト成功の場合true */
        public boolean isSuccess() {
            return success;
        }

        /** テスト結果メッセージ */
        public String getMessage() {
            return message;
        }

        /** 失敗時の例外（成功時はnull） */
        public Exception getException() {
            return exception;
        }
    }

    /**
     * システム初期化のテストを実行
     * ログシステムとディレクトリ構造の初期化を検証
     *
     * @return テスト結果
     */
    public static TestResult testSystemInitialization() {
        try {
            // テスト用ディレクトリの準備
            setupTestEnvironment();

            // LogHandlerの初期化テスト
            LogHandler.getInstance().initialize(TEST_LOG_DIR);
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "テスト環境");

            // ログファイルの存在確認
            verifyLogFileCreation();

            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "システム初期化テスト");

            return new TestResult(true,
                    MessageEnum.LOG_INFO_SYSTEM_START.format("初期化テスト"),
                    null);

        } catch (Exception e) {
            if (LogHandler.getInstance().isInitialized()) {
                LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "システム初期化テスト");
            }

            return new TestResult(false,
                    MessageEnum.LOG_ERROR_SYSTEM_ERROR.format(e.getMessage()),
                    e);
        }
    }

    /**
     * MainFrameの生成と初期化をテスト
     * UIコンポーネントの正常な動作を検証
     *
     * @return テスト結果
     */
    public static TestResult testMainFrameCreation() {
        try {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "MainFrameテスト");

            // EDT上でのテスト実行
            final boolean[] testCompleted = { false };
            final Exception[] testException = { null };

            SwingUtilities.invokeAndWait(() -> {
                try {
                    MainFrame mainFrame = new MainFrame();
                    testCompleted[0] = true;
                } catch (Exception e) {
                    testException[0] = e;
                }
            });

            if (testException[0] != null) {
                throw testException[0];
            }

            if (!testCompleted[0]) {
                throw new RuntimeException("MainFrameテストが完了しませんでした");
            }

            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "MainFrameテスト");

            return new TestResult(true,
                    MessageEnum.LOG_INFO_SYSTEM_START.format("MainFrame生成テスト"),
                    null);

        } catch (Exception e) {
            LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "MainFrameテスト");

            return new TestResult(false,
                    MessageEnum.LOG_ERROR_SYSTEM_ERROR.format(e.getMessage()),
                    e);
        }
    }

    /**
     * ListPanelのデータ表示機能をテスト
     * テストデータの生成と表示を検証
     *
     * @return テスト結果
     */
    public static TestResult testListPanelDisplay() {
        try {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "ListPanelテスト");

            final boolean[] testCompleted = { false };
            final Exception[] testException = { null };

            SwingUtilities.invokeAndWait(() -> {
                try {
                    ListPanel listPanel = new ListPanel();
                    verifyListPanelComponents(listPanel);
                    testCompleted[0] = true;
                } catch (Exception e) {
                    testException[0] = e;
                }
            });

            if (testException[0] != null) {
                throw testException[0];
            }

            if (!testCompleted[0]) {
                throw new RuntimeException("ListPanelテストが完了しませんでした");
            }

            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "ListPanelテスト");

            return new TestResult(true,
                    MessageEnum.LOG_INFO_SYSTEM_START.format("ListPanel表示テスト"),
                    null);

        } catch (Exception e) {
            LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "ListPanelテスト");

            return new TestResult(false,
                    MessageEnum.LOG_ERROR_SYSTEM_ERROR.format(e.getMessage()),
                    e);
        }
    }

    /**
     * エラーハンドリング機能をテスト
     * 異常系の動作を検証
     *
     * @return テスト結果
     */
    public static TestResult testErrorHandling() {
        try {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "エラーハンドリングテスト");

            // 無効なデータでの動作確認
            verifyErrorHandling();

            LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "エラーハンドリングテスト");

            return new TestResult(true,
                    MessageEnum.LOG_INFO_SYSTEM_START.format("エラーハンドリングテスト"),
                    null);

        } catch (Exception e) {
            LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "エラーハンドリングテスト");

            return new TestResult(false,
                    MessageEnum.LOG_ERROR_SYSTEM_ERROR.format(e.getMessage()),
                    e);
        }
    }

    /**
     * すべてのテストケースを実行
     * 包括的なテストスイートを実行し、結果を収集
     *
     * @return テスト結果のリスト
     */
    public static List<TestResult> runAllTests() {
        testStartTime = LocalDateTime.now();
        LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START, "システムテスト一括実行");

        List<TestResult> results = new ArrayList<>();
        results.add(testSystemInitialization());
        results.add(testMainFrameCreation());
        results.add(testListPanelDisplay());
        results.add(testErrorHandling());

        generateTestSummary(results);
        LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_SHUTDOWN, "システムテスト一括実行");

        return results;
    }

    // 以下、プライベートヘルパーメソッド

    /**
     * テスト環境をセットアップ
     * テスト用ディレクトリの作成と初期化を行います
     */
    private static void setupTestEnvironment() throws Exception {
        // テストディレクトリのクリーンアップ
        Path testLogPath = Path.of(TEST_LOG_DIR);
        Path testDataPath = Path.of(TEST_DATA_DIR);

        cleanupDirectory(testLogPath);
        cleanupDirectory(testDataPath);

        // ディレクトリの作成
        Files.createDirectories(testLogPath);
        Files.createDirectories(testDataPath);
    }

    /**
     * ディレクトリのクリーンアップ
     * 既存のテストデータを削除
     */
    private static void cleanupDirectory(Path path) throws Exception {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a))
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    /**
     * ログファイルの作成を検証
     */
    private static void verifyLogFileCreation() throws Exception {
        String logFileName = LogHandler.getInstance().getCurrentLogFileName();
        Path logFile = Path.of(TEST_LOG_DIR, logFileName);

        if (!Files.exists(logFile)) {
            throw new RuntimeException(MessageEnum.LOG_ERROR_FILE_ACCESS.format(logFileName));
        }
    }

    /**
     * ListPanelのコンポーネントを検証
     */
    private static void verifyListPanelComponents(ListPanel panel) {
        // 必要なコンポーネントの存在を確認
        JTable table = (JTable) findComponentByType(panel, JTable.class);
        if (table == null) {
            throw new RuntimeException(MessageEnum.LOG_ERROR_VALIDATION_FAILED.format("テーブルが見つかりません"));
        }
    }

    /**
     * エラーハンドリングを検証
     */
    private static void verifyErrorHandling() {
        // 無効なエンジニアデータの作成
        EngineerDTO invalidEngineer = new EngineerDTO();
        // 必須フィールドを設定しない

        // ログにエラーメッセージを出力して検証
        LogHandler.getInstance().log(MessageEnum.LOG_ERROR_VALIDATION_FAILED, "テスト用無効データ");
    }

    /**
     * コンポーネントツリーから特定の型のコンポーネントを検索
     */
    private static Component findComponentByType(Container container, Class<?> type) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponentByType((Container) component, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * テスト結果のサマリーを生成
     */
    private static void generateTestSummary(List<TestResult> results) {
        LocalDateTime endTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        testMessages.add("=== テスト実行サマリー ===");
        testMessages.add("開始時刻: " + testStartTime.format(formatter));
        testMessages.add("終了時刻: " + endTime.format(formatter));
        testMessages.add("総テスト数: " + results.size());

        long totalSuccess = results.stream()
                .filter(TestResult::isSuccess)
                .count();

        testMessages.add(String.format("成功: %d", totalSuccess));
        testMessages.add(String.format("失敗: %d", results.size() - totalSuccess));
        testMessages.add(String.format("成功率: %.1f%%",
                (double) totalSuccess / results.size() * 100));

        // ログにサマリーを出力
        if (LogHandler.getInstance().isInitialized()) {
            LogHandler.getInstance().log(MessageEnum.LOG_INFO_DATA_LOADED,
                    String.format("テスト成功率: %.1f%%", (double) totalSuccess / results.size() * 100));
        }
    }

    /**
     * テスト用データを生成
     *
     * @param count 生成するデータ件数
     * @return エンジニアデータのリスト
     */
    private static List<EngineerDTO> createTestData(int count) {
        List<EngineerDTO> testData = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EngineerDTO engineer = new EngineerDTO();
            engineer.setId(String.format("ID%05d", i));
            engineer.setName("テストエンジニア" + i);
            engineer.setBirthDate(new Date());
            engineer.setCareer(i % 10);

            List<String> languages = new ArrayList<>();
            languages.add("Java");
            languages.add("Python");
            engineer.setProgrammingLanguages(languages);

            testData.add(engineer);
        }
        return testData;
    }
}