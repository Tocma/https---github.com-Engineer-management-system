package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * エンジニア情報管理システムのログ管理を行うシングルトンクラス
 *
 * <p>
 * このクラスは、システム全体のログ出力を一元管理し、日付ベースのログファイル名や
 * ログローテーション、適切なフォーマットなどの機能。MessageEnum を活用して
 * メッセージの一貫性を保ちます。
 * </p>
 *
 * <p>
 * 主な特徴：
 * <ul>
 * <li>シングルトンパターンによる一元管理</li>
 * <li>MessageEnum による標準化されたメッセージ</li>
 * <li>日単位のログファイル自動生成</li>
 * <li>ログローテーションによる容量管理</li>
 * <li>詳細なエラー情報の記録</li>
 * </ul>
 * </p>
 *
 * <p>
 * ログファイルの特性：
 * <ul>
 * <li>命名規則: System-YYYY-MM-DD.log</li>
 * <li>最大サイズ: 10MB（超過時に自動ローテーション）</li>
 * <li>フォーマット: [日時] [ログレベル] [種類]メッセージ</li>
 * <li>エンコーディング: UTF-8</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用例：
 * 
 * <pre>
 * // ログシステムの初期化
 * LogHandler.getInstance().initialize();
 *
 * // MessageEnum を使用したログ出力
 * LogHandler.getInstance().log(MessageEnum.LOG_INFO_SYSTEM_START);
 *
 * // パラメータ付きメッセージ
 * LogHandler.getInstance().log(MessageEnum.LOG_INFO_ENGINEER_ADDED, "E00001", "山田太郎");
 *
 * // レベルとメッセージを指定
 * LogHandler.getInstance().log(Level.INFO, "カスタムメッセージ");
 *
 * // エラーログの出力（例外付き）
 * try {
 *     // 処理
 * } catch (Exception e) {
 *     LogHandler.getInstance().logError(MessageEnum.LOG_ERROR_SYSTEM_ERROR, e, "処理中エラー");
 * }
 * </pre>
 * </p>
 *
 * @author Nakano
 * @version 2.0.0
 * @since 2025-03-12
 */
public class LogHandler {

    /** シングルトンインスタンス */
    // private static final LogHandler INSTANCE = new LogHandler();
    private static LogHandler INSTANCE = null;

    /** ログ関連の定数定義 */
    private static final String DEFAULT_LOG_DIR = "logs";
    private static final String LOG_FILE_FORMAT = "System-%s.log";
    private static final int MAX_LOG_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final String LOG_FORMAT = "[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS] [%4$s] %5$s%6$s%n";

    /** ロガー設定 */
    private Logger logger;
    private boolean initialized;
    private String logDirectory;
    private FileHandler fileHandler;

    /**
     * プライベートコンストラクタ
     * シングルトンパターンを実現するため、外部からのインスタンス化を防ぐ
     */
    private LogHandler() {
        // 初期化はinitializeメソッドで行われるため、コンストラクタでは行わない
    }

