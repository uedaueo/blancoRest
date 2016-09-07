package blanco.rest.common;

/**
 * log levelの定義
 *
 * @author tueda
 *
 */
public enum LogLevel {
		/** 危険 */
		LOG_EMERG,
		/** 緊急 */
		LOG_ALERT,
		/** 致命的 */
		LOG_CRIT,
		/** エラー */
		LOG_ERR,
		/** 警告 */
		LOG_WARNING,
		/** 通知 */
		LOG_NOTICE,
		/** 情報 */
		LOG_INFO,
		/** デバッグ */
		LOG_DEBUG;
}
