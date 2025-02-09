package view;

import model.EngineerDTO;
/** 現在は使用していません */
//import view.DialogManager;
import util.LogHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * エンジニア一覧を表示するパネルクラス
 * ページング、ソート、検索機能
 *
 * @author Nakano
 * @version 1.0.0
 * @since 2025-02-10
 */
public class ListPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final int pageSize = 100;
    private int currentPage = 1;
    private List<EngineerDTO> allData;
    private final JLabel pageLabel;

    /** テーブルのカラム名 */
    private static final String[] COLUMN_NAMES = {
            "社員ID", "氏名", "生年月日", "エンジニア歴", "扱える言語"
    };

    public ListPanel() {
        super(new BorderLayout());
        this.allData = new ArrayList<>();
        this.tableModel = createTableModel();
        this.table = createTable();
        this.searchField = new JTextField(20);
        this.pageLabel = new JLabel();
        initialize();
    }

    private void initialize() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        loadTestData(); // テスト用データ読み込み
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable() {
        JTable table = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // 列幅設定
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(500);

        // 選択モード設定
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        return table;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // 検索パネル
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("検索:"));
        searchPanel.add(searchField);

        JButton searchButton = new JButton("検索");
        searchButton.addActionListener(e -> search());
        searchPanel.add(searchButton);

        topPanel.add(searchPanel);
        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton prevButton = new JButton("前へ");
        prevButton.addActionListener(e -> changePage(-1));

        JButton nextButton = new JButton("次へ");
        nextButton.addActionListener(e -> changePage(1));

        bottomPanel.add(prevButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextButton);

        return bottomPanel;
    }

    private void search() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            updateTable(allData);
            return;
        }

        List<EngineerDTO> filteredData = new ArrayList<>();
        for (EngineerDTO engineer : allData) {
            if (matchesSearch(engineer, searchText)) {
                filteredData.add(engineer);
            }
        }
        updateTable(filteredData);
    }

    private boolean matchesSearch(EngineerDTO engineer, String searchText) {
        return engineer.getId().toLowerCase().contains(searchText) ||
                engineer.getName().toLowerCase().contains(searchText);
    }

    private void changePage(int delta) {
        int totalPages = (int) Math.ceil((double) allData.size() / pageSize);
        currentPage = Math.max(1, Math.min(totalPages, currentPage + delta));
        updateTable(allData);
        updatePageLabel();
    }

    private void updatePageLabel() {
        int totalPages = (int) Math.ceil((double) allData.size() / pageSize);
        pageLabel.setText(String.format("ページ: %d / %d", currentPage, totalPages));
    }

    private void updateTable(List<EngineerDTO> data) {
        tableModel.setRowCount(0);
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, data.size());

        for (int i = start; i < end; i++) {
            EngineerDTO engineer = data.get(i);
            tableModel.addRow(new Object[] {
                    engineer.getId(),
                    engineer.getName(),
                    engineer.getBirthDate(),
                    engineer.getCareer(),
                    String.join(", ", engineer.getProgrammingLanguages())
            });
        }
    }

    // テスト用データ生成
    private void loadTestData() {
        for (int i = 1; i <= 1000; i++) {
            EngineerDTO engineer = new EngineerDTO();
            engineer.setId(String.format("ID%05d", i));
            engineer.setName("山田太郎 " + i);
            engineer.setBirthDate(new Date());
            engineer.setCareer(i % 20);
            engineer.setProgrammingLanguages(Arrays.asList("Java", "Python", "JavaScript"));
            allData.add(engineer);
        }
        updateTable(allData);
        updatePageLabel();
        LogHandler.getInstance().log(java.util.logging.Level.INFO, "テストデータを読み込みました");
    }
}