package ru.sfedu.course_project.api.csv;

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
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CSVCommonMethods {
    private static final Logger log = LogManager.getLogger(CSVCommonMethods.class);
    private static final String DATA_PATH="dataPath";

    private static final String FILE_EXTENTION="csv";
    private static String getFilePath (CollectionType collectionType) {
        try {
            String dataPath = System.getProperty("dataPath");
            String filePath = String.format("/%s/%s/%s.%s",
                    System.getProperty("dataPath"),
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION),
                    collectionType,
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION));

//            String root = System.getProperty("user.dir");
            String root = ".";
            String path = (root + filePath).replace("\\", "/");
            log.debug("path: " + path );

            File directory = new File(path.substring(0, path.lastIndexOf("/")));

            if (!directory.exists()){
                directory.mkdirs();
            }

            new File(path).createNewFile();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }


    ///                      Common section                                 \\\

    public static <T> Optional<List> getCollection (CollectionType collectionType, Class cl) {
        try {
            FileReader fileReader;
            try {
                log.debug(ConstantsInfo.COLLECTION_GET + collectionType);
                Optional optionalFileReader = Optional.ofNullable(new FileReader(getFilePath(collectionType)));
                if (optionalFileReader.isPresent()) {
                    fileReader = (FileReader) optionalFileReader.get();
                    CSVReader csvReader = new CSVReader(fileReader);
                    CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
                            .withType(cl)
                            .withSeparator(',')
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
                    Optional<List> optionalCollection = Optional.ofNullable(csvToBean.parse());
                    log.info(ConstantsInfo.COLLECTION_RETRIEVED + collectionType);
                    return optionalCollection;
                } else {
                    log.info(ConstantsError.DATA_SOURCE_ERROR + collectionType);
                    return Optional.empty();
                }
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

    public static <T> Status writeCollection(List list, Class cl, CollectionType collectionType) {
        try {
            log.debug(String.format(ConstantsInfo.COLLECTION_WRITE, cl.getSimpleName().toLowerCase()));

            String path = getFilePath(collectionType);
            FileWriter filePath = new FileWriter(path);
            CSVWriter writer = new CSVWriter(filePath);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).withSeparator(',').withApplyQuotesToAll(false).build();
            beanToCsv.write(list);
            writer.close();
            log.info(ConstantsSuccess.COLLECTION_WROTE);
            return Status.success;
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.COLLECTION_WRITE);
            return Status.error;
        }
    }

    public static Status removeRecordById (CollectionType collectionType, Class cl, UUID id) {
        try {
            log.debug(String.format(ConstantsInfo.INSTANCE_REMOVE_FROM_COLLECTION, collectionType));
            List updatedCollection = new ArrayList();
            int collectionSize = 0;
            switch (collectionType) {
                case presentation: {
                    List<Presentation> collection = (ArrayList<Presentation>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format(String.format(ConstantsError.COLLECTION_GET, collectionType))));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case slide: {
                    List<Slide> collection = (ArrayList<Slide>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format(String.format(ConstantsError.COLLECTION_GET, collectionType))));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case comment: {
                    List<Comment> collection = (ArrayList<Comment>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format(String.format(ConstantsError.COLLECTION_GET, collectionType))));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case assessment: {
                    List<Assessment> collection = (ArrayList<Assessment>) getCollection(collectionType, cl).orElseThrow(() -> new RuntimeException(String.format(String.format(ConstantsError.COLLECTION_GET, collectionType))));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case error:
                    break;
            }
            if (updatedCollection.size() == collectionSize) {
                log.error(ConstantsError.ELEMENT_GET + id);
                return Status.error;
            }
            writeCollection(updatedCollection, cl, collectionType);
            log.info(ConstantsSuccess.INSTANCE_REMOVED + id);
            return Status.success;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.INSTANCE_REMOVE);
            return Status.error;
        }
    }

    public static Result updateRecordInCollection (Class cls, CollectionType collectionType, Object instance, UUID instanceId) {
        try {
            log.debug(ConstantsInfo.INSTANCE_UPDATE + instance.toString());
            ArrayList updatedCollection = new ArrayList();
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType, cls).orElse(new ArrayList());
                    updatedCollection = (ArrayList) listInstance
                            .stream()
                            .map(el -> {
                                if (el.getId().equals(instanceId)) {
                                    el = (Presentation) instance;
                                }
                                return el;
                            }).collect(Collectors.toList());
                    break;
                }
                case slide: {
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType, cls).orElse(new ArrayList());
                    updatedCollection = (ArrayList) listInstance
                            .stream()
                            .map(el -> {
                                if (el.getId().equals(instanceId)) {
                                    el = (Slide) instance;
                                }
                                return el;
                            }).collect(Collectors.toList());
                    break;
                }
                case comment: {
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType, cls).orElse(new ArrayList());
                    updatedCollection = (ArrayList) listInstance
                            .stream()
                            .map(el -> {
                                if (el.getId().equals(instanceId)) {
                                    el = (Comment) instance;
                                }
                                return el;
                            }).collect(Collectors.toList());
                    break;
                }
                case element:
                    // TODO
                    break;
                case error:
                    break;
            }
            Status status = writeCollection(updatedCollection, cls, collectionType);
            log.debug(ConstantsInfo.STATUS + status);
            return new Result(status, ConstantsSuccess.EDIT_ITEM + collectionType);
        } catch (RuntimeException e) {
            log.error(e);
            return new Result(Status.error, ConstantsError.INSTANCE_UPDATE);
        }
    }

    public static Optional getInstanceById (Class cl, CollectionType collectionType, HashMap arguments) {
        UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
        log.debug(collectionType + " id: " + id);
        try {
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SEARCH + CollectionType.presentation);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    log.debug(instance);
                    return instance;
                }
                case slide: {
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SEARCH + CollectionType.slide);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case comment: {
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SEARCH + CollectionType.comment);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case shape: {
                    ArrayList<Shape> listInstance = (ArrayList<Shape>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SEARCH + CollectionType.shape);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case content: {
                    ArrayList<Content> listInstance = (ArrayList<Content>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SEARCH + CollectionType.content);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                case assessment: {
                    ArrayList<Assessment> listInstance = (ArrayList<Assessment>) getCollection(collectionType, cl).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SEARCH + CollectionType.assessment);
                    log.debug(ConstantsInfo.MARKS + listInstance);
                    Optional instance = listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                    return instance;
                }
                default: {
                    return Optional.empty();
                }
            }
        } catch (NoSuchElementException e) {
            log.error(e);
            return Optional.empty();
        }
    }

    public static Optional getInstanceExistenceByField (CollectionType collectionType, Class cls, String idField, String id) {
        try {
            log.debug("[getInstanceExistenceByField] Get: " + cls.getSimpleName() + "; " + idField + ": " + id);
            HashMap args = new HashMap();
            args.put(idField, id);
            Optional optionalInstance = getInstanceById(cls, collectionType, args);
            log.debug(optionalInstance);
            return optionalInstance;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.INSTANCE_NOT_FOUND);
            return Optional.empty();
        }
    }


    public static <T> Boolean isIdInUse (String id, List<T> list, CollectionType collectionType) {
        try {
            UUID uuid = UUID.fromString(id);
            switch (collectionType) {
                case presentation: {
                    Optional<Presentation> item = (Optional<Presentation>) list.stream().filter(el -> {
                        Presentation presentation = (Presentation) el;
                        return presentation.getId().equals(uuid);
                    }).findFirst();
                    if (item.isPresent()) {
                        return true;
                    }
                }
                default: {
                    return false;
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            return false;
        }
    }

    public static Status updatePresentationCollection (Presentation presentation) {
        Optional<List> optionalList = getCollection(CollectionType.presentation, Presentation.class);
        if (optionalList.isPresent()) {
            ArrayList updatedCollection = (ArrayList) optionalList.get().stream().map(el -> {
                Presentation item = (Presentation) el;
                if (item.getId().equals(presentation.getId())) {
                    item.setSlides(presentation.getSlides());
                }
                return item;
            }).collect(Collectors.toList());
            return Status.success;
        } else {
            log.error(ConstantsError.PRESENTATIONS_GET);
            return Status.error;
        }
    }
}
