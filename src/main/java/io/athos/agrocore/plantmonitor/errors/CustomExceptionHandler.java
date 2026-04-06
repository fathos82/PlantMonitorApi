package io.athos.agrocore.plantmonitor.errors;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.security.SignatureException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.athos.agrocore.plantmonitor.StringUtils.*;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();

            // Remove o prefixo do objeto aninhado, se existir
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }

            errors.put(fieldName, error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(ValidationPoolException.class)
    public ResponseEntity<Map<String, String>> handleServiceValidationExceptions(ValidationPoolException ex) {
        return new ResponseEntity<>(ex.getErrors(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityExceptions(DataIntegrityViolationException ex) {
        ex.getMostSpecificCause();
        String raw = ex.getMostSpecificCause().getMessage();
        if (raw == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("detail", "Violação de integridade de dados."));
        }

        String msg = raw;

        // 1) UNIQUE / DUPLICATE KEY (Postgres)
        // Ex.: Key (emission_batch_id, emission_type)=(1, BUSINESS_TRAVEL) already exists.
        if (msg.toLowerCase().contains("duplicate key value")) {
            String detail = buildUniqueDetail(msg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("detail", detail));
        }

        // 2) NOT NULL
        // Ex.: ERROR: null value in column "nome" violates not-null constraint
        if (msg.toLowerCase().contains("violates not-null constraint") ||
                msg.toLowerCase().contains("null value in column")) {
            String column = extractQuoted(msg); // pega "nome"
            String field = toCamelCase(column);
            String detail = String.format("O campo '%s' é obrigatório (não pode ser nulo).", field);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("detail", detail));
        }

        // 3) FOREIGN KEY
        // Ex.: ERROR: insert or update on table "child" violates foreign key constraint "fk_child_parent"
        if (msg.toLowerCase().contains("violates foreign key constraint")) {
            // tenta extrair nome da constraint
            String constraint = extractQuoted(msg);
            String detail = "Referência inválida: verifique os IDs relacionados (violação de chave estrangeira"
                    + (constraint != null ? " '"+constraint+"'" : "") + ").";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("detail", detail));
        }

        // 4) CHECK constraint
        // Ex.: ERROR: new row for relation "x" violates check constraint "ck_alguma_regra"
        if (msg.toLowerCase().contains("violates check constraint")) {
            String constraint = extractQuoted(msg);
            String detail = "Os dados não atendem a uma regra de validação"
                    + (constraint != null ? " ('"+constraint+"')" : "") + ".";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("detail", detail));
        }

        // 5) Fallback genérico (comportamento padrão ainda necessário!)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("detail", "Violação de integridade de dados."));
    }

    /* ================= Helpers ================= */
    private static String buildUniqueDetail(String msg) {
        // casa "(campos)=(valores)"
        Matcher m = Pattern.compile("\\((.*?)\\)=\\((.*?)\\)").matcher(msg);
        if (m.find()) {
            String fieldsStr = m.group(1);
            String valuesStr = m.group(2);

            String[] fields = Arrays.stream(fieldsStr.split(",")).map(String::trim).toArray(String[]::new);
            String[] values = Arrays.stream(valuesStr.split(",")).map(String::trim).toArray(String[]::new);

            List<String> pairs = new ArrayList<>();
            for (int i = 0; i < Math.min(fields.length, values.length); i++) {
                String fieldCamel = toCamelCase(fields[i]);
                String prettyVal  = normalizeConstraintValue(values[i]);
                pairs.add(fieldCamel + "=" + prettyVal);
            }
            return "Já existe um registro com os mesmos valores para os campos únicos: (" + String.join(", ", pairs) + ").";
        }
        // fallback se não conseguiu parsear
        return "Já existe um registro com os mesmos valores para os campos únicos.";
    }







    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<Map<String, String>> handleJwtExceptions(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("detail", "Token inválido ou expirado"));
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("detail", "Erro de autenticação"));
    }


    @ExceptionHandler(IllegalAssignRoleException.class)
    public ResponseEntity<Map<String, String>> handleIllegalRole(IllegalAssignRoleException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("detail", ex.getMessage()));
    }


    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable t = ex;

        // 1. Checa InvalidEnumValueException
        while (t != null) {
            if (t instanceof InvalidEnumValueException enumEx) {
                return handleInvalidEnum(enumEx);
            }
            t = t.getCause();
        }

        // 2. Checa InvalidFormatException
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String field = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            String invalidValue = ife.getValue() != null ? ife.getValue().toString() : "null";

            String message;
            if (LocalDate.class.equals(ife.getTargetType())) {
                message = "Campo '" + field + "' tem valor inválido. Use o formato yyyy-MM-dd.";
            } else {
                String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "unknown";
                message = "Campo '" + field + "' tem valor inválido: " + invalidValue + ". Esperado tipo: " + targetType;
            }

            return ResponseEntity.badRequest().body(Map.of("detail", message));
        }
        // 3. Fallback genérico
        return ResponseEntity.badRequest().body(Map.of("detail", "JSON inválido."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Throwable t = ex;

        // 1. Checa InvalidEnumValueException
        while (t != null) {
            if (t instanceof InvalidEnumValueException enumEx) {
                return handleInvalidEnum(enumEx);
            }
            t = t.getCause();
        }
        return handleGenericException(ex);
    }


    // Para parâmetros ausentes na URL ou body (form-data)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        String message = String.format("O parâmetro obrigatório '%s' está ausente", ex.getParameterName());
        return new ResponseEntity<>(Map.of("detail", message), HttpStatus.BAD_REQUEST);
    }

    // Para partes ausentes no multipart/form-data
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestPart(MissingServletRequestPartException ex) {
        String message = String.format(
                "O campo obrigatório '%s' não foi enviado. Verifique os campos obrigatórios e tente novamente.",
                ex.getRequestPartName()
        );
        return new ResponseEntity<>(Map.of("detail", message), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthDenied(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("detail", "Você não possui permissão para acessar este recurso. Verifique suas permissões com gerenciador do projeto."));
}


    @ExceptionHandler(InvalidEnumValueException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEnum(InvalidEnumValueException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("validValues", ex.getValidValues());
        body.put(ex.getFieldName(), "Valor não permitido para este enum: " + ex.getInvalidValue());
        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(DetailErrorException.class)
    public ResponseEntity<Map<String, String>> handleGenericDetail(DetailErrorException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("detail", ex.getMessage()));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        System.out.println(ex.getMessage());
        System.out.println(ex.getCause());
        System.out.println(ex.getClass());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("detail", "Ocorreu um erro interno"));
    }
















}
