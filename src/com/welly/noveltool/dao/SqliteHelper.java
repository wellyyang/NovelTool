package com.welly.noveltool.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.welly.noveltool.dao.po.Book;
import com.welly.noveltool.dao.po.BookContent;
import com.welly.noveltool.dao.po.FavoriteAuthor;
import com.welly.noveltool.dao.po.Path;
import com.welly.noveltool.dao.po.Type;
import com.welly.noveltool.util.BookBean;
import com.welly.noveltool.util.Conf;
import com.welly.noveltool.util.FileUtil;
import com.welly.noveltool.util.SearchType;

/**
 * sqlite帮助类
 * 
 * @author welly
 * 
 */
public class SqliteHelper {

	private static String dbPath = Conf.getDbpath();

	private static ConnectionSource conn;

	private static Dao<Book, Long> bookDao;

	private static Dao<Type, String> typeDao;

	private static Dao<BookContent, Long> bookContentDao;

	private static Dao<Path, Integer> pathDao;
	
	private static Dao<FavoriteAuthor, String> favoriteAuthorDao;
	
	private static Set<String> favoriteAuthors = new ConcurrentSkipListSet<>();

	static {
		try {
			// 载入驱动
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		init();
	}

	public static void cleanCache(){
		try {
			bookContentDao.executeRawNoArgs("VACUUM");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteBook(Book book){
		try {
			bookDao.delete(book);
			deletePhantomTypes(book.getType());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteByDate(final String date) {
		try {
			bookDao.callBatchTasks(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					int year = Integer.parseInt(date.split("-")[0]);
					int month = Integer.parseInt(date.split("-")[1]);
					GenericRawResults<String[]> rawResults = bookDao.queryRaw("select type from book " +
							" where year = "  + year + 
							" and month = " + month);
					List<String[]> results = rawResults.getResults();
					
					bookContentDao.executeRaw("delete from bookcontent "
							+ "where id in ("
							+ 	"select bc_id from book where year = " 
							+ 	year + " and month = " + month
							+ ")");
					
					bookDao.executeRaw("delete from book " +
							" where year = "  + year + 
							" and month = " + month);
					
					for(String[] strs: results){
						String type = strs[0];
						deletePhantomTypes(type);
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteFavoriteAuthor(String...author){
		for (String s: author){
			FavoriteAuthor fa = new FavoriteAuthor();
			fa.setName(s);
			try {
				favoriteAuthorDao.delete(fa);
				favoriteAuthors.remove(s);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deletePhantomTypes(String type){
		if (type == null || type.length() == 0){
			return;
		}
		String[] types = type.split("\\s+");
		for (String t: types) {
			String deletePhantomType = "delete from type where name = '" + t + "' " +
					" and not exists (select id from book b where b.type = '" + t + "' or " +
							"b.type like '%" + t + " %'" + " or " +
							"b.type like '% " + t + "%'" + ")";
			try {
				typeDao.executeRaw(deletePhantomType);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static long getBookCount(Map<SearchType, Object> map, boolean isEqual){
		int size = map == null? 0: map.size();
		long count = 0;
		try{
			if (size == 0){
				count = bookDao.countOf(bookDao.queryBuilder().setCountOf(true).where().eq("newest", true).prepare());
			} else {
				int i = 0;
				Where<Book, Long> where = bookDao.queryBuilder().setCountOf(true).where();
				
				for (SearchType type: map.keySet()) {
					if (i > 0){
						where = where.and();
					}
					if (type == SearchType.AUTHOR || type == SearchType.NAME){
						if ("None".equalsIgnoreCase((String) map.get(type))){
							where = where.eq(type.toString(), "None");
						} else if (isEqual){
							where = where.eq(type.toString(), (String) map.get(type));
						} else {
							where = where.like(type.toString(), "%" + map.get(type) + "%");
						}
					} else if (type == SearchType.SCORE){
						where = where.eq(type.toString(), (Integer) map.get(type));
					} else if (type == SearchType.TYPE){
						if ("None".equalsIgnoreCase((String) map.get(type))){
							where = where.eq(type.toString(), "None");
						} else if (isEqual){
							where = where.or(where.like(type.toString(), "%" + map.get(type) + " %")
									, where.like(type.toString(), "% " + map.get(type) + "%")
									, where.eq(type.toString(), (String) (map.get(type))));
						} else {
							where = where.like(type.toString(), "%" + map.get(type) + "%");
						}
					} else if (type == SearchType.DATE){
						String date = map.get(SearchType.DATE).toString();
						int year = Integer.parseInt(date.split("-")[0]);
						int month = Integer.parseInt(date.split("-")[1]);
						where = where.eq("year", year).and().eq("month", month);
					}
					i++;
				}
				count = bookDao.countOf(where.and().eq("newest", true).prepare());
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return count;
	}

	public static String getContent(Long id){
		try {
			return bookContentDao.queryForId(id).getContent();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String[] getKeys(SearchType searchType, String value) {
		return getKeys(searchType, value, null);
	}
	
	public static String[] getKeys(SearchType searchType, String value, String favoriteAuthorType) {
		try {
			if (searchType == SearchType.AUTHOR) {
				List<Book> list = bookDao.query(bookDao.queryBuilder()
						.groupBy("author")
						.where().like("author", "%" + value == null? "": value +"%")
						.prepare());
				if (favoriteAuthorType.equals("所有")){
					return list.stream().map(book -> book.getAuthor())
							.collect(Collectors.toList()).toArray(new String[0]);
				} else if (favoriteAuthorType.equals("已收藏")){
					return list.stream().map(book -> book.getAuthor())
							.filter(SqliteHelper::isFavoriteAuthors)
							.collect(Collectors.toList()).toArray(new String[0]);
				} else if (favoriteAuthorType.equals("未收藏")){
					return list.stream().map(book -> book.getAuthor())
							.filter(author -> !isFavoriteAuthors(author))
							.collect(Collectors.toList()).toArray(new String[0]);
				}
				String[] keys = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
					keys[i] = list.get(i).getAuthor();
				}
				return keys;
			} else if (searchType == SearchType.SCORE) {
				return new String[] {};
			} else if (searchType == SearchType.NAME) {
				List<Book> list = bookDao.query(bookDao.queryBuilder().groupBy("year").groupBy("month").prepare());
				String[] keys = new String[list.size()];
				for (int i = 0; i < keys.length; i++) {
					Book book = list.get(i);
					String year = book.getYear() + "";
					String month = book.getMonth() < 10? "0" + book.getMonth(): book.getMonth() + "";
					keys[i] = year + "-" + month;
				}
				return keys;
			} else if (searchType == SearchType.TYPE) {
				List<Type> list = typeDao.query(typeDao.queryBuilder()
									.where().like("name", "%" + value == null? "": "%" + value +"%")
									.prepare());
				String[] keys = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
					keys[i] = list.get(i).getName();
				}
				return keys;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new String[] {};
	}
	
	public static String getLatestPath(){
		try {
			return pathDao.queryForFirst(pathDao.queryBuilder().orderBy("id", false).prepare()).getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void importToDb(final List<BookBean> bookList, final int year, final int month) {
		try {
			bookDao.callBatchTasks(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					for (BookBean bookBean : bookList) {
						Book book = new Book(bookBean, year, month);
						BookContent bc = new BookContent();
						bc.setContent(FileUtil.readFile(bookBean.getPath()));
						book.setBc(bc);
						try {
							bookDao.create(book);
							String[] types = bookBean.getTypes();
							for (String type : types) {
								Type t = new Type();
								t.setName(type);
								typeDao.createIfNotExists(t);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateDuplicated();
	}
	
	/**
	 * 初始化orman
	 */
	public static void init() {
		try {
			conn = new JdbcConnectionSource("jdbc:sqlite://" + dbPath);
			try {
				TableUtils.createTableIfNotExists(conn, Type.class);
			} catch (SQLException e1) {
			}
			try {
				TableUtils.createTableIfNotExists(conn, Book.class);
			} catch (SQLException e1) {
			}
			try {
				TableUtils.createTableIfNotExists(conn, BookContent.class);
			} catch (SQLException e1) {
			}
			try {
				TableUtils.createTableIfNotExists(conn, Path.class);
			} catch (SQLException e1) {
			}
			try {
				TableUtils.createTableIfNotExists(conn, FavoriteAuthor.class);
			} catch (SQLException e1) {
			}
			bookDao = DaoManager.createDao(conn, Book.class);
			typeDao = DaoManager.createDao(conn, Type.class);
			bookContentDao = DaoManager.createDao(conn, BookContent.class);
			pathDao = DaoManager.createDao(conn, Path.class);
			favoriteAuthorDao = DaoManager.createDao(conn, FavoriteAuthor.class);
			
			List<FavoriteAuthor> authorList = favoriteAuthorDao.queryForAll();
			favoriteAuthors.addAll(authorList.stream().map(author -> author.getName()).collect(Collectors.toSet()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void insertFavoriteAuthor(String...author){
		for (String s: author){
			FavoriteAuthor fa = new FavoriteAuthor();
			fa.setName(s);
			try {
				favoriteAuthorDao.createIfNotExists(fa);
				favoriteAuthors.add(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void insertPath(String path){
		Path p = new Path();
		p.setPath(path);
		p.setDate(new Date(System.currentTimeMillis()).toString());
		try {
			pathDao.create(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Book> queryAll(Map<SearchType, Object> map, boolean isEqual) {
		int size = map == null? 0: map.size();
		List<Book> list = null;
		try{
			QueryBuilder<Book, Long> qb = bookDao.queryBuilder()
					.orderBy("length", false);
			if (size == 0){
				list = bookDao.query(qb.where().eq("newest", true).prepare());
			} else {
				int i = 0;
				Where<Book, Long> where = qb.where();
				
				for (SearchType type: map.keySet()) {
					if (i > 0){
						where = where.and();
					}
					if (type == SearchType.AUTHOR || type == SearchType.NAME){
						if ("None".equalsIgnoreCase((String) map.get(type))){
							where = where.eq(type.toString(), "None");
						} else if (isEqual){
							where = where.eq(type.toString(), (String) map.get(type));
						} else {
							where = where.like(type.toString(), "%" + map.get(type) + "%");
						}
					} else if (type == SearchType.SCORE){
						where = where.eq(type.toString(), (Integer) map.get(type));
					} else if (type == SearchType.TYPE){
						if ("None".equalsIgnoreCase((String) map.get(type))){
							where = where.eq(type.toString(), "None");
						} else if (isEqual){
							where = where.or(where.like(type.toString(), "%" + map.get(type) + " %")
									, where.like(type.toString(), "% " + map.get(type) + "%")
									, where.eq(type.toString(), (String) (map.get(type))));
						} else {
							where = where.like(type.toString(), "%" + map.get(type) + "%");
						}
					} else if (type == SearchType.DATE){
						String date = map.get(SearchType.DATE).toString();
						int year = Integer.parseInt(date.split("-")[0]);
						int month = Integer.parseInt(date.split("-")[1]);
						where = where.eq("year", year).and().eq("month", month);
					}
					i++;
				}
				list = bookDao.query(where.and().eq("newest", true).prepare());
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return list == null? new ArrayList<Book>(): list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Book> queryPage(Map<SearchType, Object> map, int pageno, boolean isEqual) {
		int size = map == null? 0: map.size();
		List<Book> list = null;
		try{
			QueryBuilder<Book, Long> qb = bookDao.queryBuilder()
					.orderBy("length", false)
					.offset((long)((pageno > 0? pageno - 1: 0) * Conf.getPagecount()))
					.limit((long) Conf.getPagecount());
			if (size == 0){
				list = bookDao.query(qb.where().eq("newest", true).prepare());
			} else {
				int i = 0;
				Where<Book, Long> where = qb.where();
				
				for (SearchType type: map.keySet()) {
					if (i > 0){
						where = where.and();
					}
					if (type == SearchType.AUTHOR || type == SearchType.NAME){
						if ("None".equalsIgnoreCase((String) map.get(type))){
							where = where.eq(type.toString(), "None");
						} else if (isEqual){
							where = where.eq(type.toString(), (String) map.get(type));
						} else {
							where = where.like(type.toString(), "%" + map.get(type) + "%");
						}
					} else if (type == SearchType.SCORE){
						where = where.eq(type.toString(), (Integer) map.get(type));
					} else if (type == SearchType.TYPE){
						if ("None".equalsIgnoreCase((String) map.get(type))){
							where = where.eq(type.toString(), "None");
						} else if (isEqual){
							where = where.or(where.like(type.toString(), "%" + map.get(type) + " %")
									, where.like(type.toString(), "% " + map.get(type) + "%")
									, where.eq(type.toString(), (String) (map.get(type))));
						} else {
							where = where.like(type.toString(), "%" + map.get(type) + "%");
						}
					} else if (type == SearchType.DATE){
						String date = map.get(SearchType.DATE).toString();
						int year = Integer.parseInt(date.split("-")[0]);
						int month = Integer.parseInt(date.split("-")[1]);
						where = where.eq("year", year).and().eq("month", month);
					}
					i++;
				}
				list = bookDao.query(where.and().eq("newest", true).prepare());
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return list == null? new ArrayList<Book>(): list;
	}

	public static void updateBook(Book book){
		try {
			bookDao.update(book);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateBookType(Book book, String oldType) {
		updateBook(book);
		try {
			for (String type : book.getType().split("\\s+")) {
				Type t = new Type();
				t.setName(type);
				typeDao.createIfNotExists(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		deletePhantomTypes(oldType);
		
	}

	public static void updateDuplicated() {
		try {
			// 更新评分
			String updateScore = "update book set score = " +
					"(select score from book b where book.name = b.name and book.author = b.author and b.newest = 1)";
			bookDao.updateRaw(updateScore);
			
			// 更新最新数据的最新标识为1
			String updateNewest = "update book set newest = 1 " +
					"where id in (select id from book group by name, author having length = max(length))";
			String updateNotNewest = "update book set newest = 0 " +
					"where id not in (select id from book group by name, author having length = max(length))";
			
			bookDao.updateRaw(updateNewest);
			bookDao.updateRaw(updateNotNewest);

			// 删除文件内容
			DeleteBuilder<BookContent, Long> bookContentDeleteBuilder = bookContentDao.deleteBuilder();
			Where<Book, Long> deleteBookContentWhere = bookDao.queryBuilder().where();
			QueryBuilder<Book, Long> bookQueryBuilder = bookDao.queryBuilder();
			bookQueryBuilder = bookQueryBuilder.selectRaw("bc_id");
			bookQueryBuilder.setWhere(deleteBookContentWhere.eq("newest", false));
			bookContentDeleteBuilder.setWhere(bookContentDeleteBuilder.where()
					.in("id", bookQueryBuilder));
			bookContentDao.delete(bookContentDeleteBuilder.prepare());
			// 删除所有重复的数据
			DeleteBuilder<Book, Long> bookDeleteBuilder = bookDao.deleteBuilder();
			Where<Book, Long> deleteBookWhere = bookDao.queryBuilder().where();
			QueryBuilder<Book, Long> bookQueryBuilder2 = bookDao.queryBuilder();
			bookQueryBuilder2 = bookQueryBuilder2.selectRaw("id");
			bookQueryBuilder2.setWhere(deleteBookWhere.eq("newest", false));
			bookDeleteBuilder.setWhere(bookDeleteBuilder.where()
					.in("id", bookQueryBuilder2));
			bookDao.delete(bookDeleteBuilder.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isFavoriteAuthors(String author) {
		return favoriteAuthors.contains(author);
	}
}
