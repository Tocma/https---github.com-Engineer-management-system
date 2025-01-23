package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.logging.*;

/**
 * エンジニア情報管理システムのログ管理を行うシングルトンクラス
 * java.util.loggingを使用して、アプリケーション全体で一貫したログ出力
 *
 * <p>
 * ログファイルは日単位で管理され、アプリケーション起動時の日付に基づいて新しいログファイルが
 * 作成されます。ログファイルの命名規則は "System-YYYY-MM-DD.log"
 * </p>
 *
 * <p>
 * ログディレクトリが存在しない場合、絶対パスで指定されたローカルディスクに自動的に
 * logsフォルダが作成
 * </p>
 *
 * <p>
 * 使用例：
 * </p>
 *
 * <pre>
 * LogHandler logger = LogHandler.getInstance();
 * logger.initialize(); // デフォルトのログディレクトリ(logs)を使用
 *
 * // または、カスタムディレクトリを指定して初期化
 * logger.initialize("C:/logs");
 *
 * // ログの出力
 * logger.log(Level.INFO, "処理を開始します");
 * try {
 *     // 処理
 * } catch (Exception e) {
 *     logger.logError("エラーが発生しました", e);
 * }
 * </pre>
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-01-24
 */
public class LogHandler {
    /** シングルトンインスタンス */
    private static final LogHandler INSTANCE = new LogHandler();

    /** ログ関連の定数定義 */
    private static final String DEFAULT_LOG_DIR = "logs";
    private static final String LOG_FILE_FORMAT = "System-%s.log";
    private static final int MAX_LOG_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    /** ロガー設定 */
    private Logger logger;
    private boolean initialized;
    private String logDirectory;
    private FileHandler fileHandler;

    /** ログフォーマット */
    private static final String LOG_FORMAT = "[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS] [%4$s] %5$s%6$s%n";

    /**
     * プライベートコンストラクタ
     * シングルトンパターンを実現するため、外部からのインスタンス化を防ぐこと
     */
    private LogHandler() {
    }

    /**
     * LogHandlerのインスタンスを取得
     *
     * @return LogHandlerの唯一のインスタンス
     */
    public static LogHandler getInstance() {
        return INSTANCE;
    }

    /**
     * デフォルトのログディレクトリ（logs）を使用してロガーを初期化
     *
     * @throws IOException ログディレクトリの作成や設定に失敗した場合
     */
    public synchronized void initialize() throws IOException {
        initialize(DEFAULT_LOG_DIR);
    }

    /**
     * 指定されたログディレクトリを使用してロガーを初期化
     * 初期化処理は以下の手順で実行：
     * <ol>
     * <li>ログディレクトリの作成確認（存在しない場合は作成）</li>
     * <li>ログ設定の初期化</li>
     * <li>ログフォーマッタの設定</li>
     * </ol>
     * 
     * @param logDir ログファイルを格納するディレクトリパス
     * @throws IOException              ログディレクトリの作成や設定に失敗した場合
     * @throws IllegalArgumentException ログディレクトリのパスがnullまたは空の場合
     */
    public synchronized void initialize(String logDir) throws IOException {
        if (logDir == null || logDir.trim().isEmpty()) {
            throw new IllegalArgumentException("Log directory path cannot be null or empty");
        }

        if (initialized) {
            return;
        }

        try {
            this.logDirectory = setupLogDirectory(logDir);
            configureLogger();
            initialized = true;
            log(Level.INFO, "LogHandler initialized successfully");
        } catch (IOException e) {
            throw new IOException("Failed to initialize LogHandler", e);
        }
    }

    /**
     * ログディレクトリを設定
     * 指定されたディレクトリが存在しない場合は絶対パスで作成
     *
     * @param logDir ログディレクトリのパス
     * @return 作成されたログディレクトリの絶対パス
     * @throws IOException ディレクトリの作成に失敗した場合
     */
    private String setupLogDirectory(String logDir) throws IOException {
        Path logPath = Paths.get(logDir).toAbsolutePath();
        if (!Files.exists(logPath)) {
            Files.createDirectories(logPath);
        }
        return logPath.toString();
    }

    /**
     * ロガーの設定
     * 現在の日付に基づいてログファイル名を生成し、
     * ログレベルやフォーマットなどの基本設定
     *
     * @throws IOException 設定に失敗した場合
     */
    private void configureLogger() throws IOException {
        // ロガーの取得
        logger = Logger.getLogger(LogHandler.class.getName());
        logger.setLevel(Level.ALL);

        // 既存のハンドラをすべて削除
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        // 現在の日付でログファイル名を生成
        String currentDate = LocalDate.now().toString();
        String logFileName = String.format(LOG_FILE_FORMAT, currentDate);
        String logFilePath = logDirectory + File.separator + logFileName;

        // FileHandlerの設定
        fileHandler = new FileHandler(logFilePath, MAX_LOG_SIZE_BYTES, 1, true);
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format(LOG_FORMAT,
                        record.getMillis(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getLevel().getName(),
                        record.getMessage(),
                        record.getThrown() == null ? "" : "\n" + formatException(record.getThrown()));
            }

            private String formatException(Throwable thrown) {
                StringBuilder sb = new StringBuilder();
                sb.append(thrown.toString());
                for (StackTraceElement element : thrown.getStackTrace()) {
                    sb.append("\n\tat ").append(element.toString());
                }
                return sb.toString();
            }
        });

        // ハンドラの追加
        logger.addHandler(fileHandler);
    }

    /**
     * 指定されたレベルでログを記録
     *
     * @param level   ログレベル
     * @param message ログメッセージ
     * @throws IllegalStateException    LogHandlerが初期化されていない場合
     * @throws IllegalArgumentException メッセージがnullの場合
     */
    public synchronized void log(Level level, String message) {
        checkInitialized();
        if (message == null) {
            throw new IllegalArgumentException("Log message cannot be null");
        }
        logger.log(level, message);
    }

    /**
     * エラーログを記録
     * エラーメッセージと共に例外のスタックトレース情報も記録
     *
     * @param message   エラーメッセージ
     * @param throwable 発生した例外
     * @throws IllegalStateException    LogHandlerが初期化されていない場合
     * @throws IllegalArgumentException メッセージまたは例外がnullの場合
     */
    public synchronized void logError(String message, Throwable throwable) {
        checkInitialized();
        if (message == null || throwable == null) {
            throw new IllegalArgumentException("Message and throwable cannot be null");
        }
        logger.log(Level.SEVERE, message, throwable);
    }

    /**
     * 初期化状態をチェック
     *
     * @throws IllegalStateException 初期化されていない場合
     */
    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("LogHandler is not initialized");
        }
    }

    /**
     * 現在のログファイル名を取得
     *
     * @return 現在の日付に対応するログファイル名
     */
    public String getCurrentLogFileName() {
        return String.format(LOG_FILE_FORMAT, LocalDate.now().toString());
    }

    /**
     * ロガーのクリーンアップ
     * アプリケーション終了時に呼び出すことを推奨とのこと
     */
    public synchronized void cleanup() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }

    /**
     * 初期化状態を取得
     *
     * @return 初期化済みの場合はtrue
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * ログディレクトリのパスを取得
     *
     * @return ログディレクトリの絶対パス
     */
    public String getLogDirectory() {
        return logDirectory;
    }
}