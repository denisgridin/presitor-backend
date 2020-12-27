package ru.sfedu.course_project.api.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import ru.sfedu.course_project.ConstantsInfo;
import ru.sfedu.course_project.ErrorConstants;
import ru.sfedu.course_project.bean.*;
import ru.sfedu.course_project.enums.CollectionType;
import ru.sfedu.course_project.enums.Status;
import ru.sfedu.course_project.tools.ArgsValidator;
import ru.sfedu.course_project.tools.Result;
import ru.sfedu.course_project.tools.xml.WrapperXML;
import ru.sfedu.course_project.tools.xml.XMLMatcher;
import ru.sfedu.course_project.utils.ConfigurationUtil;

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

    private static Optional<String> getFilePath(CollectionType collectionType) {
        try {
            Optional <String> path = Optional.of(String.format("%s/%s/%s.%s",
                    ConfigurationUtil.getConfigurationEntry(DATA_PATH),
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION),
                    collectionType,
                    ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION)));
            log.debug(path);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static <T> Optional<List> getCollection(CollectionType collectionType) {
        try {
            log.debug(ConstantsInfo.COLLECTION_GET);
            Optional<String> path = getFilePath(collectionType);
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
        UUID id = UUID.fromString((String) arguments.get("id"));
        log.debug(collectionType + " id: " + id);
        try {
            switch (collectionType) {
                case presentation: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug("Attempt to find presentation: " + id);
                    log.debug("list instance: " + listInstance);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case slide: {
                    ArrayList<Slide> listInstance = (ArrayList<Slide>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug("Attempt to find slide: " + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case comment: {
                    ArrayList<Comment> listInstance = (ArrayList<Comment>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug("Attempt to find comment: " + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case template: {
                    ArrayList<Presentation> listInstance = (ArrayList<Presentation>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug("Attempt to find template: " + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case shape: {
                    ArrayList<Shape> listInstance = (ArrayList<Shape>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug("Attempt to find Shape: " + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
                }
                case content: {
                    ArrayList<Content> listInstance = (ArrayList<Content>) getCollection(collectionType).orElse(new ArrayList());
                    log.debug("Attempt to find Content: " + id);
                    return listInstance.stream()
                            .filter(el -> el.getId().equals(id)).findFirst();
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
            log.debug(String.format("[writeCollection] Attempt to write in %s", cl.getSimpleName().toLowerCase()));

            Optional<String> path = getFilePath(collectionType);
            if (!path.isPresent()) {
                log.error(ErrorConstants.COLLECTION_WRITE);
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
            log.info("[writeCollection] Collection was wrote");
            return Status.success;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            log.error("[writeCollection] Unable to write collection");
            return Status.error;
        }
    }

    public static Status removeRecordById(CollectionType collectionType, Class cl, UUID id) {
        try {
            log.debug(String.format("[removeRecordById] Removing elements from collection: %s", collectionType));
            List updatedCollection = new ArrayList();
            int collectionSize = 0;
            switch (collectionType) {
                case presentation: {
                    List<Presentation> collection = (ArrayList<Presentation>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case slide: {
                    List<Slide> collection = (ArrayList<Slide>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case comment: {
                    List<Comment> collection = (ArrayList<Comment>) getCollection(collectionType).orElseThrow(() -> new RuntimeException(String.format("[removeRecordById] Unable to get collection: %s", collectionType)));
                    collectionSize = collection.size();
                    updatedCollection = collection.stream().filter(el -> !el.getId().equals(id)).collect(Collectors.toList());
                    break;
                }
                case element:
                    // TODO
                    break;
                case error:
                    break;
            }
            if (updatedCollection.size() == collectionSize) {
                log.error("[removeRecordById] Unable to find element with provided id: " + id);
                return Status.error;
            }
            writeCollection(updatedCollection, cl, collectionType);
            log.info("[removeRecordById] Element was successfully removed: " + id);
            return Status.success;
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error(e);
            log.error("[removeRecordById] Unable to remove element from collection");
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
            log.error(ErrorConstants.INSTANCE_NOT_FOUND);
            return Optional.empty();
        }
    }

}