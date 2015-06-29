package com.welly.noveltool.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.welly.noveltool.dao.SqliteHelper;
import com.welly.noveltool.dao.po.Book;
import com.welly.noveltool.util.BookBean;
import com.welly.noveltool.util.Conf;
import com.welly.noveltool.util.FileUtil;
import com.welly.noveltool.util.LookAndFeelUtil;
import com.welly.noveltool.util.SearchType;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7334753190505653056L;
	// 目录输入框
	private JTextField dirTextField = new JTextField(60);
	// 打开按钮
	private JButton openButton = new JButton("打    开");
	// 单选按钮组
	private ButtonGroup buttonGroup = new ButtonGroup();
	// 类型单选按钮
	private JRadioButton typeRadioButton = new JRadioButton("类型", true);
	// 作者单选按钮
	private JRadioButton authorRadioButton = new JRadioButton("作者", false);
	// 作者类型,收藏与否
	private JComboBox<String> favoriteAuthorComboBox = new JComboBox<>(new String[]{"所有", "已收藏", "未收藏"});
	// 书名单选按钮
	private JRadioButton booknameRadioButton = new JRadioButton("书名", false);
	// 评分下拉框
//	private JRadioButton scoreRadioButton = new JRadioButton("评分", false);
	private JComboBox<String> scoreComboBox = new JComboBox<String>(new String[]{"all", "0", "1", "2", "3", "4", "5"});
	// 检索类型
	private SearchType searchType = SearchType.TYPE;
	// 检索条件输入框
	private JTextField searchTextField = new JTextField(37);
	// 清空检索条件按钮
	private JButton clearButton = new JButton("清    空");
	// 上一页那妞
	private JButton lastButton = new JButton("上一页");
	// 下一页按钮
	private JButton nextButton = new JButton("下一页");
	// 页数输入框
	private JTextField pageNoTextField = new JTextField(3);
	// 确定按钮
	private JButton pageButton = new JButton("确    定");
	// 总共页数标签
	private JLabel totalLabel = new JLabel();
	// 树的根节点
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("全部");
	// 左侧树
	private JTree recordTree = new JTree(root);
	// 每行显示条数
	private int rowCount = Conf.getPagecount();
	// 右侧表格
	private JTable detailTable = new JTable(new ImageTableModel(rowCount, 6));
	// 当前列表展示的book列表
	private List<Book> bookList = new LinkedList<Book>();
	// 当前左侧树展示的关键词
	private String[] keys;
	// 当前页数
	private int pageNo = 1;
	// 当前选择的树节点的key
	private String currentKey;
	// 导入按钮
	private JButton importButton = new JButton("导    入");
	// 是否使用完全匹配进行检索
	private boolean isEqual = false;
	// 最近一次导入的路径,
	private JTextField latestPathTextField = new JTextField(52);
	// 年月的下拉选择框
	private JComboBox<String> yearComboBox = new JComboBox<String>(getYears());
	private JComboBox<String> monthComboBox = new JComboBox<String>(new String[]{"1", "2", "3"
			, "4", "5", "6", "7", "8", "9", "10", "11", "12"});
	
	// 检索管理
