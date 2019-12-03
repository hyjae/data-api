//package kr.datasolution.ds.api.util;
//
//import java.io.PrintWriter;
//import java.security.InvalidParameterException;
//import java.util.Arrays;
//import java.util.List;
//
//import com.opencsv.CSVWriter;
//import com.opencsv.bean.ColumnPositionMappingStrategy;
//import com.opencsv.bean.StatefulBeanToCsv;
//import com.opencsv.bean.StatefulBeanToCsvBuilder;
//
//public class WriteDataToCSV {
//
//    public static void writeDataToCsvUsing
//
//    public static void writeDataToCsvUsingStringArray(PrintWriter writer, List<String> headers, List<Object> objectList) {
//
//        if (headers.size() != objectList.size()) {
//            throw new InvalidParameterException();
//        }
//
//        try (
//                CSVWriter csvWriter = new CSVWriter(writer,
//                        CSVWriter.DEFAULT_SEPARATOR,
//                        CSVWriter.NO_QUOTE_CHARACTER,
//                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                        CSVWriter.DEFAULT_LINE_END);
//        ){
//            csvWriter.writeNext((String[]) headers.toArray());
//
//            for (Object : objectList) {
//                String[] data = {
//                        customer.getId().toString(),
//                        customer.getFirstName(),
//                        customer.getLastName()
//                };
//
//                csvWriter.writeNext(data);
//            }
//
//            System.out.println("Write CSV using CSVWriter successfully!");
//        }catch (Exception e) {
//            System.out.println("Writing CSV error!");
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Way 2
//     */
//    public static void writeDataToCsvWithListObjects(PrintWriter writer, List<String> headers, List<Object> objectList) {
//
//        if (headers.size() != objectList.size()) {
//            throw new InvalidParameterException();
//        }
//        StatefulBeanToCsv<Object> beanToCsv = null;
//        try (
//                CSVWriter csvWriter = new CSVWriter(writer,
//                        CSVWriter.DEFAULT_SEPARATOR,
//                        CSVWriter.NO_QUOTE_CHARACTER,
//                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                        CSVWriter.DEFAULT_LINE_END);
//        ) {
//            csvWriter.writeNext((String[]) headers.toArray());
//
//            // write List of Objects
//            ColumnPositionMappingStrategy<Object> mappingStrategy = new ColumnPositionMappingStrategy<Object>();
//            mappingStrategy.setType(Object.class);
//            mappingStrategy.setColumnMapping(String.valueOf(headers));
//
//            beanToCsv = new StatefulBeanToCsvBuilder<Customer>(writer)
//                    .withMappingStrategy(mappingStrategy)
//                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
//                    .build();
//
//            beanToCsv.write(customers);
//
//            System.out.println("Write CSV using BeanToCsv successfully!");
//        }catch (Exception e) {
//            System.out.println("Writing CSV error!");
//            e.printStackTrace();
//        }
//    }
//}
