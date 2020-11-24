package ru.sfedu.course_project.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.course_project.AppClient;
import ru.sfedu.course_project.bean.Feedback;
import ru.sfedu.course_project.bean.Font;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DataProviderCSV extends DataProvider {
    private final String PATH="csv_path";

    private final String FILE_EXTENTION="csv";

    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    private String getFilePath (String object) {
        try {
            return ConfigurationUtil.getConfigurationEntry(PATH) +
            object.toLowerCase() +
            ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createPresentation(long id, String name, List<Slide> slides, String fillColor, Font font, List<Feedback> feedbacks) throws IOException {
        try {
            Presentation presentation = new Presentation(id, name, slides, fillColor, font, feedbacks);
            List<Presentation> listPresentations = new ArrayList();
            listPresentations.add(presentation);

            String path = this.getFilePath("presentation");
            FileWriter filePath = new FileWriter(path);

            log.debug(filePath);

            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<Presentation> beanToCsv = new StatefulBeanToCsvBuilder<Presentation>(writer).withApplyQuotesToAll(false).withSeparator(';').build();
            beanToCsv.write(listPresentations);
            writer.close();
        } catch (IndexOutOfBoundsException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error(e);
        }
    }

    public Presentation getPresentationById (long id) throws IOException {
        FileReader fileReader = new FileReader(this.getFilePath("presentation"));
        CSVReader csvReader = new CSVReader(fileReader);
        CsvToBean<Presentation> csvToBean = new CsvToBeanBuilder<Presentation>(csvReader)
                .withType(Presentation.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        log.debug(csvToBean);
        List<Presentation> listPresentation = csvToBean.parse();
        return null;
//        try {
//            Presentation presentation = listPresentation.stream()
//                    .filter(el -> el.getId() == id).limit(1).findFirst().get();
//            return presentation;
//        } catch (NoSuchElementException e) {
//            log.error(e);
//            return null;
//        }
    }
}
