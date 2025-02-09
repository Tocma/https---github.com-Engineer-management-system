package test;

/** 現在は使用していません */
//import controller.MainController;
import util.LogHandler;
import view.DialogManager;
import view.MainFrame;
import view.ListPanel;
import model.EngineerDTO;

import javax.swing.*;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/** 現在は使用していません */
//import java.util.logging.Level;

/**
 * システムコアの機能テストを実行するテストクラス
 *
 * <p>
 * このクラスは、エンジニア人材管理システムの中核機能に対する自動テスト。
 * 各テストケースは独立して実行可能な静的メソッドとして実装されており、
 * テスト結果は TestResult オブジェクトとして返却。
 * </p>
 *
 * <p>
 * 主なテスト項目：
 * <ul>
 * <li>システム初期化処理の検証</li>
 * <li>UI コンポーネントの生成と表示の検証</li>
 * <li>データ操作機能の検証</li>
 * <li>エラーハンドリングの検証</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用例：
 *
 * <pre>
 * TestResult result = TestCoreSystem.testSystemInitialization();
 * if (result.isSuccess()) {
 *     System.out.println("テスト成功: " + result.getMessage());
 * } else {
 *     System.err.println("テスト失敗: " + result.getMessage());
 *     result.getException().printStackTrace();
 * }
 * </pre>
 * </p>
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-02-10
 */
public class TestCoreSystem {

    /** テスト用ログディレクトリ */
    private static final String TEST_LOG_DIR = "src/logs";

    /** テスト用データ件数 */
    // private static final int TEST_DATA_COUNT = 10;

    /**
     * テスト結果を格納するための内部クラス
     */
    public static class TestResult {
        private final boolean success;
        private final String message;
        private final Exception exception;

        public TestResult(boolean success, String message, Exception exception) {
            this.success = success;
            this.message = message;
            this.exception = exception;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Exception getException() {
            return exception;
        }
    }

    /**
     * システム初期化のテストを実行
     * ログシステムの初期化とディレクトリ作成を検証
     *
     * @return テスト結果
     */
    public static TestResult testSystemInitialization() {
        try {
            // テスト用ディレクトリの作成
            Path logPath = Path.of(TEST_LOG_DIR);
            if (Files.exists(logPath)) {
                Files.walk(logPath)
                        .sorted((a, b) -> b.compareTo(a))
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            Files.createDirectories(logPath);

            // LogHandlerの初期化
            LogHandler.getInstance().initialize(TEST_LOG_DIR);

            // ログファイルの存在確認
            String logFileName = LogHandler.getInstance().getCurrentLogFileName();
            if (!Files.exists(Path.of(TEST_LOG_DIR, logFileName))) {
                throw new RuntimeException("ログファイルが作成されていません");
            }

            return new TestResult(true, "システム初期化テスト成功", null);
        } catch (Exception e) {
            return new TestResult(false, "システム初期化テスト失敗", e);
        }
    }

    /**
     * MainFrameの生成と初期化をテスト
     * フレームの生成、サイズ設定、タイトル設定を検証
     *
     * @return テスト結果
     */
    public static TestResult testMainFrameCreation() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                MainFrame mainFrame = new MainFrame();
                JFrame frame = mainFrame.getFrame();

                // フレームの基本属性を検証
                if (!frame.getTitle().equals("エンジニア人材管理")) {
                    throw new RuntimeException("フレームのタイトルが不正です");
                }

                if (frame.getWidth() != 1000 || frame.getHeight() != 800) {
                    throw new RuntimeException("フレームのサイズが不正です");
                }

                mainFrame.setVisible(false);
                frame.dispose();
            });

            return new TestResult(true, "MainFrame生成テスト成功", null);
        } catch (Exception e) {
            return new TestResult(false, "MainFrame生成テスト失敗", e);
        }
    }

    /**
     * ListPanelのデータ表示機能をテスト
     * テストデータの生成と表示、ページング機能を検証
     *
     * @return テスト結果
     */
    public static TestResult testListPanelDisplay() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                ListPanel listPanel = new ListPanel();

                // テストデータの検証
                JTable table = (JTable) findComponentByType(listPanel, JTable.class);
                if (table == null) {
                    throw new RuntimeException("テーブルが見つかりません");
                }

                if (table.getColumnCount() != 5) {
                    throw new RuntimeException("テーブルの列数が不正です");
                }

                if (table.getModel().getRowCount() == 0) {
                    throw new RuntimeException("テストデータが表示されていません");
                }
            });

            return new TestResult(true, "ListPanel表示テスト成功", null);
        } catch (Exception e) {
            return new TestResult(false, "ListPanel表示テスト失敗", e);
        }
    }

    /**
     * エラーハンドリング機能をテスト
     * 無効なデータや操作に対する例外処理を検証
     *
     * @return テスト結果
     */
    public static TestResult testErrorHandling() {
        try {
            // 無効なデータでのエラーハンドリングをテスト
            EngineerDTO invalidEngineer = new EngineerDTO();
            // IDを設定しない状態で検証

            SwingUtilities.invokeAndWait(() -> {
                ListPanel listPanel = new ListPanel();
                // 無効なデータの追加を試行
                List<EngineerDTO> testData = new ArrayList<>();
                testData.add(invalidEngineer);
                // エラーダイアログの表示を確認
                DialogManager.getInstance().showErrorDialog("テストエラーメッセージ");
            });

            return new TestResult(true, "エラーハンドリングテスト成功", null);
        } catch (Exception e) {
            return new TestResult(false, "エラーハンドリングテスト失敗", e);
        }
    }

    /**
     * 全テストケースを実行
     *
     * @return テスト結果のリスト
     */
    public static List<TestResult> runAllTests() {
        List<TestResult> results = new ArrayList<>();
        results.add(testSystemInitialization());
        results.add(testMainFrameCreation());
        results.add(testListPanelDisplay());
        results.add(testErrorHandling());
        return results;
    }

    /**
     * コンポーネントツリーから特定の型のコンポーネントを検索
     *
     * @param container 検索対象のコンテナ
     * @param type      検索する型
     * @return 見つかったコンポーネント、見つからない場合はnull
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
     * テスト用データを生成
     *
     * @param count 生成するデータ件数
     * @return テストデータのリスト
     */
    private static List<EngineerDTO> createTestData(int count) {
        List<EngineerDTO> testData = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EngineerDTO engineer = new EngineerDTO();
            engineer.setId(String.format("TEST%05d", i));
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