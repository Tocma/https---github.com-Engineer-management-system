package model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * エンジニア情報を保持するデータ転送オブジェクト（DTO）
 * エンジニアの基本情報、スキル情報、経歴情報などを管理
 *
 * <p>
 * 主要フィールド：
 * <ul>
 * <li>id - 社員ID（必須）</li>
 * <li>name - 氏名（必須）</li>
 * <li>birthDate - 生年月日（必須）</li>
 * <li>career - エンジニア歴（必須）</li>
 * <li>programmingLanguages - 扱える言語（必須）</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用例：
 * 
 * <pre>
 * EngineerDTO engineer = new EngineerDTO();
 * engineer.setId("ID00001");
 * engineer.setName("山田太郎");
 * engineer.setBirthDate(new Date());
 * engineer.setCareer(5.0);
 * engineer.setProgrammingLanguages(Arrays.asList("Java", "Python"));
 * </pre>
 * </p>
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-01-24
 */
public class EngineerDTO {
    private String id;
    private String name;
    private Date birthDate;
    private int career;
    private List<String> programmingLanguages;
    private Date registeredDate;

    /**
     * デフォルトコンストラクタ
     */
    public EngineerDTO() {
        this.registeredDate = new Date();
    }

    /**
     * ID（社員番号）を取得
     * 
     * @return 社員ID
     */
    public String getId() {
        return id;
    }

    /**
     * ID（社員番号）を設定
     * 
     * @param id 社員ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 氏名を取得
     * 
     * @return 氏名
     */
    public String getName() {
        return name;
    }

    /**
     * 氏名を設定
     * 
     * @param name 氏名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 生年月日を取得
     * 
     * @return 生年月日
     */
    public Date getBirthDate() {
        return birthDate != null ? new Date(birthDate.getTime()) : null;
    }

    /**
     * 生年月日を設定
     * 
     * @param birthDate 生年月日
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate != null ? new Date(birthDate.getTime()) : null;
    }

    /**
     * エンジニア歴を取得
     * 
     * @return エンジニア歴（年数）
     */
    public int getCareer() {
        return career;
    }

    /**
     * エンジニア歴を設定
     * 
     * @param career エンジニア歴（年数）
     */
    public void setCareer(int career) {
        this.career = career;
    }

    /**
     * プログラミング言語リストを取得
     * 
     * @return プログラミング言語のリスト
     */
    public List<String> getProgrammingLanguages() {
        return programmingLanguages;
    }

    /**
     * プログラミング言語リストを設定
     * 
     * @param programmingLanguages プログラミング言語のリスト
     */
    public void setProgrammingLanguages(List<String> programmingLanguages) {
        this.programmingLanguages = programmingLanguages;
    }

    /**
     * 登録日時を取得
     * 
     * @return 登録日時
     */
    public Date getRegisteredDate() {
        return registeredDate != null ? new Date(registeredDate.getTime()) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EngineerDTO that = (EngineerDTO) o;
        return career == that.career &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(programmingLanguages, that.programmingLanguages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthDate, career, programmingLanguages);
    }

    @Override
    public String toString() {
        return "EngineerDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", career=" + career +
                ", programmingLanguages=" + programmingLanguages +
                ", registeredDate=" + registeredDate +
                '}';
    }
}