    /**
     * シングルトンインスタンスを取得
     *
     * @return LogHandlerの唯一のインスタンス
     */
    public static LogHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogHandler();
        }

        return INSTANCE;
    }

    /**
     * デフォルトのログディレクトリでロガーを初期化
     *
     * @throws IOException ログディレクトリの作成や設定に失敗した場合
     */
    public synchronized void initialize() throws IOException {
        initialize(DEFAULT_LOG_DIR);
    }

    /**
     * 指定されたログディレクトリでロガーを初期化
     * ディレクトリの作成、ロガーの設定、フォーマッタの設定
     *
     * @param logDir ログファイルを格納するディレクトリパス
     * @throws IOException              ログディレクトリの作成や設定に失敗した場合
     * @throws IllegalArgumentException ログディレクトリのパスがnullまたは空の場合
     */
    public synchronized void initialize(String logDir) throws IOException {
        if (logDir == null || logDir.trim().isEmpty()) {
            throw new IllegalArgumentException("ログディレクトリパスが指定されていません");
        }

        if (initialized) {
            return;
        }

        try {
            // ログディレクトリのセットアップ
            this.logDirectory = setupLogDirectory(logDir);

            // ロガーの設定
            configureLogger();

            // 初期化完了
            initialized = true;

            // 初期化完了のログを出力
            log(MessageEnum.LOG_INFO_SYSTEM_START);

        } catch (IOException e) {
            System.err.println("ログシステムの初期化に失敗しました: " + e.getMessage());
            throw new IOException("ログシステムの初期化に失敗しました", e);
        }
    }

    /**
     * ログディレクトリを設定
     * 指定されたディレクトリが存在しない場合は作成
     *
     * @param logDir ログディレクトリのパス
     * @return 作成されたログディレクトリの絶対パス
     * @throws IOException ディレクトリの作成に失敗した場合
     */
    private String setupLogDirectory(String logDir) throws IOException {
        Path logPath = Paths.get(logDir).toAbsolutePath();
        if (!Files.exists(logPath)) {
            Files.createDirectories(logPath);
            System.out.println("ログディレクトリを作成しました: " + logPath);
        }
        return logPath.toString();
    }

    /**
     * ロガーの設定
     * 日付ベースのログファイル名とフォーマットを設定
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
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
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
        System.out.println("ログファイルを設定しました: " + logFilePath);
    }

    /**
     * MessageEnumを使用してログを記録
     * MessageEnumのプレフィックスに基づいて適切なログレベルを自動選択
     *
     * @param messageEnum ログメッセージの列挙定数
     * @param args        メッセージのフォーマットパラメータ
     * @throws IllegalStateException LogHandlerが初期化されていない場合
     */
    public synchronized void log(MessageEnum messageEnum, Object... args) {
        try {
            checkInitialized();
            if (messageEnum == null) {
                throw new IllegalArgumentException("メッセージ列挙定数がnullです");
            }

            String formattedMessage;
            try {
                formattedMessage = messageEnum.format(args);
            } catch (java.util.IllegalFormatException e) {
                // フォーマットエラーを処理し、基本メッセージを使用
                formattedMessage = messageEnum.getMessage() + " [フォーマットエラー]";
                // または代替として警告をログに記録
                System.err.println("メッセージフォーマットエラー: " + e.getMessage() +
                        " for " + messageEnum.name());
            }

            Level level = determineLogLevel(messageEnum);

            // NullPointerException対策
            if (logger != null && level != null) {
                logger.log(level, formattedMessage);
            } else {
                handleLoggerError("ロガーまたはログレベルがnullです", formattedMessage, level);
            }
        } catch (Exception e) {
            // ログ出力の失敗を適切に処理
            handleLogError("ログ出力中に例外が発生しました", e);
        }
    }

    private void handleLoggerError(String errorMessage, String messageToLog, Level level) {
        System.err.println(errorMessage + ": " +
                (logger == null ? "logger=null" : "") +
                (level == null ? "level=null" : ""));
        // フォールバックとしてコンソール出力
        System.out.println("[" + (level != null ? level.getName() : "INFO") +
                "] " + messageToLog);
    }

    private void handleLogError(String errorMessage, Exception e) {
        System.err.println(errorMessage + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * MessageEnumの名前からログレベルを判断
     *
     * @param messageEnum メッセージ列挙定数
     * @return 対応するログレベル
     */
    private Level determineLogLevel(MessageEnum messageEnum) {
        String name = messageEnum.name();
        if (name.startsWith("LOG_ERROR_")) {
            return Level.SEVERE;
        } else if (name.startsWith("LOG_WARN_")) {
            return Level.WARNING;
        } else if (name.startsWith("LOG_INFO_")) {
            return Level.INFO;
        } else {
            // その他のメッセージはINFOレベルとする
            return Level.INFO;
        }
    }

    /**
     * 指定されたレベルでログを記録
     * 従来のログ出力方法を維持するためのメソッド
     *
     * @param level   ログレベル
     * @param message ログメッセージ
     * @throws IllegalStateException    LogHandlerが初期化されていない場合
     * @throws IllegalArgumentException メッセージがnullの場合
     */
    public synchronized void log(Level level, String message) {
        checkInitialized();
        if (message == null) {
            throw new IllegalArgumentException("ログメッセージがnullです");
        }
        logger.log(level, message);
    }

    /**
     * MessageEnumを使用してエラーログを記録
     * エラーメッセージと例外情報を記録
     *
     * @param messageEnum エラーメッセージの列挙定数
     * @param throwable   発生した例外
     * @param args        メッセージのフォーマットパラメータ
     * @throws IllegalStateException LogHandlerが初期化されていない場合
     */
    public synchronized void logError(MessageEnum messageEnum, Throwable throwable, Object... args) {
        checkInitialized();
        if (messageEnum == null || throwable == null) {
            throw new IllegalArgumentException("メッセージ列挙定数と例外は必須です");
        }

        String formattedMessage = messageEnum.format(args);
        logger.log(Level.SEVERE, formattedMessage, throwable);
    }

    /**
     * エラーログを記録（従来の方法）
     * エラーメッセージと例外情報を記録
     *
     * @param message   エラーメッセージ
     * @param throwable 発生した例外
     * @throws IllegalStateException    LogHandlerが初期化されていない場合
     * @throws IllegalArgumentException メッセージまたは例外がnullの場合
     */
    public synchronized void logError(String message, Throwable throwable) {
        checkInitialized();
        if (message == null || throwable == null) {
            throw new IllegalArgumentException("メッセージと例外情報は必須です");
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
            throw new IllegalStateException("LogHandlerが初期化されていません");
        }
    }

    /**
     * 現在のログファイル名を取得
     *
     * @return 現在の日付に対応するログファイル名
     */
    public String getCurrentLogFileName() {
        return String.format(LOG_FILE_FORMAT,
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    /**
     * ロガーのクリーンアップ
     * アプリケーション終了時に呼び出して、リソースを適切に解放
     */
    public synchronized void cleanup() {
        if (fileHandler != null) {
            if (initialized) {
                log(MessageEnum.LOG_INFO_SYSTEM_SHUTDOWN);
            }
            fileHandler.close();
        }
    }

    /**
     * 初期化状態を取得
     *
     * @return 初期化済みの場合true
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