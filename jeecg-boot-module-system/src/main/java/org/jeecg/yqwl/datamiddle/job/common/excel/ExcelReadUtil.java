package org.jeecg.yqwl.datamiddle.job.common.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.yqwl.datamiddle.util.ConstantsUtil;
import org.jeecg.yqwl.datamiddle.util.DateUtils;
import org.jeecg.yqwl.datamiddle.util.FileUtils;
import org.jeecg.yqwl.datamiddle.util.custom.GetterUtil;
import org.jeecgframework.core.util.ApplicationContextUtil;
import org.jeecgframework.dict.service.AutoPoiDictServiceI;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.params.ExcelImportEntity;
import org.jeecgframework.poi.exception.excel.ExcelImportException;
import org.jeecgframework.poi.exception.excel.enums.ExcelImportEnum;
import org.jeecgframework.poi.util.ExcelUtil;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: Excel导入
 * @Author: WangYouzheng
 * @Date: 2021/11/8 14:11
 * @Version: V1.0
 */
@Slf4j
public class ExcelReadUtil {

    /**
     * 读取Excel，默认从第一行开始，读取第一个sheet
     *
     * @param request
     * @param tclass
     * @param <T>
     * @return
     */
    public static <T> List<T> readExcel(HttpServletRequest request, Class<T> tclass) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        params.setTitleRows(0);
        params.setSheetNum(1);

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            try {
                // 放弃jeecg的这个Util无法满足业务场景。 增加业务代码复杂度
                // readExcelData(request);

                return ExcelImportUtil.importExcel(file.getInputStream(), tclass, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 读取excel，同时获取上传文件request中的参数  TODO:
     *
     * @param request
     * @param tclass
     * @param <T>
     * @return
     */
    public static <T> ExcelLoadResult<T> readExcelAndParam(HttpServletRequest request, Class<T> tclass) {
        ExcelLoadResult result = new ExcelLoadResult();
        Map<String, String> paramMap = new HashMap<>();
        result.setParamsMap(paramMap);

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Enumeration<String> parameterNames = multipartRequest.getParameterNames();

        // 读取参数
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            paramMap.put(key, multipartRequest.getParameter(key));
        }

        // 读取Excel
        Iterator<String> itr = multipartRequest.getFileNames();
        MultipartFile multipartFile = null;

        StringBuilder sb = new StringBuilder();
        List<T> fileDataList = new ArrayList<>();
        result.setDataList(fileDataList);

        try {
            // 获取首行数据标题，匹配模板
            if (itr.hasNext()) {
                List<String> titleList = new ArrayList<>();
                multipartFile = multipartRequest.getFile(itr.next());
                // 文件扩展名
                String suffix = FileUtils.getSuffix(multipartFile.getOriginalFilename());
                Workbook workbook = null;
                if (ConstantsUtil.FILE_XLS.equals(suffix)) {
                    workbook = new HSSFWorkbook(multipartFile.getInputStream());
                } else {
                    workbook = new XSSFWorkbook(multipartFile.getInputStream());
                }

                for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                    Sheet sheet = workbook.getSheetAt(numSheet);
                    if (numSheet == 0) {
                        // 第一行 加载表头
                        Row row = sheet.getRow(0);
                        for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
                            String cellVal = getExcelCellValueDateFormat(row.getCell(cellNum)).trim();
                            if (StringUtils.isNotBlank(cellVal)) {
                                sb.append(",").append(cellVal);
                                titleList.add(cellVal);
                            }
                        }

                        // 初始化转换类
                        Map<String, List<ExcelImportEntity>> entityMap = initExcelImportEntity(tclass);
                        // 读取Excel并转储数据
                        fileDataList.addAll(setEntity(sheet, titleList, tclass, entityMap));
                        // setEntity(sheet, titleList, tclass);
                        // fileDataList.addAll();
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


        return result;
    }

    /**
     * 获取class的 包括父类的
     *
     * @param clazz
     * @return
     */
    public static Field[] getClassFields(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        Field[] fields;
        do {
            fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                list.add(fields[i]);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class && clazz != null);
        return list.toArray(fields);
    }

    /**
     * 根据指定的类进行Excel 与 Entity映射转换
     *
     * @param clazz
     * @return
     */
    private static Map<String, List<ExcelImportEntity>> initExcelImportEntity(Class<?> clazz) throws Exception {
        Field[] classFields = getClassFields(clazz);
        Map<String, List<ExcelImportEntity>> excelEntityMap = new HashMap<>();

        // 读取每个参数上的@Excel注解的dict
        for (Field field : classFields) {

            org.jeecgframework.poi.excel.annotation.Excel excel = field.getAnnotation(org.jeecgframework.poi.excel.annotation.Excel.class);
            if (excel == null) {
                continue;
            }
            ExcelImportEntity excelEntity = new ExcelImportEntity();
            excelEntity.setName(excel.name());
            // 获取数据字典替换
            if (StringUtils.isNotEmpty(excel.dicCode())) {
                AutoPoiDictServiceI jeecgDictService = null;
                try {
                    jeecgDictService = ApplicationContextUtil.getContext().getBean(AutoPoiDictServiceI.class);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                if (jeecgDictService != null) {
                    String[] dictReplace = jeecgDictService.queryDict(excel.dictTable(), excel.dicCode(), excel.dicText());
                    if (dictReplace != null && dictReplace.length != 0) {
                        excelEntity.setReplace(dictReplace);
                    }
                }
            }
            // 获取全路径方法名
            Method method = PoiPublicUtil.getMethod(field.getName(), clazz, field.getType(), false);
            excelEntity.setMethod(method);
            // importFormat 格式化日期用
            excelEntity.setFormat(excel.importFormat());

            // 合并要设置的值根据name（通过这个字段映射excel 表头）
            if (excelEntityMap.containsKey(excel.name())) {
                excelEntityMap.get(excel.name()).add(excelEntity);
            } else {
                List<ExcelImportEntity> entityList = new ArrayList<>();
                entityList.add(excelEntity);
                excelEntityMap.put(excel.name(), entityList);
            }
        }

        return excelEntityMap;
    }

    /**
     * 传入普通的HttpServletRequest 进行统一读取，并且读取出所有的request参数
     *
     * @param request
     * @return
     */
    public static ExcelLoadResult readExcelData(HttpServletRequest request) {
        ExcelLoadResult result = new ExcelLoadResult();
        Map<String, String> paramMap = new HashMap<>();
        result.setParamsMap(paramMap);

        /*ShiroHttpServletRequest shiroRequest = (ShiroHttpServletRequest) request;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        MultipartHttpServletRequest multipartRequest = commonsMultipartResolver.resolveMultipart((HttpServletRequest) shiroRequest.getRequest());*/
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Enumeration<String> parameterNames = multipartRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            paramMap.put(key, multipartRequest.getParameter(key));
        }

        Iterator<String> itr = multipartRequest.getFileNames();
        MultipartFile multipartFile = null;

        StringBuilder sb = new StringBuilder();
        List<Map> fileDataList = new ArrayList<>();
        result.setDataList(fileDataList);
        try {
            // 获取首行数据标题，匹配模板
            if (itr.hasNext()) {
                List<String> titleList = new ArrayList<>();
                multipartFile = multipartRequest.getFile(itr.next());
                // 文件扩展名
                String suffix = FileUtils.getSuffix(multipartFile.getOriginalFilename());
                Workbook workbook = null;
                if (ConstantsUtil.FILE_XLS.equals(suffix)) {
                    workbook = new HSSFWorkbook(multipartFile.getInputStream());
                } else {
                    workbook = new XSSFWorkbook(multipartFile.getInputStream());
                }

                for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                    Sheet sheet = workbook.getSheetAt(numSheet);
                    if (numSheet == 0) {
                        // 第一行
                        Row row = sheet.getRow(0);
                        for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
                            String cellVal = getExcelCellValueDateFormat(row.getCell(cellNum)).trim();
                            if (StringUtils.isNotBlank(cellVal)) {
                                sb.append(",").append(cellVal);
                                titleList.add(cellVal);
                            }
                        }
                        // 转储数据
                        fileDataList.addAll(setEntity(sheet, titleList));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 格式化读取Excel cell值
     *
     * @param xssfCell
     * @return
     */
    public static String getExcelCellValueDateFormat(Cell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellType() == CellType.BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else if (xssfCell.getCellType() == CellType.NUMERIC) {
            String cellValue = "";
            if (HSSFDateUtil.isCellDateFormatted(xssfCell)) { // 判断是日期类型
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
                Date dt = HSSFDateUtil.getJavaDate(xssfCell.getNumericCellValue());// 获取成DATE类型
                cellValue = dateformat.format(dt);
            } else {
                HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
                String cellFormatted = dataFormatter.formatCellValue(xssfCell);
                return cellFormatted;
                // xssfCell.setCellType(CellType.STRING);
                // return String.valueOf(xssfCell.getStringCellValue());
            }
            return cellValue;
        } else {
            // 返回字符串类型的值
            xssfCell.setCellType(CellType.STRING);
            return String.valueOf(xssfCell.getStringCellValue());
        }
    }

    /**
     * 根据 @Excel 注解匹配转换
     *
     * @param sheet
     * @param keyList
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> List<T> setEntity(Sheet sheet, List<String> keyList, Class<T> clazz, Map<String, List<ExcelImportEntity>> entityMap) {
        List<T> mapList = new ArrayList();

        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = CellUtil.getRow(rowNum, sheet);
            // Map<String, Object> tmpMap = new HashMap<>();
            T t = null;
            try {
                t = clazz.newInstance();
            } catch (InstantiationException e) {
                log.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
            String cellVal;
            for (int i = 0; i < keyList.size(); i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cellVal = StringUtils.trim(getExcelCellValueDateFormat(cell));

                    if (StringUtils.isNotBlank(cellVal)) {
                        String title = keyList.get(i);
                        List<ExcelImportEntity> excelImportEntities = entityMap.get(title);
                        // 设置赋值。
                        for (ExcelImportEntity excelImportEntity : excelImportEntities) {
                            // TODO:进行字典值 replace的替换。
                            Object o = replaceValue(excelImportEntity.getReplace(), cellVal, true);
                            String paramTypes = excelImportEntity.getMethod().getGenericParameterTypes()[0].toString();
                            // 日期转换
                            if (StringUtils.isNotBlank(excelImportEntity.getFormat())) {
                                if (StringUtils.equals(paramTypes, "class java.lang.Long")) {
                                    o = DateUtils.strToTenLong(GetterUtil.getString(o), excelImportEntity.getFormat());
                                }
                            }
                            // 类型转换
                            try {
                                excelImportEntity.getMethod().invoke(t, getValueByType(paramTypes, o));
                            } catch (IllegalAccessException e) {
                                log.error(e.getMessage(), e);
                            } catch (InvocationTargetException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
            mapList.add(t);
        }
        return mapList;
    }

    /**
     * 根据返回类型获取返回值
     *
     * @param xclass
     * @param result
     * @return
     */
    private static Object getValueByType(String xclass, Object result) {
        try {
            //update-begin-author:scott date:20180711 for:TASK #2950 【bug】excel 导入报错，空指针问题
            if(result==null || "".equals(String.valueOf(result))){
                return null;
            }
            //update-end-author:scott date:20180711 for:TASK #2950 【bug】excel 导入报错，空指针问题
            if ("class java.util.Date".equals(xclass)) {
                return result;
            }
            if ("class java.lang.Boolean".equals(xclass) || "boolean".equals(xclass)) {
                //update-begin-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
                Boolean temp = Boolean.valueOf(String.valueOf(result));
                //if(StringUtils.isNotEmpty(entity.getNumFormat())){
                //	return Boolean.valueOf(new DecimalFormat(entity.getNumFormat()).format(temp));
                //}else{
                return temp;
                //}
                //update-end-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
            }
            if ("class java.lang.Double".equals(xclass) || "double".equals(xclass)) {
                //update-begin-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
                Double temp = Double.valueOf(String.valueOf(result));
                //if(StringUtils.isNotEmpty(entity.getNumFormat())){
                //	return Double.valueOf(new DecimalFormat(entity.getNumFormat()).format(temp));
                //}else{
                return temp;
                //}
                //update-end-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
            }
            if ("class java.lang.Long".equals(xclass) || "long".equals(xclass)) {
                //update-begin-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
                Long temp = Long.valueOf(ExcelUtil.remove0Suffix(String.valueOf(result)));
                //if(StringUtils.isNotEmpty(entity.getNumFormat())){
                //	return Long.valueOf(new DecimalFormat(entity.getNumFormat()).format(temp));
                //}else{
                return temp;
                //}
                //update-end-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
            }
            if ("class java.lang.Float".equals(xclass) || "float".equals(xclass)) {
                //update-begin-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
                Float temp = Float.valueOf(String.valueOf(result));
                //if(StringUtils.isNotEmpty(entity.getNumFormat())){
                //	return Float.valueOf(new DecimalFormat(entity.getNumFormat()).format(temp));
                //}else{
                return temp;
                //}
                //update-end-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
            }
            if ("class java.lang.Integer".equals(xclass) || "int".equals(xclass)) {
                //update-begin-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
                Integer temp = Integer.valueOf(ExcelUtil.remove0Suffix(String.valueOf(result)));
                //if(StringUtils.isNotEmpty(entity.getNumFormat())){
                //	return Integer.valueOf(new DecimalFormat(entity.getNumFormat()).format(temp));
                //}else{
                return temp;
                //}
                //update-end-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
            }
            if ("class java.math.BigDecimal".equals(xclass)) {
                //update-begin-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
                BigDecimal temp = new BigDecimal(String.valueOf(result));
                //if(StringUtils.isNotEmpty(entity.getNumFormat())){
                //	return new BigDecimal(new DecimalFormat(entity.getNumFormat()).format(temp));
                //}else{
                return temp;
                //}
                //update-end-author:taoYan date:20200319 for:Excel注解的numFormat方法似乎未实现 #970
            }
            if ("class java.lang.String".equals(xclass)) {
                // 针对String 类型,但是Excel获取的数据却不是String,比如Double类型,防止科学计数法
                if (result instanceof String) {
                    //---update-begin-----autor:scott------date:20191016-------for:excel导入数字类型，去掉后缀.0------
                    return ExcelUtil.remove0Suffix(result);
                    //---update-end-----autor:scott------date:20191016-------for:excel导入数字类型，去掉后缀.0------
                }
                // double类型防止科学计数法
                if (result instanceof Double) {
                    return PoiPublicUtil.doubleToString((Double) result);
                }
                //---update-begin-----autor:scott------date:20191016-------for:excel导入数字类型，去掉后缀.0------
                return ExcelUtil.remove0Suffix(String.valueOf(result));
                //---update-end-----autor:scott------date:20191016-------for:excel导入数字类型，去掉后缀.0------
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
        }
    }


    /**
     * 导入支持多值替换
     * @param replace 数据库中字典查询出来的数组
     * @param result excel单元格获取的值
     * @param multiReplace 是否支持多值替换
     * @author taoYan
     * @since 2018年8月7日
     */
    private static Object replaceValue(String[] replace, Object result,boolean multiReplace) {
        if(result == null){
            return "";
        }
        if(replace == null || replace.length<=0){
            return result;
        }
        String temp = String.valueOf(result);
        String backValue = "";
        if(temp.indexOf(",")>0 && multiReplace){
            //原值中带有逗号，认为他是多值的
            String multiReplaces[] = temp.split(",");
            for (String str : multiReplaces) {
                backValue = backValue.concat(replaceSingleValue(replace, str)+",");
            }
            if(backValue.equals("")){
                backValue = temp;
            }else{
                backValue = backValue.substring(0, backValue.length()-1);
            }
        }else{
            backValue = replaceSingleValue(replace, temp);
        }
        //update-begin-author:liusq date:20210204 for:字典替换失败提示日志
        if(replace.length>0 && backValue.equals(temp)){
            log.warn("====================字典替换失败,字典值:{},要转换的导入值:{}====================", replace, temp);
        }
        //update-end-author:liusq date:20210204 for:字典替换失败提示日志
        return backValue;
    }

    /**
     * 单值替换 ,若没找到则原值返回
     */
    private static String replaceSingleValue(String[] replace, String temp){
        String[] tempArr;
        for (int i = 0; i < replace.length; i++) {
            tempArr = replace[i].split("_");
            if (temp.equals(tempArr[0])) {
                return tempArr[1];
            }
        }
        return temp;
    }

    private static List<Map<String, Object>> setEntity(Sheet sheet, List<String> keyList) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = CellUtil.getRow(rowNum, sheet);
            Map<String, Object> tmpMap = new HashMap<>();
            String cellVal = getExcelCellValueDateFormat(row.getCell(0));
            //if (StringUtils.isNotEmpty(cellVal)) {
            for (int i = 0; i < keyList.size(); i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cellVal = StringUtils.trim(getExcelCellValueDateFormat(cell));
                    if (StringUtils.isNotBlank(cellVal)) {
                        tmpMap.put(keyList.get(i), cellVal);
                    }
                }
            }
            tmpMap.put("lineNo", rowNum + 1);
            mapList.add(tmpMap);
            //}
        }
        return mapList;
    }

    /**
     * excel 验证替换共通方法
     *
     * @return
     */
    public static String getMessage(String message, String... replaceStr) {
        for (int i = 0; i < replaceStr.length; i++) {
            message = message.replace("{" + i + "}", replaceStr[i]);
        }
        return message;
    }

    public class Error {
        public static final String CHECK_DATA_BAR_NUMBER = "一次上传不能超过{0}条数据";

        public static final String CHECK_DATA_NULL = "文件中没有数据或无法读取数据";

        public static final String CHECK_MESSAGE_FORMAT_ERROR = "第{0}行[{1}]{2} : 不正确";

        public static final String CHECK_MESSAGE_REPEAT_ERROR = "第{0}行[{1}] : 不可重复";

        public static final String CHECK_MESSAGE_NULL_FORMAT_ERROR = "第{0}行[{1}] : 不能为空";

        public static final String CHECK_MESSAGE_NO_LENGTH_IS_NOT_CORRECT = "第{0}行[{1}] : 长度不正确";

        public static final String CHECK_MESSAGE_OUT_FORMAT_ERROR = "第{0}行[{1}] : 不能超过{2}字";

        public static final String CHECK_MESSAGE_DATE_FORMAT_ERROR = "第{0}行[{1}]{2} : 日期格式不正确";

        public static final String CHECK_MESSAGE_DATE_BETWEEN_ERROR = "第{0}行[{1}]{2}[{3}]{4} : 开始日期不能大于结束日期";

        public static final String CHECK_MESSAGE_NUMBER_FORMAT_ERROR = "第{0}行[{1}]{2} : 数字格式不正确";

        public static final String CHECK_MESSAGE_NUMBER_MAX_ERROR = "第{0}行[{1}]{2} : 数字超出允许范围，不得小于0";

        public static final String CHECK_MESSAGE_PHONE_FORMAT_ERROR = "第{0}行[{1}]{2} : 电话格式不正确(格式: 13800001111)";
        //public static final String CHECK_MESSAGE_PHONE_FORMAT_ERROR = "第{0}行[{1}]{2} : 电话格式不正确(格式: 0775-8578774 或 13800001111)";

        public static final String CHECK_MESSAGE_NAME_EXISTS_FORMAT_ERROR = "第{0}行[{1}]{2} : 客户已存在";

        public static final String CHECK_MESSAGE_NAME_EXISTS_FORMAT_ERROR_FILE = "第{0}行[{1}]{2} : 文件中客户重复导入";

        public static final String CHECK_MESSAGE_NOT_EXISTS_FORMAT_ERROR = "第{0}行[{1}]{2} : 不存在";

        public static final String CHECK_MESSAGE_NOT_EXISTS_FILE_FORMAT_ERROR = "第{0}行[{1}]{2} : 不存在本次上传文件中";

        public static final String CHECK_MESSAGE_EMAIL_FORMAT_ERROR = "第{0}行[{1}]{2} : 邮箱格式不正确";

        public static final String CHECK_MESSAGE_NUMBER_NEGATIVE_ERROR = "第{0}行[{1}]{2} : 数字不能为负数";

        public static final String CHECK_MESSAGE_CUSTOMER_ERROR = "第{0}行[{1}] : {2}";

        public static final String CHECK_MESSAGE_DUPLICATE_ERROR = "表格信息在第{0}行中有重复！";

        public static final String CHECK_EXCEL_READ_NULL = "未读取到数据";

        public static final String CHECK_EXCEL_READ_ERROR = "系统读取失败，请重新校验表格格式，若格式正常请联系管理员处理";
    }
}
