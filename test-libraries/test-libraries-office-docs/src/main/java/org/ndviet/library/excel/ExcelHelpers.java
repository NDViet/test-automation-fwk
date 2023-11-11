package org.ndviet.library.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExcelHelpers {
    public static List getNameOfSheets(String filePath) throws Exception {
        Workbook workbook = new XSSFWorkbook(new File(filePath));
        List<String> listSheets = new ArrayList<>();
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            listSheets.add(workbook.getSheetName(i));
        }
        return listSheets;
    }

    public static LinkedHashMap getMapValuesBySheetName(String filePath) throws Exception {
        List<String> sheets = getNameOfSheets(filePath);
        System.out.println(sheets);
        return getMapValuesBySheetName(filePath, sheets.get(0));
    }

    public static LinkedHashMap getMapValuesBySheetName(String filePath, String sheetName) throws Exception {
        Workbook workbook = new XSSFWorkbook(new File(filePath));
        Sheet sheet = workbook.getSheet(sheetName);
        LinkedHashMap<String, List<String>> sheet_map = new LinkedHashMap<>();
        Row headers = sheet.getRow(0);
        int numberOfColumns = headers.getPhysicalNumberOfCells();
        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
            String headerName = null;
            List<String> listValues = new ArrayList<>();
            for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    if (rowIndex > 0) {
                        listValues.add(getCellValue(cell));
                    } else {
                        headerName = getCellValue(cell);
                    }
                }
            }
            sheet_map.put(headerName, listValues);
        }
        return sheet_map;
    }

    public static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue() + "";
            case BOOLEAN:
                return cell.getBooleanCellValue() + "";
            case ERROR:
                return cell.getErrorCellValue() + "";
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
