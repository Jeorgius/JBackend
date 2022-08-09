package com.jeorgius.jbackend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class BaseUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String EXP_KEY = "exp";
    public static final String ESIA_USER_ID_KEY = "urn:esia:sbj_id";
    // email regexp is taken from frontend
    public static final String EMAIL_REGEXP = "((([a-z]|[A-Z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|[A-Z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|[A-Z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[A-Z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|[A-Z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[A-Z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[A-Z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[A-Z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|[A-Z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[A-Z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))";
    public static final String PHONE_REGEXP = "((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}";
    public static final String ORG_OGRN_REGEXP = "^\\d{13}$";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        .withZone(ZoneId.systemDefault());

    protected BaseUtils() {
        // static methods and constants only (not a component)
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static int getInt(String string, int defaultValue) {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long getLong(String string, int defaultValue) {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static <T, R> R ifNotNull(T value, Function<T, R> func) {
        return value != null ? func.apply(value) : null;
    }

    public static <T> T ifNotNull(T val, T def) {
        return val != null
                ? val
                : def;
    }

    public static <T, R> R ifNotNull(T val, Function<T, R> get, R def) {
        return val != null
                ? ifNotNull(get.apply(val), def)
                : def;
    }

    public static <T, R> R ifNotEmpty(List<T> vals, Function<List<T>, R> get) {
        return ifNotEmpty(vals, get, null);
    }

    public static <T, R> R ifNotEmpty(List<T> vals, Function<List<T>, R> get, R defaultValue) {
        return vals != null && !vals.isEmpty()
                ? get.apply(vals)
                : defaultValue;
    }

    public static <R> R ifNotEmpty(String val, Function<String, R> get) {
        return val != null && !val.isEmpty()
                ? get.apply(val)
                : null;
    }

    public static String getFullName(String surname, String name, String patronymic) {
        return surname + " " + name + (patronymic != null ? " " + patronymic : "");
    }

    public MediaType resolveFileMimeType(String dtoContentType) {
        if (dtoContentType == null) {
            dtoContentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        try {
            return MediaType.parseMediaType(dtoContentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public Path generateFilePathForDate(Instant insertDate, String fileStorageRootPath) {
        Path datePath = getDatePathEnsuringExists(insertDate, fileStorageRootPath);
        return generateFilePath(datePath);
    }

    public Map<String, String> getTokenInfo(String token) throws Exception {
        var tokenData = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        return getInfoFromJson(tokenData);
    }

    public Map<String, Object> getObjectTokenInfo(String token) throws Exception {
        var tokenData = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        return getObjectInfoFromJson(tokenData);
    }

    public Map<String, String> getInfoFromJson(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<Map<String, String>>() {
        });
    }

    public Map<String, Object> getObjectInfoFromJson(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public boolean isTokenHasExpiredDate(String token) {
        try {
            var tokenInfo = getObjectTokenInfo(token);
            return tokenInfo != null && tokenInfo.containsKey(EXP_KEY);
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiredTimestamp(String token) {
        try {
            var tokenInfo = getObjectTokenInfo(token);
            if (tokenInfo == null || !tokenInfo.containsKey(EXP_KEY)) {
                return 0;
            }
            return (Integer) tokenInfo.get(EXP_KEY);
        } catch (Exception e) {
            log.info("Ошибка при получении срока действия токена", e);
            return 0;
        }
    }

    protected Path getDatePathEnsuringExists(Instant instant, String fileStorageRootPath) {
        File file = Paths.get(fileStorageRootPath, getPathForDate(instant)).toFile();
        if (file.exists() && file.isFile()) {
            String message =
                    String.format("Failed to create directories for path [%s]. File already exist and it is a regular file, not a director", file.getAbsolutePath());
            throw new RuntimeException(message);
        } else if (!file.exists() && !file.mkdirs()) {
            String message = String.format("Failed to create directories for path [%s]", file.getAbsolutePath());
            throw new RuntimeException(message);
        }
        return file.toPath();
    }

    protected Path generateFilePath(Path basePath) {
        String fileName = generateFileName();
        Path filePath = basePath.resolve(fileName);
        while (filePath.toFile().exists()) {
            filePath = basePath.resolve(generateFileName());
        }
        return filePath;
    }

    protected String getPathForDate(Instant instant) {
        LocalDate ld = instant.atOffset(ZoneOffset.UTC).toLocalDate();
        return String.join(File.separator,
                new String[]{Integer.toString(ld.getYear()),
                        padDatePartByZero(ld.getMonthValue()),
                        padDatePartByZero(ld.getDayOfMonth())});
    }

    protected String padDatePartByZero(int value) {
        return StringUtils.leftPad(Integer.toString(value), 2, "0");
    }

    protected String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public interface UploadFileCallback<S> {
        ResponseEntity<S> upload(File tempFile);
    }

    public static boolean isValidDate(String input) {
        try {
            LocalDate.parse(input);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String input) {
        try {
            LocalTime.parse(input);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
