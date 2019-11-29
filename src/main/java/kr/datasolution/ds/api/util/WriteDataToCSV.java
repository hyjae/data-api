//package kr.datasolution.ds.api.util;
//
//import java.io.PrintWriter;
//import java.util.List;
//
//import com.opencsv.CSVWriter;
//import com.opencsv.bean.ColumnPositionMappingStrategy;
//import com.opencsv.bean.StatefulBeanToCsv;
//import com.opencsv.bean.StatefulBeanToCsvBuilder;
//
//public class WriteDataToCSV {
//
//    public static void writeDataToCsvUsingStringArray(PrintWriter writer, List<> customers) {
//        String[] CSV_HEADER = { "id", "firstname", "lastname" };
//        try (
//                CSVWriter csvWriter = new CSVWriter(writer,
//                        CSVWriter.DEFAULT_SEPARATOR,
//                        CSVWriter.NO_QUOTE_CHARACTER,
//                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                        CSVWriter.DEFAULT_LINE_END);
//        ){
//            csvWriter.writeNext(CSV_HEADER);
//
//            for (Customer customer : customers) {
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
//    public static void writeDataToCsvWithListObjects(PrintWriter writer,List<Customer> customers) {
//        String[] CSV_HEADER = { "id", "firstname", "lastname" };
//        StatefulBeanToCsv<Customer> beanToCsv = null;
//        try (
//                CSVWriter csvWriter = new CSVWriter(writer,
//                        CSVWriter.DEFAULT_SEPARATOR,
//                        CSVWriter.NO_QUOTE_CHARACTER,
//                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//                        CSVWriter.DEFAULT_LINE_END);
//        ){
//            csvWriter.writeNext(CSV_HEADER);
//
//            // write List of Objects
//            ColumnPositionMappingStrategy<Customer> mappingStrategy =
//                    new ColumnPositionMappingStrategy<Customer>();
//
//            mappingStrategy.setType(Customer.class);
//            mappingStrategy.setColumnMapping(CSV_HEADER);
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