//	private SearchManager sm = SearchManager.getInstanse();
	
	public MainFrame(String title) {
		super(title);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(1000, 600));
		this.setMinimumSize(new Dimension(1000, 600));
		// 设置外观
		LookAndFeelUtil.setLookAndFeel(LookAndFeelUtil.METAL, this);

		this.add(createTopPanel(), BorderLayout.PAGE_START);
		this.add(createCenterPane(), BorderLayout.CENTER);
		this.add(createBottomPanel(), BorderLayout.PAGE_END);

		this.pack();
		searchAndUpdateTree();
	}

	/**
	 * 创建上方面板,包括目录,类型选择,检索
	 * @return
	 */
	private JPanel createTopPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// panel.setSize(200, 100);

		// 目录显示面板
		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		dirTextField.setEditable(false);
		// 点击打开按钮,弹出文件夹选择对话框
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				// 获取上次打开路径,检索是否存在,如果存在直接打开上次路径,如果不存在,检索父路径,如果还是不存在,则打开home目录
				String latestPath = dirTextField.getText();
				File openFile = null;
				if (latestPath == null || latestPath.isEmpty()){
					latestPath = latestPathTextField.getText();
				}

				File file = new File(latestPath);
				if (file.exists()){
					openFile = file;
				} else if (file.getParentFile().exists()){
					openFile = file.getParentFile();
				}
				
				if (openFile == null){
					openFile = FileSystemView
							.getFileSystemView().getHomeDirectory();
				}
				
				JFileChooser fileChooser = new JFileChooser(openFile);
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				int ret = fileChooser.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					String dirPath = fileChooser.getSelectedFile()
							.getAbsolutePath();
					// 和之前选择相同,直接返回
					if (dirPath.equals(dirTextField.getText())) {
						return;
					}
					dirTextField.setText(dirPath);
				}
			}
		});
		// 点击导入的事件
		importButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String dir = dirTextField.getText();
				if (dir.equals("")){
					return;
				} else {
					int year = Integer.parseInt(yearComboBox.getSelectedItem().toString());
					int month = Integer.parseInt(monthComboBox.getSelectedItem().toString());
					importToDb(dir, year, month);
					searchAndUpdateTree();
					SqliteHelper.insertPath(dir);
					latestPathTextField.setText(SqliteHelper.getLatestPath());
				}
			}
		});
		filePanel.add(dirTextField);
		filePanel.add(openButton);
		filePanel.add(importButton);
		filePanel.add(yearComboBox);
		filePanel.add(new JLabel("年"));
		filePanel.add(monthComboBox);
		filePanel.add(new JLabel("月"));
		int yearOfNow = Calendar.getInstance().get(Calendar.YEAR);
		int monthOfNow = Calendar.getInstance().get(Calendar.MONTH);
		yearComboBox.setSelectedItem(yearOfNow + "");
		monthComboBox.setSelectedItem(monthOfNow + 1 + "");
		
		JPanel latestPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		latestPathPanel.add(new JLabel("上次导入路径:"));
		latestPathTextField.setEditable(false);
		latestPathTextField.setText(SqliteHelper.getLatestPath());
		latestPathPanel.add(latestPathTextField);

		// 类型选择显示面板
		JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonGroup.add(typeRadioButton);
		buttonGroup.add(authorRadioButton);
		buttonGroup.add(booknameRadioButton);
		// 类型单选按钮点击事件
		typeRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				searchType = SearchType.TYPE;
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						isEqual = false;
						searchAndUpdateTree();
					}
				});
			}
		});
		// 作者单选按钮点击事件
		authorRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				searchType = SearchType.AUTHOR;
				searchAndUpdateTree();
			}
		});
		// 书名单选按钮点击事件
		booknameRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				searchType = SearchType.NAME;
				isEqual = false;
				searchAndUpdateTree();
			}
		});
		typePanel.add(typeRadioButton);
		typePanel.add(authorRadioButton);
		typePanel.add(booknameRadioButton);
		typePanel.add(searchTextField);
		typePanel.add(clearButton);

		// 评分单选按钮点击事件
		scoreComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent actionevent) {
//				searchAndUpdateTree();
				updateTable(currentKey, 1);
			}
		});
		// 作者类型按钮点击事件
		favoriteAuthorComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (searchType == SearchType.AUTHOR){
					searchAndUpdateTree();
				} else {
					updateTable(currentKey, 1);
				}
			}
		});
		JPanel otherSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		otherSearchPanel.add(new JLabel("评分:"));
		otherSearchPanel.add(scoreComboBox);
		otherSearchPanel.add(new JLabel("作者:"));
		otherSearchPanel.add(favoriteAuthorComboBox);

		// 清除按钮点击事件
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				searchTextField.setText("");
			}
		});

		// 检索输入框文本改变事件
		searchTextField.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent e) {
						isEqual = false;
						searchAndUpdateTree();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						isEqual = false;
						searchAndUpdateTree();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
					}
				});

		panel.add(filePanel);
		panel.add(latestPathPanel);
		panel.add(typePanel);
		panel.add(otherSearchPanel);

		return panel;
	}

	/**
	 * 创建中部面板,包括树和表格
	 * @return
	 */
	private JSplitPane createCenterPane() {
		JScrollPane treePane = new JScrollPane(recordTree);
		treePane.setMinimumSize(new Dimension(200, 400));
		treePane.setSize(new Dimension(200, 400));
		JScrollPane tablePane = new JScrollPane(detailTable);
		tablePane.setMinimumSize(new Dimension(750, 400));
		tablePane.setSize(new Dimension(750, 400));
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				treePane, tablePane);

		// 不显示树的连线
		recordTree.putClientProperty("JTree.lineStyle", "None");
		// 树不咳编辑
		recordTree.setEditable(false);
		// 树可多选
		recordTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		recordTree.setExpandsSelectedPaths(true);
		recordTree.expandRow(0);

		// 右键菜单,当前包括保存
		final JPopupMenu popMenu = new JPopupMenu();
		JMenuItem saveItem = new JMenuItem("保存");
		// 保存菜单点击事件
		saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				save();
			}
		});
		final JMenuItem deleteItem = new JMenuItem("删除");
		// 保存菜单点击事件
		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				deleteByDate();
			}
		});
		popMenu.add(saveItem);
		popMenu.add(deleteItem);
		
		// 树的右键点击事件,弹出菜单
		recordTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (bookList == null || bookList.size() == 0) {
					return;
				}
				if (e.getButton() == MouseEvent.BUTTON3) { // BUTTON3是鼠标右键
					int count = recordTree.getSelectionCount();
					if (count == 0) {
						return;
					}
					
					if (searchType == SearchType.NAME){
						if (count == 1){
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) recordTree
									.getLastSelectedPathComponent();
							if (node.getLevel() != 0) {
								popMenu.add(deleteItem);
							} else {
								popMenu.remove(deleteItem);
							}
						} else {
							popMenu.add(deleteItem);
						}
					} else {
						popMenu.remove(deleteItem);
					}
					
					popMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		// 树的点击选择事件,更新表格
		recordTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent treeselectionevent) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) recordTree
						.getLastSelectedPathComponent();
				currentKey = null;
				if (node.getLevel() != 0) {
					currentKey = (String) node.getUserObject();
				}
				if (!booknameRadioButton.isSelected()){
					isEqual = true;
				}
				updateTable(currentKey, 1);
			}
		});

