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
import ru.sfedu.course_project.Constants;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.SuccessConstants;
import ru.sfedu.course_project.bean.Presentation;
import ru.sfedu.course_project.bean.Slide;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.BaseClass;
import ru.sfedu.course_project.tools.Creator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataProviderCSV implements DataProvider {
    private final String PATH="csv_path";
    private final String DATA_PATH="dataPath";

    private final String FILE_EXTENTION="csv";

    private static Logger log = LogManager.getLogger(DataProviderCSV.class);

    public DataProviderCSV () {}

    @Override
    public String getName () {
        return "CSV";
    }

    private String getFilePath (CollectionType collectionType) {
        try {
            return String.format("%s/%s/%s.%s",
                    ConfigurationUtil.getConfigurationEntry(DATA_PATH),
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION),
                    collectionType,
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    ///                      Common section                                 \\\

    @Override
    public <T> Optional<List> getCollection (CollectionType collectionType, Class cl) {
        try {
            FileReader fileReader;
            try {
                log.debug("[getCollection] Attempt to get collection: " + collectionType);
                fileReader = new FileReader(getFilePath(collectionType));
                CSVReader csvReader = new CSVReader(fileReader);
                CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                        .withType(cl)
                        .withSeparator(',')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();
                Optional<List> optionalCollection = Optional.ofNullable(csvToBean.parse());
                log.info("[getCollection] Collection was retrieved: " + collectionType);
                return optionalCollection;
            } catch (FileNotFoundException e) {
                log.error(e);
                log.error(String.format(Constants.MSG_ERROR_DATA_SOURCE, collectionType));
                return Optional.empty();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public <T> Status writeCollection(List list, Class cl) {
        try {
            log.debug(String.format("[writeCollection] Attempt to write in %s", cl.getSimpleName().toLowerCase()));
            CollectionType collectionType = CollectionType.valueOf(cl.getSimpleName().toLowerCase());
            String path = getFilePath(collectionType);
            FileWriter filePath = new FileWriter(path);
            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).withSeparator(',').withApplyQuotesToAll(false).build();
            beanToCsv.write(list);
            writer.close();
            log.info("[writeCollection] Collection was wrote");
            return Status.success;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
            log.error("[writeCollection] Unable to write collection");
            return Status.error;
        }
    }

    @Override
    public <T extends BaseClass> Status removeRecordById (CollectionType collectionType, Class cl, UUID id) {
        try {
            log.debug(String.format("[removeRecordById] Removing elements from collection: %s", collectionType));
            List <T> collection = getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
            List <T> updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
            if (updatedCollection.size() == collection.size()) {
                log.error("[removeRecordById] Unable to find element with provided id: " + id);
                return Status.error;
            }
            writeCollection(updatedCollection, cl);
            log.info("[removeRecordById] Element was successfully removed: " + id);
            return Status.success;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[removeRecordById] Unable to remove element from collection");
            return Status.error;
        }
    }


    @Override
    public <T extends BaseClass> Boolean isIdInUse (String id, List<T> list) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<T> item = list.stream().filter(el -> el.getId().equals(uuid)).findFirst();
            if (item.isPresent()) {
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }

//    @Override
//    public <T> Status addCollectionRecord (T record, UUID id) {
//        try {
//            String collectionType = record.getClass().getName().toLowerCase();
//            List itemsList = getCollection(CollectionType.valueOf(collectionType), record.getClass()).orElse(new ArrayList());
//            itemsList.add(record);
//            Status result = writeCollection(itemsList, record.getClass());
//            if (result == Status.success) {
//                log.info(String.format("[addCollectionRecord] Item was successfully added: %s %s", collectionType, id));
//                return Status.success;
//            } else {
//                log.error("[addCollectionRecord] Unable to create presentation");
//                return Status.error;
//            }
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            log.error(e);
//            log.error("[addCollectionRecord] Unable to add collection record ");
//            return Status.error;
//        }
//    }


    ///                      Presentations section                          \\\

    @Override
    public Result createPresentation(HashMap args) {
        try {
            List<Presentation> listPresentations = getCollection(CollectionType.presentation, Presentation.class)
                    .orElse(new ArrayList());
            if (args.get("id") != null) {
                String id = (String) args.get("id");
                if (isIdInUse(id, listPresentations)) return new Result(Status.error, ErrorConstants.ID_IN_USE);
            }
            Optional<Presentation> optionalPresentation = (Optional<Presentation>) new Creator().create(Presentation.class, args);
            if (!optionalPresentation.isPresent()) {
                log.error(ErrorConstants.ARGUMENTS_ERROR);
                return new Result(Status.error, ErrorConstants.ARGUMENTS_ERROR);
            }
            Presentation presentation = optionalPresentation.get();
            listPresentations.add(presentation);
            Status result = writeCollection(listPresentations, Presentation.class);
            if (result == Status.success) {
                log.info(SuccessConstants.PRESENTATION_CREATED + presentation.getId());
                return new Result(Status.success, presentation.getId());
            } else {
                log.error(ErrorConstants.CREATE_PRESENTATION);
                return new Result(Status.error, ErrorConstants.CREATE_PRESENTATION);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error(e);
            log.error(ErrorConstants.CREATE_PRESENTATION);
            return new Result(Status.error, ErrorConstants.CREATE_PRESENTATION);
        }
    }

    public Optional<Presentation> getPresentationById (HashMap arguments) throws IOException {
        UUID id = UUID.fromString((String) arguments.get("id"));
        try {
            List<Presentation> listPresentations = getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
            log.debug("Attempt to find presentation: " + id);
            Optional<Presentation> presentation = listPresentations.stream()
                    .filter(el -> el.getId().equals(id)).findFirst();
            if (presentation.isPresent()) {
                log.info("[getPresentationById] Result: " + presentation.get().toString());
                return presentation;
            } else {
                log.error("[getPresentationById] Unable to get presentation: " + id);
                return Optional.empty();
            }
        } catch (NoSuchElementException e) {
            log.error(e);
            log.error("Unable to get presentation");
            return Optional.empty();
        }
    }

    public Status removePresentationById (HashMap arguments) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        try {
            if (arguments.get("id") == null) {
                log.error("[removePresentationById] Presentation id is not provide");
                return Status.error;
            } else {
                UUID id = UUID.fromString((String) arguments.get("id"));
                return removeRecordById(CollectionType.presentation, Presentation.class, id);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return Status.error;
        }
    }

    public Status editPresentationOptions (HashMap arguments) throws CsvDataTypeMismatchException, IOException, CsvRequiredFieldEmptyException {
        try {
            UUID id = UUID.fromString((String) arguments.getOrDefault("id", null));
            if (id == null) {
                log.error("[editPresentationOptions] Presentation id is not provided");
                return Status.error;
            }
            Boolean validId = getPresentationById(arguments).isPresent();
            if (validId) {
                List<Presentation> list = getCollection(CollectionType.presentation, Presentation.class).orElse(new ArrayList());
                List<Presentation> updatedList = list.stream().map(el -> {
                    if (el.getId().equals(id)) {
                        el.setFillColor((String) arguments.getOrDefault("fillColor", el.getFillColor()));
                        el.setFontFamily((String) arguments.getOrDefault("fontFamily", el.getFontFamily()));
                        el.setName((String) arguments.getOrDefault("name", el.getName()));
                    } return el;
                }).collect(Collectors.toList());
                Status result = writeCollection(updatedList, Presentation.class);
                if (result == Status.success) {
                    log.info("[editPresentationOptions] Presentation options was successfully updated: " + id);
                    return Status.success;
                }
                return Status.error;
            } else {
                log.info("[editPresentationOptions] Unable to find presentation: " + id);
                return Status.error;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Unable to edit presentation options");
            return Status.error;
        }
    }


    ///                         Slides section                          \\\

    public Optional<List> getPresentationSlides (HashMap arguments) {
        try {
            FileReader fileReader;
            try {
                fileReader = Optional.of(new FileReader(getFilePath(CollectionType.slide))).orElse(null);
            } catch (FileNotFoundException e) {
                log.error("[getPresentationSlides] Unable to get slides data source");;
                return Optional.empty();
            }
            if (fileReader != null) {
                UUID presentationId = UUID.fromString((String) arguments.get("presentationId"));
                HashMap getPresentationByIdParams = new HashMap();
                getPresentationByIdParams.put("id", String.valueOf(presentationId));
                Optional<Presentation> presentation = getPresentationById(getPresentationByIdParams);
                if (presentation.isPresent()) {
                    Optional<List> listSlides = getCollection(CollectionType.slide, Slide.class);
                    Optional<List> presentationSlides = Optional.empty();
                    if (listSlides.isPresent()) {
                        log.debug("[getPresentationSlides] Attempt to find presentation slides for: " + presentationId);
                        List<Slide> list = listSlides.get();
                        presentationSlides = Optional.of(list.stream().filter(slide -> slide.getPresentationId().equals(presentationId)).collect(Collectors.toList()));
                    }
                    log.debug("[getPresentationSlides] Found presentation slides: " + presentationSlides.orElse(new ArrayList()));
                    log.info("[getPresentationSlides] Found " + arguments.get("id") + " slides for presentation: " + arguments.get("presentationId"));
                    return presentationSlides;
                } else {
                    log.error("[getPresentationSlides] Unable to find presentation with provided id");
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[getPresentationSlides] Unable to get presentation slides");;
            return Optional.empty();
        }
    }

    public Object createPresentationSlide (HashMap arguments) {
        try {
            if (arguments.get("presentationId") == null) {
                log.error("[createPresentationSlide] Argument is required: presentationId");
                return null;
            }
            HashMap getPresentationByIdParams = new HashMap();
            getPresentationByIdParams.put("id", String.valueOf(arguments.get("presentationId")));
            Optional<Presentation> optionalPresentation = getPresentationById(getPresentationByIdParams);
            if (optionalPresentation.isPresent()) {
                log.info(arguments.entrySet());
                List<Slide> slides = getPresentationSlides(arguments).orElse(new ArrayList());
                arguments.put("index", slides.size());
                Slide slide = new Slide(arguments);
                log.info("[createPresentationSlide] Create slide: " + slide.toString());
                log.debug("[createPresentationSlide] For presentation: " + slide.getPresentationId());
                slides.add(slide);
                Status result = writeCollection(slides, Slide.class);
                if (result == Status.success) {
                    return slide.getId();
                } else {
                    return Status.error;
                }
            } else {
                log.error("[createPresentationSlide] Unable to find presentation with provided id");
                return Status.error;
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[createPresentationSlide] Unable to create slide");
            return Status.error;
        }
    }
}
