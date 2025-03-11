package util;

/**
 * システム全体で使用されるメッセージを定義する列挙型クラス。
 * ログメッセージ、エラーメッセージ、ダイアログメッセージを一元管理する。
 *
 * <p>
 * メッセージは以下のカテゴリに分類：
 * </p>
 * <ul>
 * <li>ログメッセージ（情報）: LOG_INFO_で始まるもの</li>
 * <li>ログメッセージ（警告）: LOG_WARN_で始まるもの</li>
 * <li>ログメッセージ（エラー）: LOG_ERROR_で始まるもの</li>
 * <li>バリデーションエラーメッセージ: VALIDATION_ERROR_で始まるもの</li>
 * <li>ダイアログメッセージ（エラー）: DIALOG_ERROR_で始まるもの</li>
 * <li>ダイアログメッセージ（確認）: DIALOG_CONFIRM_で始まるもの</li>
 * <li>ダイアログメッセージ（完了通知）: DIALOG_COMPLETION_で始まるもの</li>
 * </ul>
 *
 * @author Nakano
 * @version 2.0.0
 * @since 2025-03-12
 */
public enum MessageEnum {

    // ----- ログメッセージ（情報）-----
    /**
     * システム起動時のログメッセージ
     */
    LOG_INFO_SYSTEM_START("[PROCESS]システムを起動しました"),

    /**
     * システム終了時のログメッセージ
     */
    LOG_INFO_SYSTEM_SHUTDOWN("[PROCESS]システムを終了しました"),

    /**
     * ファイル作成時のログメッセージ
     * パラメータ: %s - ファイルパス
     */
    LOG_INFO_FILE_CREATED("[PROCESS]ファイルを新規作成しました: %s"),

    /**
     * ディレクトリ作成時のログメッセージ
     * パラメータ: %s - ディレクトリパス
     */
    LOG_INFO_DIRECTORY_CREATED("[PROCESS]ディレクトリを新規作成しました: %s"),

    /**
     * データ読み込み時のログメッセージ
     * パラメータ: %d - 読み込まれたデータ件数
     */
    LOG_INFO_DATA_LOADED("[PROCESS]データを読み込みました: %d件"),

    /**
     * データ保存時のログメッセージ
     * パラメータ: %d - 保存されたデータ件数
     */
    LOG_INFO_DATA_SAVED("[PROCESS]データを保存しました: %d件"),

    /**
     * エンジニア情報追加時のログメッセージ
     * パラメータ:
     * %s - エンジニアID
     * %s - エンジニア名
     */
    LOG_INFO_ENGINEER_ADDED("[PROCESS]エンジニア情報を追加しました: ID=%s, 名前=%s"),

    /**
     * エンジニア情報更新時のログメッセージ
     * パラメータ:
     * %s - エンジニアID
     * %s - エンジニア名
     */
    LOG_INFO_ENGINEER_UPDATED("[PROCESS]エンジニア情報を更新しました: ID=%s, 名前=%s"),

    /**
     * エンジニア情報削除時のログメッセージ
     * パラメータ: %s - エンジニアID
     */
    LOG_INFO_ENGINEER_DELETED("[PROCESS]エンジニア情報を削除しました: ID=%s"),

    /**
     * CSVエクスポート時のログメッセージ
     * パラメータ: %s - ファイルパス
     */
    LOG_INFO_CSV_EXPORTED("[PROCESS]CSVファイルをエクスポートしました: %s"),

    /**
     * 画面遷移時のログメッセージ
     * パラメータ:
     * %s - 遷移元画面名
     * %s - 遷移先画面名
     */
    LOG_INFO_SCREEN_TRANSITION("[PROCESS]画面遷移: %s -> %s"),

    // ----- ログメッセージ（警告）-----
    /**
     * ファイルが見つからない場合の警告ログメッセージ
     * パラメータ: %s - ファイルパス
     */
    LOG_WARN_FILE_NOT_FOUND("[PROCESS]ファイルが見つかりません: %s"),

    /**
     * ディレクトリが見つからない場合の警告ログメッセージ
     * パラメータ: %s - ディレクトリパス
     */
    LOG_WARN_DIRECTORY_NOT_FOUND("[PROCESS]ディレクトリが見つかりません: %s"),

    /**
     * データ形式が不正な場合の警告ログメッセージ
     * パラメータ: %s - 不正データの詳細
     */
    LOG_WARN_INVALID_DATA_FORMAT("[PROCESS]データ形式が不正です: %s"),

