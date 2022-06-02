package com.yqwl.datamiddle.realtime.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author: WangYouzheng
 * @Date: 2019/4/22 16 37
 * @Description: 基本运算。
 */
@Slf4j
public final class BigDecimalUtil {
	private static final int ZERO = 0;

	/**
	 * 比较相等
	 *
	 * @param decimal
	 * @param decimal2
	 * @return
	 */
	public static boolean eq(BigDecimal decimal, BigDecimal decimal2) {
		return decimal.compareTo(decimal2) == ZERO;
	}

	/**
	 * 如果第一个值大于第二个值 返回true
	 * decimal > deciaml2 = true
	 *
	 * @param decimal
	 * @param decimal2
	 * @return
	 */
	public static boolean isBigger(BigDecimal decimal, BigDecimal decimal2) {
		return decimal.compareTo(decimal2) > ZERO;
	}

	/**
	 * 是正数
	 *
	 * @param decimal
	 * @return
	 */
	public static boolean isPositive(BigDecimal decimal) {
		BigDecimal bigDecimal = BigDecimalUtil.getBigDecimal(decimal);
		return bigDecimal.signum() == 1;
	}

	/**
	 * 是正数或0
	 *
	 * @return
	 */
	public static boolean isPositiveZero(BigDecimal decimal) {
		BigDecimal bigDecimal = BigDecimalUtil.getBigDecimal(decimal);
		return (bigDecimal.signum() == 0 || bigDecimal.signum() == 1);
	}

	/**
	 * 是正数或0
	 *
	 * @return
	 */
	public static boolean isPositiveZero(String decimal) {
		BigDecimal bigDecimal = BigDecimalUtil.getBigDecimal(decimal);
		return (bigDecimal.signum() == 0 || bigDecimal.signum() == 1);
	}

	/**
	 * 是负数 返回true
	 *
	 * @param decimal
	 * @return
	 */
	public static boolean isNegative(String decimal) {
		BigDecimal bigDecimal = BigDecimalUtil.getBigDecimal(decimal);
		return bigDecimal.signum() == -1;
	}

	/**
	 * 是负数 返回true
	 *
	 * @param decimal
	 * @return
	 */
	public static boolean isNegative(BigDecimal decimal) {
		decimal = BigDecimalUtil.getBigDecimal(decimal);
		return decimal.signum() == -1;
	}

