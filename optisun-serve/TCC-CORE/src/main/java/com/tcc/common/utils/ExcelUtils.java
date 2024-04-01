package com.tcc.common.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel工具类
 */
public class ExcelUtils {
    /**
     * Excel表格导出
     * @param response HttpServletResponse对象
     * @param excelData Excel表格的数据，封装为List<List<String>>
     * @param sheetName sheet的名字
     * @param fileName 导出Excel的文件名
     * @param columnWidth Excel表格的宽度，建议为15
     * @throws IOException 抛IO异常
     */

    public static void exportExcel(HttpServletResponse response,
                                   List<List<String>> excelData,
                                   String sheetName,
                                   String fileName,
                                   int columnWidth) throws IOException {

        //声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();

        //生成一个表格，设置表格名称
        HSSFSheet sheet = workbook.createSheet(sheetName);

        //设置表格列宽度
        sheet.setDefaultColumnWidth(columnWidth);

        //写入List<List<String>>中的数据
        int rowIndex = 0;
        for(List<String> data : excelData){
            //创建一个row行，然后自增1
            HSSFRow row = sheet.createRow(rowIndex++);

            //遍历添加本行数据
            for (int i = 0; i < data.size(); i++) {
                //创建一个单元格
                HSSFCell cell = row.createCell(i);

                //创建一个内容对象
                HSSFRichTextString text = new HSSFRichTextString(data.get(i));

                //将内容对象的文字内容写入到单元格中
                cell.setCellValue(text);
            }
        }

        //准备将Excel的输出流通过response输出到页面下载
        //八进制输出流
        response.setContentType("application/octet-stream");

        //设置导出Excel的名称
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        //刷新缓冲
        response.flushBuffer();

        //workbook将Excel写入到response的输出流中，供页面下载该Excel文件
        workbook.write(response.getOutputStream());
        //关闭workbook
        workbook.close();
    }
    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith("xls")){
                //2003
                POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);
                workbook = new HSSFWorkbook(poifsFileSystem);
                sheet = workbook.getSheetAt(0);
            }else if(fileName.endsWith("xlsx")){
                //2007 及2007以上
                workbook = new XSSFWorkbook(is);
                sheet = workbook.getSheetAt(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
    public static String getValue(Cell cell){
        if(cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.BOOLEAN){
            return String.valueOf(cell.getBooleanCellValue());
        }else if(cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.NUMERIC){
            String value = "";
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date d = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                value = sdf.format(d);
            }else{
                DecimalFormat format = new DecimalFormat("#");
                double num = cell.getNumericCellValue();
                value = format.format(num);
            }
            return value;
        }else if (cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING){
            return String.valueOf(cell.getStringCellValue());
        }else{
            return String.valueOf(cell.getStringCellValue());
        }
    }
    public static boolean isEmptyRow(Row row) {
        if (row == null || row.toString().isEmpty()) {
            return true;
        } else {
            Iterator<Cell> it = row.iterator();
            boolean isEmpty = true;
            while (it.hasNext()) {
                Cell cell = it.next();
                if (cell.getCellTypeEnum() != CellType.BLANK) {
                    isEmpty = false;
                    break;
                }
            }
            return isEmpty;
        }
    }
    /**
     * @param fileName 文件名称
     * @param headers 表头
     * @param dataset 数据集
     * @param isSortDataSet 是否对数据排序
     * @param response HttpServletResponse
     * @param mergeBasis 合并基准列 可选
     * @param mergeCells 要合并的列 可选
     * @param sumCells 要求和的列 可选
     * @param timeCells 时间列 可选
     * @throws IOException
     */
    public static void exportExelMerge(String fileName,final String[] headers,List<String[]> dataset,boolean isSortDataSet,HttpServletResponse response, final Integer[] mergeBasis, final Integer[] mergeCells, final Integer[] sumCells, final Integer[] timeCells) throws IOException{
        String title = "Sheet1";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));

        createExcelMerge(title,headers,dataset,isSortDataSet,response.getOutputStream(),mergeBasis,mergeCells,sumCells,timeCells);

        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    /**
     * @param title 文件名称
     * @param headers 表头
     * @param dataset 数据集
     * @param isSortDataSet 是否对数据排序
     * @param out OutputStream
     * @param mergeBasis 合并基准列 可选
     * @param mergeCells 要合并的列
     * @param sumCells 要求和的列
     * @param timeCells 时间列 可选
     */
    public static void createExcelMerge(String title, final String[] headers, List<String[]> dataset, boolean isSortDataSet, OutputStream out, final Integer[] mergeBasis, final Integer[] mergeCells, final Integer[] sumCells, final Integer[] timeCells){
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(title);

        sheet.setDefaultColumnWidth(25); // 设置表格默认列宽度为15个字节

        HSSFCellStyle headStyle = createHeadStyle(workbook); // 生成头部样式
        HSSFCellStyle commonDataStyle = createCommonDataStyle(workbook); // 生成一般数据样式
        HSSFCellStyle numStyle = createNumStyle(workbook); //生成数字类型保留两位小数样式
        HSSFCellStyle sumRowStyle = createSumRowStyle(workbook); //生成合计行样式

        if(headers == null || headers.length <= 0){
            return;
        }

        HSSFRow row = sheet.createRow(0); // 产生表格标题行
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        if(isSortDataSet && mergeBasis != null && mergeBasis.length > 0){ //是否排序数据
            Collections.sort(dataset, new Comparator<String[]>() {
                public int compare(String[] o1, String[] o2) {
                    String s1 = "";
                    String s2 = "";
                    for(int i = 0 ; i < mergeBasis.length ; i++){
                        s1+=(o1[mergeBasis[i].intValue()]+Character.valueOf((char)127).toString());
                        s2+=(o2[mergeBasis[i].intValue()]+Character.valueOf((char)127).toString());
                    }
                    if(timeCells != null && timeCells.length > 0){
                        for(int i = 0 ; i < timeCells.length ; i++){
                            s1+= o1[timeCells[i].intValue()];
                            s2+= o2[timeCells[i].intValue()];
                        }
                    }
                    if(s1.compareTo(s2) < 0){
                        return -1;
                    }else if(s1.compareTo(s2) == 0){
                        return 0;
                    }else{
                        return 1;
                    }
                }
            });
        }
        // 遍历集合数据，产生数据行
        Iterator<String[]> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            String[] dataSources = it.next() ;
            for (int i = 0; i < dataSources.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(commonDataStyle);
                cell.setCellValue(dataSources[i]);
            }
        }
        try {
            if(mergeBasis != null && mergeBasis.length > 0 && mergeCells != null && mergeCells.length > 0){
                for(int i = 0 ; i < mergeCells.length ; i++){
                    mergedRegion(sheet,mergeCells[i],1,sheet.getLastRowNum(),workbook,mergeBasis);
                }
            }
            if(sumCells != null && sumCells.length > 0){
                createSumRow(sheet, row, headers, sumCells, sumRowStyle, numStyle);
            }
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建合计行
     * @param sheet
     * @param row
     * @param headers
     * @param sumCells
     * @param sumRowStyle
     * @param numStyle
     */
    private static void createSumRow(HSSFSheet sheet , HSSFRow row , final String[] headers, final Integer[] sumCells , HSSFCellStyle sumRowStyle,HSSFCellStyle numStyle){
        row=sheet.createRow(sheet.getLastRowNum()+1);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(sumRowStyle);
        }
        for(int i = 1 ; i < sheet.getLastRowNum() ; i++){
            for(int j = 0 ; j < sumCells.length ; j++){
                sheet.getRow(i).getCell(sumCells[j]).setCellValue(Double.parseDouble(sheet.getRow(i).getCell(sumCells[j]).getStringCellValue()));
                sheet.getRow(i).getCell(sumCells[j]).setCellStyle(numStyle);
            }
        }
        HSSFCell sumCell = row.getCell(0);
        sumCell.setCellValue("合计：");
        String sumFunctionStr = null;
        for(int i = 0 ; i < sumCells.length ; i++){
            sumFunctionStr = "SUM("+ CellReference.convertNumToColString(sumCells[i])+"2:"+CellReference.convertNumToColString(sumCells[i])+sheet.getLastRowNum()+")";
            row.getCell(sumCells[i]).setCellFormula(sumFunctionStr);
        }
    }

    /**
     * 合并单元格
     * @param sheet
     * @param cellLine
     * @param startRow
     * @param endRow
     * @param workbook
     * @param mergeBasis
     */
    private static void mergedRegion(HSSFSheet sheet, int cellLine,int startRow, int endRow, HSSFWorkbook workbook, Integer[] mergeBasis) {
        HSSFCellStyle style = workbook.createCellStyle();           // 样式对象
        style.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直
        style.setAlignment(HorizontalAlignment.CENTER);             // 水平
        String s_will="";
        for(int y = 0 ; y< mergeBasis.length ; y++){
            s_will+= sheet.getRow(startRow).getCell(mergeBasis[y]).getStringCellValue();
        }
        int count = 0;
        Set<Integer> set = new HashSet<Integer>();
        CollectionUtils.addAll(set, mergeBasis);
        for (int i = 2; i <= endRow; i++) {

            String s_current="";
            for(int y = 0 ; y< mergeBasis.length ; y++){
                s_current+= sheet.getRow(i).getCell(mergeBasis[y]).getStringCellValue();
            }
            if (s_will.equals(s_current)) {
                boolean isMerge = true;
                for(int j = 0 ; j < mergeBasis.length ; j++){
                    if(!sheet.getRow(i).getCell(mergeBasis[j]).getStringCellValue()
                            .equals(sheet.getRow(i-1).getCell(mergeBasis[j]).getStringCellValue())){
                        isMerge = false;
                    }
                }
                if(isMerge){
                    count++;
                }else{
                    sheet.addMergedRegion(new CellRangeAddress( startRow, startRow+count,cellLine , cellLine));
                    startRow = i;
                    s_will = s_current;
                    count = 0;
                }
            }
            else {
                if(count>0){
                    sheet.addMergedRegion(new CellRangeAddress(startRow,startRow+count ,cellLine , cellLine));
                }
                startRow = i;
                s_will = s_current;
                count = 0;
            }
            if (i == endRow && count > 0) {
                sheet.addMergedRegion(new CellRangeAddress(startRow,startRow+count ,cellLine , cellLine));
            }
        }
    }

    /**
     * 标题单元格样式
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createHeadStyle(HSSFWorkbook workbook){
        //标题单元格样式
        HSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);
        headStyle.setBorderTop(BorderStyle.THIN);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        //标题单元格字体
        HSSFFont headFont = workbook.createFont();
        headFont.setColor(HSSFColor.VIOLET.index);
        headFont.setFontHeightInPoints((short) 12);
        headFont.setBold(true);
        // 把字体应用到当前的样式
        headStyle.setFont(headFont);
        return headStyle;
    }

    /**
     * 合计行单元格样式
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createSumRowStyle(HSSFWorkbook workbook){
        //合计行单元格样式
        HSSFCellStyle sumRowStyle = workbook.createCellStyle();
        sumRowStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        sumRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sumRowStyle.setBorderBottom(BorderStyle.THIN);
        sumRowStyle.setBorderLeft(BorderStyle.THIN);
        sumRowStyle.setBorderRight(BorderStyle.THIN);
        sumRowStyle.setBorderTop(BorderStyle.THIN);
        sumRowStyle.setAlignment(HorizontalAlignment.CENTER);
        sumRowStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        //合计行单元格字体
        HSSFFont sumRowFont = workbook.createFont();
        sumRowFont.setColor(HSSFColor.VIOLET.index);
        sumRowFont.setFontHeightInPoints((short) 12);
        sumRowFont.setBold(true);
        // 把字体应用到当前的样式
        sumRowStyle.setFont(sumRowFont);
        return sumRowStyle;
    }

    /**
     * 普通数据单元格样式
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createCommonDataStyle(HSSFWorkbook workbook){
        //普通数据单元格样式
        HSSFCellStyle commonDataStyle = workbook.createCellStyle();
        commonDataStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        commonDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        commonDataStyle.setBorderBottom(BorderStyle.THIN);
        commonDataStyle.setBorderLeft(BorderStyle.THIN);
        commonDataStyle.setBorderRight(BorderStyle.THIN);
        commonDataStyle.setBorderTop(BorderStyle.THIN);
        commonDataStyle.setAlignment(HorizontalAlignment.CENTER);
        commonDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //普通数据单元格字体
        HSSFFont commonDataFont = workbook.createFont();
        commonDataFont.setBold(false);
        //把字体应用到当前的样式
        commonDataStyle.setFont(commonDataFont);
        return commonDataStyle;
    }

    /**
     * 自定义保留两位小数数字单元格格式
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createNumStyle(HSSFWorkbook workbook){
        //自定义保留两位小数数字单元格格式
        HSSFCellStyle numStyle = workbook.createCellStyle();
        numStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        numStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        numStyle.setBorderBottom(BorderStyle.THIN);
        numStyle.setBorderLeft(BorderStyle.THIN);
        numStyle.setBorderRight(BorderStyle.THIN);
        numStyle.setBorderTop(BorderStyle.THIN);
        numStyle.setAlignment(HorizontalAlignment.CENTER);
        numStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        //自定义保留两位小数数字单元格字体
        HSSFFont numFont = workbook.createFont();
        numFont.setBold(false);
        //把字体应用到当前的样式
        numStyle.setFont(numFont);
        return numStyle;
    }
    public static void main(String[] args) {
        String[] headers =  { "大区", "部门", "金额", "数量", "日期" };
        List<String[]> dataset = new ArrayList<String[]>();
        dataset.add(new String[] {"华东", "部门3", "35", "1", "2015-01-01"});
        dataset.add(new String[] {"华北", "部门1", "20", "1", "2015-01-02"});
        dataset.add(new String[] {"华北", "部门2", "25", "1", "2015-01-03"});
        dataset.add(new String[] {"华北", "部门5", "25", "1", "2015-01-04"});
        dataset.add(new String[] {"华南", "部门1", "15", "1", "2015-01-05"});
        dataset.add(new String[] {"华北", "部门3", "30", "1", "2015-01-06"});
        dataset.add(new String[] {"华北", "部门3", "30", "1", "2015-01-07"});
        dataset.add(new String[] {"华东", "部门1", "25", "1", "2015-01-08"});
        dataset.add(new String[] {"华南", "部门4", "30", "1", "2015-01-09"});
        dataset.add(new String[] {"华东", "部门2", "25", "1", "2015-01-10"});
        dataset.add(new String[] {"华东", "部门2", "25", "1", "2015-01-11"});
        dataset.add(new String[] {"华东", "部门3", "35", "1", "2015-01-12"});
        dataset.add(new String[] {"华南", "部门1", "15", "1", "2015-01-13"});
        dataset.add(new String[] {"华北", "部门6", "20", "1", "2015-01-14"});
        dataset.add(new String[] {"华南", "部门2", "25", "1", "2015-01-15"});
        dataset.add(new String[] {"华南", "部门2", "25", "1", "2015-01-16"});
        dataset.add(new String[] {"华东", "部门1", "25", "1", "2015-01-17"});
        dataset.add(new String[] {"华南", "部门8", "30", "1", "2015-01-18"});
        dataset.add(new String[] {"华东", "部门2", "35", "1", "2015-01-01"});
        dataset.add(new String[] {"华北", "部门1", "20", "1", "2015-01-02"});
        dataset.add(new String[] {"华北", "部门11", "25", "1", "2015-01-03"});
        dataset.add(new String[] {"华北", "部门2", "25", "1", "2015-01-04"});
        dataset.add(new String[] {"华南", "部门1", "15", "1", "2015-01-05"});
        dataset.add(new String[] {"华北", "部门4", "30", "1", "2015-01-06"});
        dataset.add(new String[] {"华北", "部门3", "30", "1", "2015-01-07"});
        dataset.add(new String[] {"华东", "部门9", "25", "1", "2015-01-08"});
        dataset.add(new String[] {"华南", "部门3", "30", "1", "2015-01-09"});
        dataset.add(new String[] {"华东", "部门12", "25", "1", "2015-01-10"});
        dataset.add(new String[] {"华东", "部门2", "25", "1", "2015-01-11"});
        dataset.add(new String[] {"华东", "部门12", "35", "1", "2015-01-12"});
        dataset.add(new String[] {"华南", "部门1", "15", "1", "2015-01-13"});
        dataset.add(new String[] {"华北", "部门11", "20", "1", "2015-01-14"});
        dataset.add(new String[] {"华南", "部门21", "25", "1", "2015-01-15"});
        dataset.add(new String[] {"华南", "部门2", "25", "1", "2015-01-16"});
        dataset.add(new String[] {"华东", "部门15", "25", "1", "2015-01-17"});
        dataset.add(new String[] {"华南", "部门3", "30", "1", "2015-01-18"});
        dataset.add(new String[] {"华东", "部门3", "35", "1", "2015-01-01"});
        dataset.add(new String[] {"华北", "部门17", "20", "1", "2015-01-02"});
        dataset.add(new String[] {"华北", "部门22", "25", "1", "2015-01-03"});
        dataset.add(new String[] {"华北", "部门2", "25", "1", "2015-01-04"});
        dataset.add(new String[] {"华南", "部门1", "15", "1", "2015-01-05"});
        dataset.add(new String[] {"华北", "部门2", "30", "1", "2015-01-06"});
        dataset.add(new String[] {"华北", "部门3", "30", "1", "2015-01-07"});
        dataset.add(new String[] {"华东", "部门1", "25", "1", "2015-01-08"});
        dataset.add(new String[] {"华南", "部门8", "30", "1", "2015-01-09"});
        dataset.add(new String[] {"华东", "部门2", "25", "1", "2015-01-10"});
        dataset.add(new String[] {"华东", "部门2", "25", "1", "2015-01-11"});
        dataset.add(new String[] {"华东", "部门3", "35", "1", "2015-01-12"});
        dataset.add(new String[] {"华南", "部门7", "15", "1", "2015-01-13"});
        dataset.add(new String[] {"华北", "部门1", "20", "1", "2015-01-14"});
        dataset.add(new String[] {"华南", "部门2", "25", "1", "2015-01-15"});
        dataset.add(new String[] {"华南", "部门2", "25", "1", "2015-01-16"});
        dataset.add(new String[] {"华东", "部门9", "25", "1", "2015-01-17"});
        dataset.add(new String[] {"华南", "部门3", "30", "1", "2015-01-18"});
        try {
            OutputStream out = new FileOutputStream("E://a.xls");
            ExcelUtils.createExcelMerge("测试.xls", headers, dataset, true, out, new Integer[]{0,1}, new Integer[]{0}, new Integer[]{2,3}, new Integer[]{4});
            out.close();
            JOptionPane.showMessageDialog(null, "导出成功!");
            System.out.println("excel导出成功！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
