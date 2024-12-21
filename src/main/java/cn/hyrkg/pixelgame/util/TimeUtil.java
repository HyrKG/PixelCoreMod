package cn.hyrkg.pixelgame.util;

public class TimeUtil {
	public static String getTimeLeftAsChinese(long time) {
		long hour = time / 3600L;
		long minutes = time / 60L - hour * 60L;
		long second = time - hour * 60L * 60L - minutes * 60L;
		if (hour > 0) {
			return fillEmpty(hour) + "小时" + fillEmpty(minutes) + "分" + fillEmpty(second) + "秒";
		} else if (minutes > 0) {
			return fillEmpty(minutes) + "分" + fillEmpty(second) + "秒";
		} else {
			return fillEmpty(second) + "秒";
		}
	}

	public static String fillEmpty(long time) {
		return time < 10L ? "0" + time : "" + time;
	}

}