	/**
	 * 判断这一批数据中是否有大于0的，如果有返回true
	 *
	 * @param bigDecimals
	 * @return
	 */
	public static boolean isBigger0(BigDecimal... bigDecimals) {
		boolean result = false;
		for (BigDecimal bigDecimal : bigDecimals) {
			boolean bigger = BigDecimalUtil.isBigger(bigDecimal, BigDecimal.ZERO);
			if (bigger) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 是否这一系列的参数列表都等于0
	 *
	 * @param bigDecimals
	 * @return
	 */
	public static boolean isEquals0(BigDecimal... bigDecimals) {
		boolean result = false;
		for (BigDecimal bigDecimal : bigDecimals) {
			boolean eq = BigDecimalUtil.eq(bigDecimal, BigDecimal.ZERO);
			if (!eq) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 如果第一个值小于第二个值 返回true
	 * decimal > deciaml2 = true
	 *
	 * @param decimal
	 * @param decimal2
	 * @return
	 */
	public static boolean isSmaller(BigDecimal decimal, BigDecimal decimal2) {
		return decimal.compareTo(decimal2) < ZERO;
	}

	/**
	 * 加和
	 *
	 * @param decimal
	 * @param decimal2
	 * @return
	 */
	public static BigDecimal add(BigDecimal decimal, BigDecimal decimal2) {
		BigDecimal rst = BigDecimal.ZERO;
		rst = BigDecimalUtil.getBigDecimal(decimal).add(BigDecimalUtil.getBigDecimal(decimal2));
		return rst;
	}

	/**
	 * 第一个数加第二个数，改变第一个数
	 * version2: 。。extends number 值传递。 除非wrapper或许可操作，否则无法实现既定效果~~
	 *
	 * @param decimal
	 * @param decimals
	 */
	public static BigDecimal addTo(BigDecimal decimal, BigDecimal... decimals) {
		BigDecimal decimal2 = BigDecimal.ZERO;
		if (decimals != null && decimals.length > 0) {
			for (int i = 0; i < decimals.length; i++) {
				decimal2 = decimals[i];
				decimal = BigDecimalUtil.add(decimal, decimal2);
			}
		}

		return decimal;
	}

	/**
	 * 第一个数加第二个数，改变第一个数
	 * version2: 。。extends number 值传递。 除非wrapper或许可操作，否则无法实现既定效果~~
	 *
	 * @param decimal
	 * @param decimals
	 */
	public static BigDecimal addTo(String decimal, String... decimals) {
		BigDecimal decimal2 = BigDecimal.ZERO;
		BigDecimal result = BigDecimal.ZERO;
		if (decimals != null && decimals.length > 0) {
			for (int i = 0; i < decimals.length; i++) {
				decimal2 = BigDecimalUtil.getBigDecimal(decimals[i]);
				result = BigDecimalUtil.add(result, decimal2);
			}
		}
		result = BigDecimalUtil.add(result, BigDecimalUtil.getBigDecimal(decimal));

		return result;
	}
/*

	public static void addTo(BigDecimal[] decimal) {
		BigDecimal bigDecimal = decimal[0];
		BigDecimalUtil.add(bigDecimal, new BigDecimal("1"));
	}
*/

	/**
	 * 10位小数除法
	 *
	 * @param dividend
	 * @param divisor
	 * @return
	 */
	public static BigDecimal divideWith10Point(BigDecimal dividend, BigDecimal divisor) {
		return BigDecimalUtil.divide(dividend, divisor, 10);
	}

	/**
	 * 两位小数点除法
	 * divident / divisor 四舍五入保留两位
	 *
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @return
	 */
	public static BigDecimal divideWith2Point(BigDecimal dividend, BigDecimal divisor) {
		return BigDecimalUtil.divide(dividend, divisor, 2);
	}

	/**
	 * 两位小数点 除法
	 *
	 * @param dividend
	 * @param divisor
	 * @return
	 */
	public static BigDecimal divideWith2Point(Long dividend, Long divisor) {
		return BigDecimalUtil.divideWith2Point(new BigDecimal(dividend), new BigDecimal(divisor));
	}

	public static BigDecimal divideWith2PointPercent(Long dividend, Long divisor) {
		return BigDecimalUtil.multiply(BigDecimalUtil.divide(new BigDecimal(dividend), new BigDecimal(divisor), 4), new BigDecimal(100));
	}

	public static BigDecimal divideWith2PointPercent(BigDecimal dividend, BigDecimal divisor) {
		return BigDecimalUtil.multiply(BigDecimalUtil.divide(dividend, divisor, 4), new BigDecimal(100));
	}

	/**
	 * 四舍五入除法
	 *
	 * @param dividend
	 * @param divisor
	 * @param scale
	 * @return
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
		BigDecimal rst = BigDecimal.ZERO.setScale(scale);

		try {
			rst = dividend.divide(divisor, scale, RoundingMode.HALF_UP);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return rst;
	}

	/**
	 * 除法
	 *
	 * @param dividend
	 * @param divisor
	 * @param scale
	 * @param roundingMode
	 * @return
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
		BigDecimal rst = BigDecimal.ZERO;
		rst = dividend.divide(divisor, scale, roundingMode);
		return rst;
	}

	/**
	 * 乘法 multiplicand * multiplier
	 *
	 * @param multiplicand
	 * @param multiplier
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal multiplicand, BigDecimal multiplier) {
		BigDecimal rst = BigDecimal.ZERO;

		rst = multiplicand.multiply(multiplier);
		return rst;
	}
	/*
	 *//**
	 * 乘法 保留十位 减少偏差
	 *
	 * @param multiplicand
	 * @param multiplier
	 * @return
	 *//*
	public static BigDecimal multiplyWith10Point(BigDecimal multiplicand, BigDecimal multiplier) {
		BigDecimal rst = BigDecimal.ZERO;

		rst = multiplicand.multiply(multiplier);
		return rst;
	}*/

	/**
	 * 乘法 连乘，传入空 返回 0
	 *
	 * @param multipliers
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal... multipliers) {
		BigDecimal rst = BigDecimal.ONE;

		BigDecimal multiplier = null;

		if (multipliers != null && multipliers.length > 0) {
			for (int i = 0; i < multipliers.length; i++) {
				multiplier = multipliers[i];
				rst = rst.multiply(multiplier);
			}
		} else {
			rst = BigDecimal.ZERO;
		}

		return rst;
	}

	/**
	 * 减法
	 *
	 * @param subtracted
	 * @param subtrahend
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal subtracted, BigDecimal subtrahend) {
		BigDecimal rst = BigDecimal.ZERO;

		rst = subtracted.subtract(subtrahend);

		return rst;
	}

	/**
	 * 使用 0 减
	 *
	 * @param subtrahend
	 * @return
	 */
	public static BigDecimal subStractedByZero(BigDecimal subtrahend) {
		BigDecimal rst = BigDecimal.ZERO;

		BigDecimal subtracted = BigDecimal.ZERO;
		rst = BigDecimalUtil.subtract(subtracted, subtrahend);

		return rst;
	}

	/**
	 * 如果是空返回0 除法慎用~
	 *
	 * @param bigDecimal
	 * @return
	 */
	public static BigDecimal getBigDecimal(BigDecimal bigDecimal) {
		if (BigDecimalUtil.isNotNull(bigDecimal)) {
			return bigDecimal;
		} else {
			return BigDecimal.ZERO;
		}
	}



	public static BigDecimal getBigDecimal(Object obj) {
		try {
			return new BigDecimal(obj.toString());
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 负数会转成0
	 * @param obj
	 * @return
	 */
	public static BigDecimal getBigDecimalMinusToZero(Object obj) {
		BigDecimal bigDecimal = getBigDecimal(obj);
		if (isNegative(bigDecimal)) {
			return BigDecimal.ZERO;
		} else {
			return bigDecimal;
		}
	}

	/**
	 * 取得BigDecimal
	 *
	 * @param obj
	 * @return
	 */
	public static BigDecimal getBigDecimal(Object obj, int scale) {
		if (ObjectUtil.isNull(obj)) {
			return new BigDecimal(0);
		}

		try {
			return new BigDecimal(obj.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP);
		} catch (Exception e) {
			return new BigDecimal(0);
		}
	}

	/**
	 * 判空~~
	 *
	 * @param bigDecimal
	 * @return
	 */
	public static boolean isNotNull(BigDecimal bigDecimal) {
		return bigDecimal != null;
	}

	/**
	 * 判断参数是否有效
	 * <pre>
	 * 0 与 null 返回false  大于0的数视为有效. 返回true
	 * </pre>
	 * <pre>
	 * BigDecimalUtil.isValid(BigDecimal.ZERO) = false
	 * BigDecimalUtil.isValid(null) = false
	 * BigDecimalUtil.isValid(new BigDecimal("0.00")) = false
	 * </pre>
	 *
	 * @param bigDecimal
	 * @return
	 */
	public static boolean isValid(BigDecimal bigDecimal) {
		boolean result = false;
		try {
			if (BigDecimalUtil.isNotNull(bigDecimal) && bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
				result = true;
			}
		} catch (Exception e) {

		} finally {
			return result;
		}
	}

	/**
	 * 获取数字的百分比字符串
	 *
	 * @param bigDecimal
	 * @return
	 */
	public static String getBigDecimalPercentStr(BigDecimal bigDecimal) {
		return BigDecimalUtil.getBigDecimal(bigDecimal) + "%";
	}

	public static String getBigDecimalPercentStrWithScale(BigDecimal bigDecimal, int scale) {
		return BigDecimalUtil.getBigDecimal(bigDecimal, scale) + "%";
	}

	/**
	 * TODO:
	 *
	 * @param toConvert
	 * @return
	 */
	public static BigDecimal setScale(BigDecimal toConvert) {
		BigDecimal bigDecimal = new BigDecimal("");
		return bigDecimal;
	}

	public static void main(String[] args) {
		/*System.out.println(BigDecimalUtil.isValid(new BigDecimal("0.00")));
		System.out.println(BigDecimalUtil.isValid(BigDecimal.ZERO));
		System.out.println(BigDecimalUtil.isValid(null));
		System.out.println(BigDecimalUtil.isValid(new BigDecimal("0.0000001")));*/

		/*BigDecimal add1 = new BigDecimal("123.555");
		System.out.println(add1);
		add1 = add1.setScale(2, RoundingMode.DOWN);
		System.out.println(add1);*/


		/*BigDecimal a = new BigDecimal("3");
		BigDecimal b = new BigDecimal("2");
		System.out.println(a.divideAndRemainder(b));*/
/*
		BigDecimal add12 = new BigDecimal("1");
		BigDecimalUtil.addTo(add1, add12, add12);
		System.out.println(add1);*/
		/*final BigDecimal[] arr= {add1};
		System.out.println(arr[0]);
		BigDecimalUtil.addTo(arr);
		System.out.println(arr[0]);*/

		// System.out.println(getBigDecimalPercentStr(new BigDecimal("123")));
		/*String ss1 = "00:是,办理分期 \n" + "                                    01:是,办理减免 \n" + "                                    02:是,办理减免+分期 \n" + "                                    03:是,办理延期还款 \n"
				+ "                                    04:是,办理停息挂账 \n" + "                                    05:否,未成功与客户取得联系 \n" + "                                    07:否,客户无法提供证明 \n"
				+ "                                    08:否,客户不满分期数 \n" + "                                    09:否,客户要求减免后分期 \n" + "                                    10:否,客户拒绝与第三方机构沟通 \n"
				+ "                                    11:否,客户已办理分期要求再分期 \n" + "                                    12:否,催收员政策内不同意";
		String s = "00:0 \n 01:1~12 \n 02:13~24 \n 03:25~36 \n04:37~48 \n05:49~60 \n06:60以上";

		String[] split = StringUtils.split(s, "\n");
		for (String s1 : split) {
			String trim = StringUtils.trim(s1);
			String[] split1 = trim.split(":");
			String k = split1[0];
			String v = split1[1];
			System.out.println("case \"" + k + "\" : rst = \"" + v + "\";break;");
		}*/

		String ss1 = "";

		System.out.println(divideWith2Point(0L, 0L));
	}

	/**
	 * 取得BigDecimal
	 *
	 * @param obj
	 * @return
	 */
	public static BigDecimal getBigDecimalByRoundDown(Object obj, int scale) {
		if (ObjectUtil.isNull(obj)) {
			return new BigDecimal(0);
		}

		try {
			return new BigDecimal(obj.toString()).setScale(scale, BigDecimal.ROUND_DOWN);
		} catch (Exception e) {
			return new BigDecimal(0);
		}
	}

}