    /**
     * 重複IDが検出された場合の警告ログメッセージ
     * パラメータ: %s - 重複しているID
     */
    LOG_WARN_DUPLICATE_ID("[PROCESS]重複するIDが検出されました: %s"),

    /**
     * 操作タイムアウト時の警告ログメッセージ
     * パラメータ: %s - タイムアウトした操作の詳細
     */
    LOG_WARN_OPERATION_TIMEOUT("[PROCESS]操作がタイムアウトしました: %s"),

    /**
     * データ変換エラー時の警告ログメッセージ
     * パラメータ: %s - エラーの詳細
     */
    LOG_WARN_DATA_CONVERSION_ERROR("[PROCESS]データ変換エラーが発生しました: %s"),

    // ----- ログメッセージ（エラー）-----
    /**
     * システムエラー発生時のエラーログメッセージ
     * パラメータ: %s - エラーの詳細
     */
    LOG_ERROR_SYSTEM_ERROR("[PROCESS]システムエラーが発生しました: %s"),

    /**
     * ファイルアクセスエラー時のエラーログメッセージ
     * パラメータ: %s - エラーの詳細
     */
    LOG_ERROR_FILE_ACCESS("[PROCESS]ファイルアクセスエラーが発生しました: %s"),

    /**
     * データ読み込みエラー時のエラーログメッセージ
     * パラメータ: %s - エラーの詳細
     */
    LOG_ERROR_DATA_LOAD("[PROCESS]データ読み込みエラーが発生しました: %s"),

    /**
     * データ保存エラー時のエラーログメッセージ
     * パラメータ: %s - エラーの詳細
     */
    LOG_ERROR_DATA_SAVE("[PROCESS]データ保存エラーが発生しました: %s"),

    /**
     * I/O例外発生時のエラーログメッセージ
     * パラメータ: %s - 例外の詳細
     */
    LOG_ERROR_IO_EXCEPTION("[PROCESS]I/O例外が発生しました: %s"),

    /**
     * スレッド例外発生時のエラーログメッセージ
     * パラメータ: %s - 例外の詳細
     */
    LOG_ERROR_THREAD_EXCEPTION("[PROCESS]スレッド例外が発生しました: %s"),

    /**
     * 入力検証失敗時のエラーログメッセージ
     * パラメータ: %s - 検証失敗の詳細
     */
    LOG_ERROR_VALIDATION_FAILED("[PROCESS]入力検証に失敗しました: %s"),

    // ----- バリデーションエラーメッセージ -----
    /**
     * 必須項目未入力時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_REQUIRED("*は必須項目になります"),

    /**
     * 氏名入力エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_NAME("*は必須項目になります。または20文字以内で入力してください"),

    /**
     * フリガナ入力エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_NAME_KANA("*は必須項目になります。またはフリガナを20文字以内で入力してください"),

    /**
     * 社員ID入力エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_EMPLOYEE_ID("*は必須項目になります。すでに登録されているIDまたはID番号5桁の数字を入力してください"),

    /**
     * 生年月日選択エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_BIRTH_DATE("*は必須項目になります。項目を選択してください"),

    /**
     * 入社年月選択エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_JOIN_DATE("*は必須項目になります。項目を選択してください"),

    /**
     * エンジニア歴選択エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_CAREER("*は必須項目になります。項目を選択してください"),

    /**
     * プログラミング言語選択エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_PROGRAMMING_LANGUAGES("*は必須項目になります。1つ以上項目を選択してください"),

    /**
     * 経歴入力エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_CAREER_HISTORY("経歴は200文字以内で入力してください"),

    /**
     * 研修受講歴入力エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_TRAINING_HISTORY("研修の受講歴は200文字以内で入力してください"),

    /**
     * 備考入力エラー時のバリデーションエラーメッセージ
     */
    VALIDATION_ERROR_NOTE("備考は500文字以内で入力してください"),

    // ----- ダイアログメッセージ - エラー -----
    /**
     * エラーダイアログのタイトル
     */
    DIALOG_ERROR_TITLE("エラー"),

    /**
     * 入力検証エラー時のダイアログメッセージ
     */
    DIALOG_ERROR_VALIDATION("入力内容に誤りがあります"),

