package blanco.rest.common;

import java.util.Calendar;
import java.util.Random;

/**
 * ユーティリティクラス
 *
 * @author tueda
 *
 */
public class Util {

	private static Random rnd = null;

	public static void initRandom(int seed) {
		rnd = new Random(seed);
	}

	/**
	 * 0 - max (exclusive) までの整数乱数値を返す
	 *
	 * @param max
	 * @return 乱数値
	 */
	public static int getIntRnd(int max) {
		if (null == rnd)
			initRandom(max);

		return rnd.nextInt(max);
	}


	/**
	 * Debug 用 println<br>
	 * log level:<br>
	 * LOG_EMERG       0       system is unusable<br>
	 * LOG_ALERT       1       action must be taken immediately<br>
	 * LOG_CRIT        2       critical conditions<br>
	 * LOG_ERR         3       error conditions<br>
	 * LOG_WARNING     4       /* warning conditions<br>
	 * LOG_NOTICE      5       /* normal but significant condition<br>
	 * LOG_INFO        6       /* informational<br>
	 * LOG_DEBUG       7       /* debug-level messages<br>
	 *
	 * @param level log level.
	 * @param str log文字列
	 */
	public static void infoPrintln(LogLevel level, String str) {
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);        //(2)現在の年を取得
		int month = cal.get(Calendar.MONTH) + 1;  //(3)現在の月を取得
		int day = cal.get(Calendar.DATE);         //(4)現在の日を取得
		int hour = cal.get(Calendar.HOUR_OF_DAY); //(5)現在の時を取得
		int minute = cal.get(Calendar.MINUTE);    //(6)現在の分を取得
		int second = cal.get(Calendar.SECOND);    //(7)現在の秒を取得

		String logThreshold = Config.properties.getProperty(Config.logLevelKey);
		if (logThreshold == null)
			logThreshold = "LOG_DEBUG";

		if (/*level.compareTo(LogLevel.valueOf(logThreshold)) <= 0*/true) {
			String strLevel = null;
			switch (level) {
				case LOG_EMERG:
					strLevel = "!!!EMERG!!!";
					break;
				case LOG_ALERT:
					strLevel = "!!ALERT!!";
					break;
				case LOG_CRIT:
					strLevel = "!!CRIT!!";
					break;
				case LOG_ERR:
					strLevel = "!!ERR!!";
					break;
				case LOG_WARNING:
					strLevel = "(WARN)";
					break;
				case LOG_NOTICE:
					strLevel = "(NOTICE)";
					break;
				case LOG_INFO:
					strLevel = "(INFO)";
					break;
				case LOG_DEBUG:
					strLevel = "(DEBUG)";
					break;
				default:
					strLevel = "(???)";
			}

			System.err.printf("[%4d/%02d/%02d %02d:%02d:%02d] %s %s\n",
					year, month, day, hour, minute, second, str,
					strLevel.toString());

		}
	}
}
