package io.athos.agrocore.plantmonitor.errors;

import org.springframework.http.HttpStatus;

public class AnalysisTypeMismatchException extends DetailErrorException {
    public AnalysisTypeMismatchException(Long analysisId) {
        super("O analysisType fornecido não corresponde ao tipo da análise com id " + analysisId + ". Verifique se a entidade que você deseja editar realmente possui esse tipo." , HttpStatus.BAD_REQUEST);
    }
}
