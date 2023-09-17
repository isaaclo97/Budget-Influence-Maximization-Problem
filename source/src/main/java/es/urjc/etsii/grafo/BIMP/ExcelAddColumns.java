package es.urjc.etsii.grafo.BIMP;


import es.urjc.etsii.grafo.BIMP.model.BIMPInstance;
import es.urjc.etsii.grafo.BIMP.model.BIMPSolution;
import es.urjc.etsii.grafo.io.serializers.excel.ExcelCustomizer;
import es.urjc.etsii.grafo.solver.services.events.AbstractEventStorage;
import es.urjc.etsii.grafo.solver.services.events.types.SolutionGeneratedEvent;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelAddColumns extends ExcelCustomizer {

    @Override
    public void customize(XSSFWorkbook excelBook, AbstractEventStorage eventStorage) {
        try {
            XSSFSheet sheet = excelBook.createSheet("Data");
            List<SolutionGeneratedEvent> events = eventStorage.getEventsByType(SolutionGeneratedEvent.class).collect(Collectors.toList());
            HashMap<String, SolutionGeneratedEvent> results = new HashMap<>();

            for (var event : events) {
                String experimentName = event.getAlgorithmName();
                String instanceName = event.getInstanceName();
                String realName = experimentName + '_' + instanceName;
                var solution = (BIMPSolution) event.getSolution().orElseThrow();
                if (results.get(realName) == null) {
                    results.put(realName, event);
                    continue;
                }
                var solution2 = (BIMPSolution) results.get(realName).getSolution().orElseThrow();
                if (solution2.getScore() > solution.getScore()) {
                    results.put(realName, event);
                }
            }
            int currentRow = 0;
            Row newRow = sheet.createRow(currentRow);
            newRow.createCell(0, CellType.STRING).setCellValue("Algorithm");
            newRow.createCell(1, CellType.STRING).setCellValue("Instance");
            newRow.createCell(2, CellType.NUMERIC).setCellValue("Remaining Budget");
            newRow.createCell(3, CellType.NUMERIC).setCellValue("Total Seed Set");
            newRow.createCell(4, CellType.STRING).setCellValue("Seed Set");
            currentRow++;
            for (String r : results.keySet()) {
                var solution = (BIMPSolution) results.get(r).getSolution().orElseThrow();
                int budget = solution.getBudget();
                int seedNodes = solution.getSelectedNodes().size();
                String nameInstance = results.get(r).getInstanceName();
                String experimentName = results.get(r).getAlgorithmName();
                String seedSet = solution.printSolutions();
                newRow = sheet.createRow(currentRow);
                newRow.createCell(0, CellType.STRING).setCellValue(experimentName);
                newRow.createCell(1, CellType.STRING).setCellValue(nameInstance);
                newRow.createCell(2, CellType.NUMERIC).setCellValue(budget);
                newRow.createCell(3, CellType.NUMERIC).setCellValue(seedNodes);
                newRow.createCell(4, CellType.STRING).setCellValue(seedSet);
                currentRow++;
            }
        }
        catch (Exception e){
            System.out.println("Error in the excel creation");
        }
    }
}
