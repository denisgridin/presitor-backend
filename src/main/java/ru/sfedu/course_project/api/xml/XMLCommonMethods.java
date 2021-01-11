package ru.sfedu.course_project.api.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ConstantsError;
import ru.sfedu.course_project.ConstantsSuccess;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.xml.WrapperXML;
import ru.sfedu.course_project.tools.xml.XMLMatcher;
import ru.sfedu.course_project.utils.ConfigurationUtil;
import ru.sfedu.course_project.utils.ConstantsField;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class XMLCommonMethods {
    private static final String DATA_PATH = "dataPath";
    private static final String FILE_EXTENTION = "xml";
    private static final Logger log = LogManager.getLogger(XMLCommonMethods.class);

    public String getName() {
        return "XML";
    }

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

    public static <T> Optional<List> getCollection(CollectionType collectionType) {
        try {
            log.debug(ConstantsInfo.COLLECTION_GET);
            Optional<String> path = Optional.ofNullable(getFilePath(collectionType));
            if (!path.isPresent()) {
                return Optional.empty();
            }
            String filePath = path.get();
            (new File(filePath)).createNewFile();
            FileReader fileReader = new FileReader(filePath);
            Serializer serializer = new Persister(new XMLMatcher());
            WrapperXML xml = serializer.read(WrapperXML.class, fileReader);
            if (xml.getList() == null) {
                xml.setList(new ArrayList<T>());
            }
            ;
            return Optional.ofNullable(xml.getList());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return Optional.empty();
        }
    }


    public static Optional getInstanceById(CollectionType collectionType, HashMap arguments) {
        UUID id = UUID.fromString((String) arguments.get(ConstantsField.ID));
        log.debug(collectionType + " id: " + id);
        try {
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug(ConstantsInfo.PRESENTATIONS_GET + id);
                    log.debug(ConstantsInfo.PRESENTATIONS + listInstance);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case slide: {
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SLIDE + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case comment: {
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug(ConstantsInfo.COMMENT + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case shape: {
                    ArrayList<Shape> listInstance = (ArrayList<Shape>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug(ConstantsInfo.SHAPE + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case content: {
                    ArrayList<Content> listInstance = (ArrayList<Content>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug(ConstantsInfo.CONTENT + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case assessment: {
                    ArrayList<Assessment> listInstance = (ArrayList<Assessment>) getCollection(collectionType).orElse(new ArrayList());
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

    public static <T> Status writeCollection(List list, Class cl, CollectionType collectionType) {
        try {
            log.debug(String.format(ConstantsInfo.COLLECTION_WRITE, cl.getSimpleName().toLowerCase()));

            Optional<String> path = Optional.ofNullable(getFilePath(collectionType));
            if (!path.isPresent()) {
                log.error(ConstantsError.COLLECTION_WRITE);
                return Status.error;
            }
            String filePath = path.get();
            log.debug("File path: " + filePath);
            (new File(filePath)).createNewFile();
            FileWriter writer = new FileWriter(path.get(), false);
            Serializer serializer = new Persister(new XMLMatcher());
            WrapperXML<T> xml = new WrapperXML<T>();
            xml.setList(list);

            serializer.write(xml, writer);
            log.info(ConstantsSuccess.COLLECTION_WROTE);
            return Status.success;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.COLLECTION_WRITE);
            return Status.error;
        }
    }

    public static Status removeRecordById(CollectionType collectionType, Class cl, UUID id) {
        try {
            log.debug(String.format(ConstantsInfo.INSTANCE_REMOVE_FROM_COLLECTION, collectionType));
            List updatedCollection = new ArrayList();
            int collectionSize = 0;
            switch (collectionType) {
                case presentation: {
                    List<Presentation> collection = (ArrayList<Presentation>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format(ConstantsError.COLLECTION_GET, collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case slide: {
                    List<Slide> collection = (ArrayList<Slide>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format(ConstantsError.COLLECTION_GET, collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case comment: {
                    List<Comment> collection = (ArrayList<Comment>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format(ConstantsError.COLLECTION_GET, collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case assessment: {
                    List<Assessment> collection = (ArrayList<Assessment>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format(ConstantsError.COLLECTION_GET, collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case error:
                    break;
            }
            if (updatedCollection.size() == collectionSize) {
                log.error(ConstantsError.PRESENTATION_NOT_FOUND + id);
                return Status.error;
            }
            writeCollection(updatedCollection, cl, collectionType);
            log.info(ConstantsSuccess.INSTANCE_REMOVED + id);
            return Status.success;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error(ConstantsError.ELEMENT_REMOVE);
            return Status.error;
        }
    }

    public static <T> Boolean isIdInUse (String id, List<T> list, CollectionType collectionType) {
        try {
            UUID uuid = UUID.fromString(id);
            log.debug(ConstantsInfo.COLLECTION_CHECK_ID + String.format(" %s %s", collectionType, id));
            switch (collectionType) {
                case presentation: {
                    Optional<Presentation> item = (Optional<Presentation>) list.stream().filter(el -> {
                        Presentation presentation = (Presentation) el;
                        return presentation.getId().equals(uuid);
                    }).findFirst();
                    if (item.isPresent()) {
                        log.info(ConstantsInfo.COLLECTION_FOUND_ID + String.format(" %s %s", collectionType, id));
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


    public static Optional getInstanceExistenceByField (CollectionType collectionType, Class cls, String idField, String id) {
        try {
            log.debug("[getInstanceExistenceByField] Get: " + collectionType + "; " + idField + ": " + id);
            HashMap args = new HashMap();
            args.put(idField, id);
            Optional optionalInstance = getInstanceById(collectionType, args);
            log.debug(optionalInstance);
            return optionalInstance;
        } catch (RuntimeException e) {
            log.error(e);
            log.error(ConstantsError.INSTANCE_NOT_FOUND);
            return Optional.empty();
        }
    }

    public static Result updateRecordInCollection (Class cls, CollectionType collectionType, Object instance, UUID instanceId) {
        try {
            log.debug("[updateRecordInCollection] Update: " + instance.toString());
            ArrayList updatedCollection = new ArrayList();
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType).orElse(new ArrayList());
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
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType).orElse(new ArrayList());
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
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType).orElse(new ArrayList());
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

}