    /**
     * ファイルアクセスエラー時のダイアログメッセージ
     */
    DIALOG_ERROR_FILE_ACCESS("ファイルアクセスエラーが発生しました"),

    /**
     * データ読み込みエラー時のダイアログメッセージ
     */
    DIALOG_ERROR_DATA_LOAD("データの読み込みに失敗しました"),

    /**
     * データ保存エラー時のダイアログメッセージ
     */
    DIALOG_ERROR_DATA_SAVE("データの保存に失敗しました"),

    /**
     * システムエラー時のダイアログメッセージ
     */
    DIALOG_ERROR_SYSTEM("システムエラーが発生しました"),

    /**
     * 重複ID検出時のダイアログメッセージ
     */
    DIALOG_ERROR_DUPLICATE_ID("すでに使用されているIDです"),

    /**
     * 入力形式不正時のダイアログメッセージ
     */
    DIALOG_ERROR_INVALID_FORMAT("入力形式が正しくありません"),

    // ----- ダイアログメッセージ - 確認 -----
    /**
     * 確認ダイアログのタイトル
     */
    DIALOG_CONFIRM_TITLE("確認"),

    /**
     * 保存確認時のダイアログメッセージ
     */
    DIALOG_CONFIRM_SAVE("変更を保存しますか？"),

    /**
     * 更新確認時のダイアログメッセージ
     */
    DIALOG_CONFIRM_UPDATE("情報を更新しますか？"),

    /**
     * 削除確認時のダイアログメッセージ
     */
    DIALOG_CONFIRM_DELETE("この情報を削除しますか？"),

    /**
     * キャンセル確認時のダイアログメッセージ
     */
    DIALOG_CONFIRM_CANCEL("操作をキャンセルしますか？未保存の変更は失われます"),

    /**
     * 終了確認時のダイアログメッセージ
     */
    DIALOG_CONFIRM_EXIT("アプリケーションを終了しますか？"),

    /**
     * CSVエクスポート確認時のダイアログメッセージ
     */
    DIALOG_CONFIRM_EXPORT_CSV("CSVをエクスポートしますか？"),

    // ----- ダイアログメッセージ - 完了通知 -----
    /**
     * 完了ダイアログのタイトル
     */
    DIALOG_COMPLETION_TITLE("完了"),

    /**
     * 保存完了時のダイアログメッセージ
     */
    DIALOG_COMPLETION_SAVE("保存が完了しました"),

    /**
     * 更新完了時のダイアログメッセージ
     */
    DIALOG_COMPLETION_UPDATE("更新が完了しました"),

    /**
     * 削除完了時のダイアログメッセージ
     */
    DIALOG_COMPLETION_DELETE("削除が完了しました"),

    /**
     * CSVエクスポート完了時のダイアログメッセージ
     */
    DIALOG_COMPLETION_EXPORT_CSV("CSVのエクスポートが完了しました"),

    /**
     * CSVインポート完了時のダイアログメッセージ
     */
    DIALOG_COMPLETION_IMPORT_CSV("CSVのインポートが完了しました");

    /**
     * メッセージテキスト
     */
    private final String message;

    /**
     * コンストラクタ
     *
     * @param message メッセージテキスト
     */
    MessageEnum(String message) {
        this.message = message;
    }

    /**
     * メッセージテキストを取得
     *
     * @return メッセージテキスト
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * フォーマット済みのメッセージテキストを取得。
     * メッセージ内の「%s」や「%d」などのプレースホルダーが引数の値で置換。
     *
     * <p>
     * 使用例：
     * </p>
     * 
     * <pre>
     * String msg = MessageEnum.LOG_INFO_ENGINEER_ADDED.format("ID00001", "山田太郎");
     * // 結果: "[2025-01-23 15:30:45][INFO][PROCESS]エンジニア情報を追加しました: ID=ID00001,
     * // 名前=山田太郎"
     * </pre>
     *
     * @param args フォーマット引数
     * @return フォーマット済みメッセージテキスト
     * @throws java.util.IllegalFormatException フォーマット文字列が不正か、引数の数や型が合わない場合
     */
    public String format(Object... args) {
        if (args == null || args.length == 0) {
            // 引数がなければそのままメッセージを返す
            return this.message;
        }

        try {
            return String.format(this.message, args);
        } catch (java.util.IllegalFormatException e) {
            // フォーマットエラーの場合は基本メッセージと警告を返す
            return this.message + " [フォーマットエラー]";
        }
    }
}