//		detailTable.setMinimumSize(new Dimension(640, 0));
		// 表格可多选
		detailTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// 表格resize方式
		detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// 表格
//		detailTable.setCellEditor(null);
		DefaultTableColumnModel cm = (DefaultTableColumnModel) detailTable
				.getColumnModel();
		cm.getColumn(0).setHeaderValue("书名");
		cm.getColumn(1).setHeaderValue("作者");
		cm.getColumn(2).setHeaderValue("大小");
		cm.getColumn(3).setHeaderValue("类型");
		cm.getColumn(4).setHeaderValue("路径");
		cm.getColumn(5).setHeaderValue("评分");
		cm.getColumn(0).setPreferredWidth(235);
		cm.getColumn(0).setMinWidth(100);
//		cm.getColumn(0).setMaxWidth(300);
		cm.getColumn(1).setPreferredWidth(100);
		cm.getColumn(1).setMinWidth(50);
		cm.getColumn(1).setMaxWidth(100);
		cm.getColumn(1).setCellRenderer(new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 9156057311551037177L;

			@Override
			protected void setValue(Object obj) {
				if (obj instanceof String){
					String author = (String) obj;
					if (SqliteHelper.isFavoriteAuthors(author)){
						setForeground(Color.RED);
					} else {
						setForeground(Color.BLACK);
					}
				}
				super.setValue(obj);
			}
		});
		cm.getColumn(2).setPreferredWidth(50);
		cm.getColumn(2).setMinWidth(50);
		cm.getColumn(2).setMaxWidth(100);
		cm.getColumn(3).setPreferredWidth(175);
		cm.getColumn(3).setMinWidth(150);
		cm.getColumn(4).setPreferredWidth(100);
		cm.getColumn(4).setMinWidth(50);
		cm.getColumn(5).setPreferredWidth(90);
		cm.getColumn(5).setMinWidth(90);
		cm.getColumn(5).setMaxWidth(90);
		cm.getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
		cm.getColumn(3).getCellEditor().addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent changeevent) {
				Book book = bookList.get(detailTable.getSelectedRow());
				String oldType = book.getType();
				Object type = detailTable.getValueAt(detailTable.getSelectedRow(), detailTable.getSelectedColumn());
				book.setType(String.valueOf(type).trim());
				SqliteHelper.updateBookType(book, oldType);
			}
			
			@Override
			public void editingCanceled(ChangeEvent changeevent) {}
		});
		// 表格鼠标悬停提示
		detailTable.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent mouseevent) {
				int row = detailTable.rowAtPoint(mouseevent.getPoint());
				int col = detailTable.columnAtPoint(mouseevent.getPoint());
				if (row > -1 && col > -1) {
					Object value = detailTable.getValueAt(row, col);
					if (null != value && !(value instanceof ImageIcon) &&!"".equals(value))
						detailTable.setToolTipText(value.toString());// 悬浮显示单元格内容
					else
						detailTable.setToolTipText(null);// 关闭提示
				}
			}

			@Override
			public void mouseDragged(MouseEvent mouseevent) {
			}
		});
		// 表格点击事件
		detailTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				// 双击左键,作者栏则检索作者,路径则打开文件
				if (mouseevent.getButton() == MouseEvent.BUTTON1 && mouseevent.getClickCount() >= 2) {
					ImageTableModel model = (ImageTableModel) detailTable
							.getModel();
					if (detailTable.getColumnName(
							detailTable.getSelectedColumn()).equals("B")) { // 点击作者列
						authorRadioButton.setSelected(true);
						searchType = SearchType.AUTHOR;
						searchTextField.setText((String) model.getValueAt(
								detailTable.getSelectedRow(), 1));
					} else if (detailTable.getColumnName(
							detailTable.getSelectedColumn()).equals("E")) { // 点击路径列
						String path = (String) model.getValueAt(detailTable
								.getSelectedRow(), 4);
						
						if (path.equals("") || !(new File(path).exists())){
							showDialog("<html><body>该文件本地已经被删除,无法直接打开<br>请另存以后自行查看</body></html>");
							Book book = bookList.get(detailTable.getSelectedRow());
							book.setPath("");
							SqliteHelper.updateBook(book);
							detailTable.setValueAt("", detailTable.getSelectedRow(), detailTable.getSelectedColumn());
							return;
						}
						
						try {
							Runtime.getRuntime().exec("notepad \"" + path + "\"");
						} catch (IOException e) {
							showDialog("<html><body>无法直接打开该文件,原因为" + e.getMessage()
									+ "<br>请另存以后自行查看</body></html>");
						}
					} else if (detailTable.getColumnName(
							detailTable.getSelectedColumn()).equals("F")) { // 点击评分列,清零评分
						// 点击选择评分并更新数据库
						int index = detailTable.getSelectedRow();
						
						Book book = bookList.get(index);
						book.setScore(0);
						SqliteHelper.updateBook(book);
						
						detailTable.getModel().setValueAt(FileUtil.getScoreImage(0)
								, detailTable.getSelectedRow(), detailTable.getSelectedColumn());
					}
				} else if (mouseevent.getButton() == MouseEvent.BUTTON3){
					if (detailTable.getSelectedRowCount() == 0){
						int row = detailTable.rowAtPoint(mouseevent.getPoint());
						int column = detailTable.columnAtPoint(mouseevent.getPoint());
						detailTable.changeSelection(row, column, false, false);
					}
					int selectColumn = detailTable.columnAtPoint(mouseevent.getPoint());
					if (detailTable.getColumnName(selectColumn).equals("B")) { // 点击作者列
						int[] rows = detailTable.getSelectedRows();
						final String[] author = new String[rows.length];
						for (int i = 0; i <  rows.length; i++){
							author[i] = (String) detailTable.getValueAt(rows[i],
									detailTable.getSelectedColumn());
						}
						
						final JPopupMenu popMenu = new JPopupMenu();
						JMenuItem setFavoriteItem = new JMenuItem("收藏");
						setFavoriteItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent actionevent) {
								SqliteHelper.insertFavoriteAuthor(author);
								detailTable.repaint();
							}
						});
						popMenu.add(setFavoriteItem);
						JMenuItem cancelFavoriteItem = new JMenuItem("取消收藏");
						cancelFavoriteItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent actionevent) {
								SqliteHelper.deleteFavoriteAuthor(author);
								detailTable.repaint();
							}
						});
						popMenu.add(cancelFavoriteItem);
						popMenu.show(mouseevent.getComponent(), mouseevent.getX(), mouseevent.getY());
					} else {
						// 鼠标右键点击事件,弹出菜单
						int[] rows = detailTable.getSelectedRows();
						if (rows.length == 0){
							return;
						}
						
						final JPopupMenu popMenu = new JPopupMenu();
						JMenuItem saveItem = new JMenuItem("保存");
						saveItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent actionevent) {
								saveFiles();
							}
						});
						JMenuItem deleteItem = new JMenuItem("删除");
						deleteItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent actionevent) {
								deleteBooks();
							}
						});
						popMenu.add(saveItem);
						popMenu.add(deleteItem);
						popMenu.show(mouseevent.getComponent(), mouseevent.getX(), mouseevent.getY());
					}
				} else if (mouseevent.getButton() == MouseEvent.BUTTON1){
					if (detailTable.getColumnName(
							detailTable.getSelectedColumn()).equals("F")) {
						// 点击选择评分并更新数据库
						int index = detailTable.getSelectedRow();
						
						int clickX = mouseevent.getX();
						int tableX = detailTable.getWidth();
						int score = 5 - ((tableX - clickX) / 18);
						
						Book book = bookList.get(index);
						book.setScore(score);
						SqliteHelper.updateBook(book);
						
						detailTable.getModel().setValueAt(FileUtil.getScoreImage(score)
								, detailTable.getSelectedRow(), detailTable.getSelectedColumn());
					}
				}
			}
		});

		return pane;
	}

	/**
	 * 创建底部面板,包括分页
	 * @return
	 */
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.add(lastButton, -1);
		panel.add(nextButton, -1);
		totalLabel.setText("共?页，第?页");
		panel.add(totalLabel, -1);
		panel.add(new JLabel("跳转到"), -1);
		panel.add(pageNoTextField, -1);
		panel.add(new JLabel("页"), -1);
		panel.add(pageButton, -1);

		// 各按钮点击事件
		lastButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				updateTable(currentKey, pageNo - 1);
			}
		});
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				updateTable(currentKey, pageNo + 1);
			}
		});
		pageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				int i = 1;
				try {
					i = Integer.parseInt(pageNoTextField.getText());
				} catch (Exception e) {
					i = 1;
				}
				updateTable(currentKey, i);
			}
		});

		return panel;
	}

	/**
	 * 检索db并更新树
	 */
	private void searchAndUpdateTree() {
		String condition = searchTextField.getText();
		String favoriteAuthorType = (String) favoriteAuthorComboBox.getSelectedItem();
		keys = SqliteHelper.getKeys(searchType, condition, favoriteAuthorType);
		root.removeAllChildren();
		for (String key : keys) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
			root.add(node);
		}
		recordTree.updateUI();
		recordTree.expandRow(0);
		recordTree.setSelectionRow(0);
		currentKey = null;

		ImageTableModel model = (ImageTableModel) detailTable.getModel();
		// 删除所有现在table中的数据
		model.setRowCount(0);
		updateTable(currentKey, 1);
	}

	/**
	 * 更新表格
	 * @param key
	 * @param pageNo
	 */
	private void updateTable(String key, int pageNo) {
		Map<SearchType, Object> map = new HashMap<SearchType, Object>();
		if (searchType == SearchType.AUTHOR){ // 检索作者
			String condition = (key == null || key.isEmpty())? searchTextField.getText(): key;
			if (!condition.equals("")){
				map.put(searchType, condition);
			}
		} else if (searchType == SearchType.TYPE){ // 
			String condition = (key == null || key.isEmpty())? searchTextField.getText(): key;
			if (!condition.equals("")){
				map.put(searchType, condition);
			}
		} else  if (searchType == SearchType.NAME){ // 根据书名检索
			if (key != null && !key.trim().isEmpty()){
				map.put(SearchType.DATE, key);
			}
			String condition = searchTextField.getText();
			if (!condition.trim().equals("")){
				map.put(searchType, condition);
			}
		}
		map.put(SearchType.FAVORITE_AUTHOR, (String) favoriteAuthorComboBox.getSelectedItem());
		// 评分条件
		String score = (String) scoreComboBox.getSelectedItem();
		if (!score.equals("all")){
			Integer s = Integer.parseInt(score);
			map.put(SearchType.SCORE, s);
		}
		
		ImageTableModel model = (ImageTableModel) detailTable.getModel();
		// 删除所有现在table中的数据
		model.setRowCount(0);

		int totalPage = (int) Math.ceil((SqliteHelper.getBookCount(map, isEqual) + 0.0) / rowCount);
		if (pageNo > totalPage) {
			this.pageNo = totalPage;
		} else {
			this.pageNo = pageNo;
		}
		if (this.pageNo < 1) {
			this.pageNo = 1;
		}
		totalLabel.setText("共" + totalPage + "页，第" + this.pageNo + "页");
		bookList = SqliteHelper.queryPage(map, this.pageNo, isEqual);
		int total = bookList.size();

		for (int i = 0; i < rowCount; i++) {
			if (i >= total) {
				break;
			}
			Book book = bookList.get(i);
			model.addRow(new Object[]{book.getName(), book.getAuthor(),
					(book.getLength() / 1024) + "KB", book.getType(), book.getPath(),
					FileUtil.getScoreImage(book.getScore())});
		}
		detailTable.updateUI();
	}

	/**
	 * 根据树上所选择的节点,保存文件到指定目录
	 */
	private void save() {
		JFileChooser fileChooser = new JFileChooser(FileSystemView
				.getFileSystemView().getHomeDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		int ret = fileChooser.showOpenDialog(null);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		final String dirPath = fileChooser.getSelectedFile().getAbsolutePath();
		
		final TreePath[] selectionPaths = recordTree.getSelectionPaths();
		Arrays.sort(selectionPaths, new Comparator<TreePath>() {
			@Override
			public int compare(TreePath o1, TreePath o2) {
				DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) o1
						.getLastPathComponent();
				DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) o2
						.getLastPathComponent();
				if (node1.getLevel() < node2.getLevel()) {
					return -1;
				} else if (node1.getLevel() == node2.getLevel()) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		DefaultMutableTreeNode node0 = (DefaultMutableTreeNode) selectionPaths[0]
				.getLastPathComponent();
		if (node0.getLevel() == 0) {
			showDialog("保存文件中", 999999, new Runnable() {
				
				@Override
				public void run() {
					int i = recordTree.getRowCount();
					for (int j = 0; j < i; j++ ) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) recordTree.getPathForRow(j)
								.getLastPathComponent();
						if (node.getLevel() == 0){
							continue;
						}
						String key = (String) node.getUserObject();
						Map<SearchType, Object> map = new HashMap<SearchType, Object>();
						if (searchType == SearchType.NAME){
							map.put(SearchType.DATE, key);
						} else {
							map.put(searchType, key);
						}
						String score = (String) scoreComboBox.getSelectedItem();
						if (!"all".equals(score)){
							map.put(SearchType.SCORE, Integer.parseInt(score));
						}
						List<Book> list = SqliteHelper.queryAll(map, isEqual);
						for (Book book : list) {
							try {
								copyFile(new File(book.getPath()), dirPath + File.separator + key, book);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		} else {
			showDialog("保存文件中", 999999, new Runnable() {
				
				@Override
				public void run() {
					for (TreePath treePath : selectionPaths) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath
								.getLastPathComponent();
						String key = (String) node.getUserObject();
						Map<SearchType, Object> map = new HashMap<SearchType, Object>();
						if (searchType == SearchType.NAME){
							map.put(SearchType.DATE, key);
						} else {
							map.put(searchType, key);
						}
						String score = (String) scoreComboBox.getSelectedItem();
						if (!"all".equals(score)){
							map.put(SearchType.SCORE, Integer.parseInt(score));
						}
						List<Book> list = SqliteHelper.queryAll(map, isEqual);
						for (Book book : list) {
							try {
								copyFile(new File(book.getPath()), dirPath + File.separator + key, book);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
	}
	
	/**
	 * 根据表格所选,保存文件到指定目录
	 */
	private void saveFiles(){
		JFileChooser fileChooser = new JFileChooser(FileSystemView
				.getFileSystemView().getHomeDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int ret = fileChooser.showSaveDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			final String dirPath = fileChooser.getSelectedFile()
					.getAbsolutePath();
			showDialog("保存文件中", 999999, new Runnable() {
				public void run() {
					int[] rows = detailTable.getSelectedRows();
					for (int row : rows) {
						String path = (String) ((ImageTableModel) detailTable.getModel()).getValueAt(row, 4);
						try {
							Book book = bookList.get(row);
							copyFile(new File(path), dirPath, book);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	private void copyFile(File srcFile, String targetDir, Book book) throws Exception {
		if (srcFile.exists()){
			FileUtil.copyFile(srcFile, targetDir);
		} else {
			String content = SqliteHelper.getContent(book.getId());
			File dir = new File(targetDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File target = new File(targetDir, book.getFilename());
			target.createNewFile();
			FileUtil.writeFile(target, content);
		}
	}
	
	/**
	 * 显示模态提示框
	 * @param msg
	 */
	private void showDialog(String msg){
		showDialog(msg, null, null);
	}
	
	private void showDialog(String msg, Integer timeout, Runnable run){
		JDialog dialog = new JTimerDialog(this, "提示", msg, timeout, run);
		dialog.setModal(true);
		dialog.setVisible(true);
	}


	private void importToDb(String dir, final int year, final int month) {
		showDialog("正在导入中!", Conf.getImportTimeout(), new Runnable() {
			
			@Override
			public void run() {
				String dir = dirTextField.getText();
				if (!dir.endsWith("\\")){
					dir += "\\";
				}
				List<BookBean> list = FileUtil.deepSearch(new File(dir));
				if (list == null){
					return;
//					throw new NullPointerException();
				}
				SqliteHelper.importToDb(list, year, month);
			}
		});
	}

	private String[] getYears() {
		Calendar now = Calendar.getInstance();
		int yearOfNow = now.get(Calendar.YEAR);
		int firstYear = 2006;
		String[] ret = new String[yearOfNow - firstYear + 1];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (firstYear + i) + "";
		}
		return ret;
	}
	
	private void deleteBooks(){
		int ret = JOptionPane.showConfirmDialog(this, "是否要删除选定的文件?", "确认窗口"
				, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (ret == JOptionPane.YES_OPTION){
			showDialog("正在删除中!", Conf.getImportTimeout(), new Runnable() {
				
				@Override
				public void run() {
					int[] rows = detailTable.getSelectedRows();
					Arrays.sort(rows);
					DefaultTableModel model = (DefaultTableModel) detailTable.getModel();
					for (int i = rows.length; i > 0; i--){
						int row = rows[i - 1];
						Book book = bookList.get(row);
						SqliteHelper.deleteBook(book);
						model.removeRow(row);
					}
				}
			});
		} else {
			return;
		}
	}
	
	private void deleteByDate(){
		int ret = JOptionPane.showConfirmDialog(this, "是否要删除选定的文件?", "确认窗口"
				, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (ret == JOptionPane.YES_OPTION){
			showDialog("正在删除中!", Conf.getImportTimeout(), new Runnable() {
				
				@Override
				public void run() {
					final TreePath[] selectionPaths = recordTree.getSelectionPaths();
					for (TreePath treePath : selectionPaths) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath
								.getLastPathComponent();
						if (node.getLevel() == 0){
							continue;
						}
						String key = (String) node.getUserObject();
						SqliteHelper.deleteByDate(key);
						root.remove(node);
					}
					searchAndUpdateTree();
				}
			});
		} else {
			return;
		}
	}
}
