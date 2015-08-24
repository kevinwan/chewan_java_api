package com.gongpingjia.carplay.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 数据库操作公共类
 * 
 * @author licheng
 *
 */
public class DASUtil {

	private static SqlSessionFactory sessionFactory = BeanUtil.getBean("sqlSessionFactory", SqlSessionFactory.class);

	public static <T> List<T> selectList(String sqlName) {
		List<T> list = new ArrayList<T>(0);
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			list = session.selectList(sqlName);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return list;
	}

	public static <T> List<T> selectList(String sqlName, Object param) {
		List<T> list = new ArrayList<T>(0);
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			list = session.selectList(sqlName, param);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return list;
	}

	public static <T> List<T> selectList(String packageName, String sqlName, Object param) {
		return selectList(packageName + "." + sqlName, param);
	}

	public static <T> List<T> selectList(String packageName, String sqlName) {
		return selectList(packageName + "." + sqlName);
	}

	public static <T> T selectOne(String sqlName, Object param) {
		T result = null;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			result = session.selectOne(sqlName, param);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return result;
	}

	public static <T> T selectOne(String sqlName) {
		T result = null;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			result = session.selectOne(sqlName);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return result;
	}

	public static <T> T selectOne(String packageName, String sqlName) {
		return selectOne(packageName + "." + sqlName);
	}

	public static <T> T selectOne(String packageName, String sqlName, Object param) {
		return selectOne(packageName + "." + sqlName, param);
	}

	public static <T> int save(String sqlName, T param) {
		int rows = 0;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			rows = session.insert(sqlName, param);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rows;
	}

	public static <T> int save(String packageName, String sqlName, T param) {
		return save(packageName + "." + sqlName, param);
	}

	public static <T> int saveList(String sqlName, List<T> paramList) {
		int rows = 0;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			for (T param : paramList) {
				rows += session.insert(sqlName, param);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rows;
	}

	public static <T> int saveList(String packageName, String sqlName, List<T> paramList) {
		return saveList(packageName + "." + sqlName, paramList);
	}

	public static <T> int update(String sqlName, T param) {
		int rows = 0;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			rows = session.update(sqlName, param);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rows;
	}

	public static <T> int update(String packageName, String sqlName, Object param) {
		return update(packageName + "." + sqlName, param);
	}

	public static <T> int updateList(String sqlName, List<T> paramList) {
		int rows = 0;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			for (T param : paramList) {
				rows += session.update(sqlName, param);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rows;
	}

	public static <T> int updateList(String packageName, String sqlName, List<T> paramList) {
		return updateList(packageName + "." + sqlName, paramList);
	}

	public static <T> int delete(String sqlName, T param) {
		int rows = 0;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			rows = session.delete(sqlName, param);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rows;
	}

	public static <T> int delete(String packageName, String sqlName, T param) {
		return delete(packageName + "." + sqlName, param);
	}

	public static <T> int deleteList(String sqlName, List<T> paramList) {
		int rows = 0;
		SqlSession session = null;
		try {
			session = sessionFactory.openSession();
			for (T param : paramList) {
				rows += session.delete(sqlName, param);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rows;
	}

	public static <T> int deleteList(String packageName, String sqlName, List<T> paramList) {
		return deleteList(packageName + "." + sqlName, paramList);
	}
}
